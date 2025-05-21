// #9_Задание 3_Курсовая LogEntry

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private final String ip;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int dataSize;
    private final String referer;
    private final String userAgentString;

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+) \\S+ \\S+ \\[(.+?)\\] \"(\\S+) (\\S+) HTTP/\\d\\.\\d\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*)\"");

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public LogEntry(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid log format: " + logLine);
        }

        this.ip = matcher.group(1);
        this.time = LocalDateTime.parse(matcher.group(2), DATE_FORMATTER);
        this.method = parseMethod(matcher.group(3));
        this.path = matcher.group(4);
        this.responseCode = Integer.parseInt(matcher.group(5));
        this.dataSize = Integer.parseInt(matcher.group(6));
        this.referer = "-".equals(matcher.group(7)) ? null : matcher.group(7);
        this.userAgentString = "-".equals(matcher.group(8)) ? null : matcher.group(8);
    }

    private HttpMethod parseMethod(String methodStr) {
        try {
            return HttpMethod.valueOf(methodStr);
        } catch (IllegalArgumentException e) {
            return HttpMethod.UNKNOWN;
        }
    }

    // Геттеры
    public String getIp() { return ip; }
    public LocalDateTime getTime() { return time; }
    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public int getResponseCode() { return responseCode; }
    public int getDataSize() { return dataSize; }
    public String getReferer() { return referer; }
    public String getUserAgentString() { return userAgentString; }
}