// #10_StreamAPI_Задание2_Курсовая Statistics

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.net.URL;
import java.net.MalformedURLException;

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
    private int humanRequests = 0;
    private int errorRequests = 0;
    private final Set<String> humanIPs = new HashSet<>();

    // Новые поля для текущего задания
    private final HashMap<Integer, Integer> visitsPerSecond = new HashMap<>();
    private final HashSet<String> refererDomains = new HashSet<>();
    private final HashMap<String, Integer> ipVisitCounts = new HashMap<>();

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

        // Проверка на бота
        boolean isBot = userAgent.isBot();
        if (!isBot) {
            humanRequests++;
            humanIPs.add(entry.getIp());

            // Пиковая посещаемость в секунду
            int second = (int) entry.getTime().toEpochSecond(java.time.ZoneOffset.UTC);
            visitsPerSecond.put(second, visitsPerSecond.getOrDefault(second, 0) + 1);

            // Сбор доменов из referer
            String referer = entry.getReferer();
            if (referer != null && !referer.isEmpty() && !referer.equals("-")) {
                try {
                    URL url = new URL(referer);
                    String domain = url.getHost();
                    refererDomains.add(domain);
                } catch (MalformedURLException e) {
                    // Игнорируем некорректные URL
                }
            }

            // Подсчет посещений по IP
            String ip = entry.getIp();
            ipVisitCounts.put(ip, ipVisitCounts.getOrDefault(ip, 0) + 1);
        }

        // Подсчет ошибок
        int code = entry.getResponseCode();
        if (code >= 400 && code < 600) {
            errorRequests++;
        }

        // Определение Yandex/Google ботов
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


    // Пиковая посещаемость в секунду (только для обычных пользователей).

    public int getPeakVisitsPerSecond() {
        return visitsPerSecond.values().stream()
                .max(Integer::compare)
                .orElse(0);
    }

    // Список доменов, ссылающихся на сайт.

    public HashSet<String> getRefererDomains() {
        return new HashSet<>(refererDomains);
    }

    // Максимальная посещаемость одним пользователем (не ботом).

    public int getMaxVisitsPerUser() {
        return ipVisitCounts.values().stream()
                .max(Integer::compare)
                .orElse(0);
    }


    public HashSet<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    public HashSet<String> getNonExistingPages() {
        return new HashSet<>(nonExistingPages);
    }

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

    public double getAverageVisitsPerHour() {
        long hours = getHoursBetween(minTime, maxTime);
        return hours == 0 ? 0 : (double) humanRequests / hours;
    }

    public double getAverageErrorRatePerHour() {
        long hours = getHoursBetween(minTime, maxTime);
        return hours == 0 ? 0 : (double) errorRequests / hours;
    }

    public double getAverageVisitsPerUser() {
        return humanIPs.isEmpty() ? 0 : (double) humanRequests / humanIPs.size();
    }

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