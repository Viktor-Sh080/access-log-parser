// #10_Задание_1_Курсовая Statistics

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

public class Statistics {
    private int totalRequests = 0;
    private int yandexBotRequests = 0;
    private int googleBotRequests = 0;
    private long totalTraffic = 0;
    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;
    private final HashSet<String> existingPages = new HashSet<>();
    private final HashMap<String, Integer> osCounts = new HashMap<>();

    public void addEntry(LogEntry entry) {
        totalRequests++;
        totalTraffic += entry.getDataSize();

        // Добавление страницы с кодом 200
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        // Обновление временных границ
        if (minTime == null || entry.getTime().isBefore(minTime)) {
            minTime = entry.getTime();
        }
        if (maxTime == null || entry.getTime().isAfter(maxTime)) {
            maxTime = entry.getTime();
        }

        // Обновление статистики ОС
        UserAgent userAgent = new UserAgent(entry.getUserAgentString());
        String os = userAgent.getOsType();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);

        // Определение ботов
        String ua = entry.getUserAgentString();
        if (ua != null) {
            ua = ua.toLowerCase();
            if (ua.contains("yandexbot") || ua.contains("yandex.com/bots")) {
                yandexBotRequests++;
            } else if (ua.contains("googlebot") || ua.contains("google.com/bot")) {
                googleBotRequests++;
            }
        }
    }

    // Метод для получения списка существующих страниц
    public HashSet<String> getExistingPages() {
        return new HashSet<>(existingPages); // Защитная копия
    }

    // Метод для статистики ОС
    public HashMap<String, Double> getOsStatistics() {
        HashMap<String, Double> osStats = new HashMap<>();
        int total = osCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return osStats;

        osCounts.forEach((os, count) -> osStats.put(os, (double) count / total));
        return osStats;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0;
        }
        long hours = Duration.between(minTime, maxTime).toHours();
        return hours == 0 ? totalTraffic : (double) totalTraffic / hours;
    }

    // Геттеры
    public int getTotalRequests() { return totalRequests; }
    public int getYandexBotRequests() { return yandexBotRequests; }
    public int getGoogleBotRequests() { return googleBotRequests; }
    public double getYandexBotPercentage() {
        return totalRequests == 0 ? 0 : (double) yandexBotRequests / totalRequests * 100;
    }
    public double getGoogleBotPercentage() {
        return totalRequests == 0 ? 0 : (double) googleBotRequests / totalRequests * 100;
    }
}