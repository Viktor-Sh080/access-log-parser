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

        // Бесконечный цикл опроса пути к файлу
        while (true) {
            System.out.print("Укажите путь к файлу: ");

            String path = new Scanner(System.in).nextLine(); // Получаем введённый путь от пользователя

            File file = new File(path); // Создаем объект класса File для проверки существования файла

            if (!file.exists()) { // Если файл не существует
                System.out.println("Файл не найден!!!");
                continue; // Продолжаем цикл заново
            }

            if (file.isDirectory()) { // Если путь ведет к директории (папке)
                System.out.println("Указанный путь ведёт к папке, укажите путь к файлу!!!");
                continue; // Продолжаем цикл заново
            }

            // Иначе (файл существует и не является директорией):
            validFilesCount++; // Увеличиваем счётчик правильно введённых файлов
            System.out.println("Путь указан верно");
            System.out.printf("Это файл номер %d\n", validFilesCount); // Выводим номер файла

            try (FileReader fileReader = new FileReader(path);
                 BufferedReader reader = new BufferedReader(fileReader)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    totalRequests++;

                    // Проверяем длину строки
                    if (line.length() > 1024) {
                        throw new LongLineException("Строка длиннее 1024 символов");
                    }

                    // Обработка User-Agent
                    int uaPosition = line.lastIndexOf('"'); // Находим последнее вхождение "
                    if (uaPosition != -1) {
                        String userAgent = line.substring(uaPosition + 1, line.length()); // Вырезаем User-Agent

                        // Находим круглые скобки в User-Agent
                        int startBracketIndex = userAgent.indexOf('(');
                        int endBracketIndex = userAgent.indexOf(')', startBracketIndex);

                        // Проверяем корректность индексов
                        if (startBracketIndex != -1 && endBracketIndex != -1 &&
                                startBracketIndex < endBracketIndex) {
                            String firstBracketsContent = userAgent.substring(startBracketIndex + 1, endBracketIndex);

                            // Разбираем содержание по точке с запятой
                            String[] parts = firstBracketsContent.split(";");
                            if (parts.length >= 2) {
                                String fragment = parts[1].trim();

                                // Берём фрагмент до "/"
                                int slashIndex = fragment.indexOf("/");
                                if (slashIndex != -1) {
                                    String botType = fragment.substring(0, slashIndex).trim();

                                    // Подсчитываем запросы от YandexBot и Googlebot
                                    if ("YandexBot".equals(botType)) {
                                        yandexBotRequests++;
                                    } else if ("Googlebot".equals(botType)) {
                                        googleBotRequests++;
                                    }
                                }
                            }
                        }
                    }
                }

                // Выводим долю запросов от YandexBot и Googlebot
                if (totalRequests > 0) {
                    double yandexPercentage = (double) yandexBotRequests / totalRequests * 100;
                    double googlePercentage = (double) googleBotRequests / totalRequests * 100;

                    System.out.printf("Процент запросов от YandexBot: %.2f%%\n", yandexPercentage);
                    System.out.printf("Процент запросов от Googlebot: %.2f%%\n", googlePercentage);
                }

            } catch (LongLineException e) {
                System.out.println("Ошибка: " + e.getMessage());
                break; // Прервать выполнение программы
            } catch (IOException e) {
                e.printStackTrace(); // выводим трассировку стека в случае ошибки
            }
        }
    }
}