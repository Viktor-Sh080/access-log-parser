// #10_StreamAPI_Задание1_Курсовая Statistics

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Statistics {
    private int totalRequests = 0;
    private int yandexBotRequests = 0;
    private int googleBotRequests = 0;
    private long totalTraffic = 0;
    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

    // Поля из предыдущих заданий
    private final HashSet<String> existingPages = new HashSet<>();
    private final HashSet<String> nonExistingPages = new HashSet<>();
    private final HashMap<String, Integer> osCounts = new HashMap<>();
    private final HashMap<String, Integer> browserCounts = new HashMap<>();

    // Новые поля для текущего задания
    private int humanRequests = 0;          // Посещения без ботов
    private int errorRequests = 0;          // Ошибки 4xx/5xx
    private final Set<String> humanIPs = new HashSet<>(); // Уникальные IP не-ботов

    // Методы для страниц
    public HashSet<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    public HashSet<String> getNonExistingPages() {
        return new HashSet<>(nonExistingPages);
    }

    // Методы для статистики
    public HashMap<String, Double> getOsStatistics() {
        HashMap<String, Double> osStats = new HashMap<>();
        int total = osCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (total > 0) {
            osCounts.forEach((os, count) -> osStats.put(os, (double) count / total));
        }
        return osStats;
    }

    public HashMap<String, Double> getBrowserStatistics() {
        HashMap<String, Double> browserStats = new HashMap<>();
        int total = browserCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (total > 0) {
            browserCounts.forEach((browser, count) ->
                    browserStats.put(browser, (double) count / total)
            );
        }
        return browserStats;
    }

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

        // Определение ботов и подсчет человеческих запросов
        boolean isBot = userAgent.isBot();
        if (!isBot) {
            humanRequests++;
            humanIPs.add(entry.getIp()); // Добавляем IP не-бота
        }

        // Подсчет ошибок 4xx/5xx
        int code = entry.getResponseCode();
        if (code >= 400 && code < 600) {
            errorRequests++;
        }

        // Определение ботов (дополнительная проверка для Yandex/Google)
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

    // Метод 1: Среднее количество посещений в час (без ботов)
    public double getAverageVisitsPerHour() {
        long hours = getHoursBetween(minTime, maxTime);
        return hours == 0 ? 0 : (double) humanRequests / hours;
    }

    // Метод 2: Среднее количество ошибочных запросов в час
    public double getAverageErrorRatePerHour() {
        long hours = getHoursBetween(minTime, maxTime);
        return hours == 0 ? 0 : (double) errorRequests / hours;
    }

    // Метод 3: Средняя посещаемость одним пользователем
    public double getAverageVisitsPerUser() {
        if (humanIPs.isEmpty()) return 0;
        return (double) humanRequests / humanIPs.size();
    }

    // Вспомогательный метод для вычисления часов
    private long getHoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || start.isAfter(end)) return 0;
        return Duration.between(start, end).toHours();
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