import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int validFilesCount = 0;
        int yandexBotRequests = 0;
        int googleBotRequests = 0;
        int totalRequests = 0;

        while (true) {
            System.out.print("Укажите путь к файлу: ");
            String path = new Scanner(System.in).nextLine();

            File file = new File(path);

            if (!file.exists()) {
                System.out.println("Файл не найден!!!");
                continue;
            }

            if (file.isDirectory()) {
                System.out.println("Указанный путь ведёт к папке, укажите путь к файлу!!!");
                continue;
            }

            validFilesCount++;
            System.out.println("Путь указан верно");
            System.out.printf("Это файл номер %d\n", validFilesCount);

            try (FileReader fileReader = new FileReader(path);
                 BufferedReader reader = new BufferedReader(fileReader)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    totalRequests++;

                    if (line.length() > 1024) {
                        throw new LongLineException("Строка длиннее 1024 символов");
                    }

                    // Ищем User-Agent в строке (между последними двумя кавычками)
                    int lastQuoteIndex = line.lastIndexOf('"');
                    if (lastQuoteIndex == -1) continue;

                    int prevQuoteIndex = line.lastIndexOf('"', lastQuoteIndex - 1);
                    if (prevQuoteIndex == -1) continue;

                    String userAgent = line.substring(prevQuoteIndex + 1, lastQuoteIndex).trim();
                    if (userAgent.isEmpty() || "-".equals(userAgent)) continue;

                    // Проверяем на соответствие YandexBot или Googlebot
                    if (userAgent.contains("YandexBot") ||
                            userAgent.contains("yandex.com/bots")) {
                        yandexBotRequests++;
                    } else if (userAgent.contains("Googlebot") ||
                            userAgent.contains("google.com/bot")) {
                        googleBotRequests++;
                    }
                }

                // Выводим статистику
                if (totalRequests > 0) {
                    double yandexPercentage = (double) yandexBotRequests / totalRequests * 100;
                    double googlePercentage = (double) googleBotRequests / totalRequests * 100;


                    System.out.println("\nСтатистика запросов:");
                    System.out.printf("Общее количество запросов: %d\n", totalRequests);
                    System.out.printf("Запросов от YandexBot: %d (%.2f%%)\n", yandexBotRequests, yandexPercentage);
                    System.out.printf("Запросов от Googlebot: %d (%.2f%%)\n", googleBotRequests, googlePercentage);

                } else {
                    System.out.println("Файл не содержит запросов.");
                }

            } catch (LongLineException e) {
                System.out.println("Ошибка: " + e.getMessage());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Сбрасываем счетчики для нового файла
            totalRequests = 0;
            yandexBotRequests = 0;
            googleBotRequests = 0;
        }
    }
}