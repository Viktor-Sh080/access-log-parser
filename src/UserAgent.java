// #10_StreamAPI_Задание1_Курсовая UserAgent

import java.util.Locale;

public class UserAgent {
    private final String osType;
    private final String browser;
    private final boolean isBot;

    public UserAgent(String userAgentString) {
        // Обработка пустого или некорректного User-Agent
        if (userAgentString == null || userAgentString.isEmpty() || "-".equals(userAgentString)) {
            this.osType = "Unknown";
            this.browser = "Unknown";
            this.isBot = false;
            return;
        }

        String ua = userAgentString.toLowerCase(Locale.ROOT);

        // Определение ОС
        if (ua.contains("windows")) {
            this.osType = "Windows";
        } else if (ua.contains("mac os x") || ua.contains("macintosh")) {
            this.osType = "macOS";
        } else if (ua.contains("linux")) {
            this.osType = "Linux";
        } else if (ua.contains("android")) {
            this.osType = "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            this.osType = "iOS";
        } else {
            this.osType = "Unknown";
        }

        // Определение браузера
        if (ua.contains("edge") || ua.contains("edg/")) {
            this.browser = "Edge";
        } else if (ua.contains("firefox")) {
            this.browser = "Firefox";
        } else if (ua.contains("chrome") && !ua.contains("chromium")) {
            this.browser = "Chrome";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            this.browser = "Safari";
        } else if (ua.contains("opera") || ua.contains("opr/")) {
            this.browser = "Opera";
        } else if (ua.contains("yandex")) {
            this.browser = "Yandex";
        } else {
            this.browser = "Unknown";
        }

        // Определение бота
        this.isBot = ua.contains("bot")
                || userAgentString.contains("YandexBot")
                || userAgentString.contains("Googlebot");
    }

    public String getOsType() {
        return osType;
    }

    public String getBrowser() {
        return browser;
    }

    public boolean isBot() {
        return isBot;
    }
}