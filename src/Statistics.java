// #9_Задание 3_Курсовая Statistics

import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {
    private int totalRequests = 0;
    private int yandexBotRequests = 0;
    private int googleBotRequests = 0;
    private long totalTraffic = 0;
    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

    public void addEntry(LogEntry entry) {
        totalRequests++;
        totalTraffic += entry.getDataSize();

        // Обновляем временные границы
        if (minTime == null || entry.getTime().isBefore(minTime)) {
            minTime = entry.getTime();
        }
        if (maxTime == null || entry.getTime().isAfter(maxTime)) {
            maxTime = entry.getTime();
        }

        // Определяем ботов
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