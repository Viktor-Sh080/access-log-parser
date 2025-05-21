// #10_Задание_2_Курсовая Statistics

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

    // Для существующих страниц (200)
    private final HashSet<String> existingPages = new HashSet<>();
    // Для несуществующих страниц (404)
    private final HashSet<String> nonExistingPages = new HashSet<>();
    // Для статистики ОС
    private final HashMap<String, Integer> osCounts = new HashMap<>();
    // Для статистики браузеров
    private final HashMap<String, Integer> browserCounts = new HashMap<>();

    public void addEntry(LogEntry entry) {
        totalRequests++;
        totalTraffic += entry.getDataSize();

        // Обработка страниц
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        } else if (entry.getResponseCode() == 404) {
            nonExistingPages.add(entry.getPath());
        }

        // Обновление временных границ
        if (minTime == null || entry.getTime().isBefore(minTime)) {
            minTime = entry.getTime();
        }
        if (maxTime == null || entry.getTime().isAfter(maxTime)) {
            maxTime = entry.getTime();
        }

        // Обновление статистики ОС и браузеров
        UserAgent userAgent = new UserAgent(entry.getUserAgentString());
        String os = userAgent.getOsType();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);

        String browser = userAgent.getBrowser();
        browserCounts.put(browser, browserCounts.getOrDefault(browser, 0) + 1);

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

    // Методы для существующих страниц (оставлены без изменений)
    public HashSet<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    // Метод для несуществующих страниц
    public HashSet<String> getNonExistingPages() {
        return new HashSet<>(nonExistingPages);
    }

    // Метод для статистики ОС (оставлен без изменений)
    public HashMap<String, Double> getOsStatistics() {
        HashMap<String, Double> osStats = new HashMap<>();
        int totalOs = osCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (totalOs == 0) return osStats;
        osCounts.forEach((os, count) -> osStats.put(os, (double) count / totalOs));
        return osStats;
    }

    // Метод для статистики браузеров
    public HashMap<String, Double> getBrowserStatistics() {
        HashMap<String, Double> browserStats = new HashMap<>();
        int totalBrowsers = browserCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (totalBrowsers == 0) return browserStats;
        browserCounts.forEach((browser, count) ->
                browserStats.put(browser, (double) count / totalBrowsers)
        );
        return browserStats;
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