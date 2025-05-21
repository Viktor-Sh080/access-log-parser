// #10_Задание_1_КурсоваяMain.java

import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Укажите путь к файлу: ");
            String path = scanner.nextLine();

            File file = new File(path);

            if (!file.exists()) {
                System.out.println("Файл не найден!");
                continue;
            }

            if (file.isDirectory()) {
                System.out.println("Указан путь к папке!");
                continue;
            }

            Statistics stats = new Statistics();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        if (line.length() > 1024) {
                            throw new LongLineException("Строка превышает 1024 символа");
                        }

                        LogEntry entry = new LogEntry(line);
                        stats.addEntry(entry);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Ошибка разбора строки: " + e.getMessage());
                    }
                }

                printStatistics(stats);

            } catch (LongLineException e) {
                System.out.println("Ошибка: " + e.getMessage());
                break;
            } catch (IOException e) {
                System.err.println("Ошибка чтения файла: " + e.getMessage());
            }
        }
    }

    private static void printStatistics(Statistics stats) {
        System.out.println("\nСтатистика:");
        System.out.println("Всего запросов: " + stats.getTotalRequests());
        System.out.printf("Запросов от YandexBot: %d (%.2f%%)%n",
                stats.getYandexBotRequests(), stats.getYandexBotPercentage());
        System.out.printf("Запросов от Googlebot: %d (%.2f%%)%n",
                stats.getGoogleBotRequests(), stats.getGoogleBotPercentage());
        System.out.printf("Средний трафик: %.2f байт/час%n", stats.getTrafficRate());

        // Вывод списка существующих страниц
        System.out.println("\nСтраницы с кодом 200:");
        stats.getExistingPages().forEach(page -> System.out.println("- " + page));

        // Вывод статистики ОС
        System.out.println("\nДоли операционных систем:");
        stats.getOsStatistics().forEach((os, ratio) ->
                System.out.printf("%s: %.2f%%%n", os, ratio * 100));
    }
}
