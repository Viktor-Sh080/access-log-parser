import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int validFilesCount = 0;

        // Бесконечный цикл опроса пути к файлу
        while (true) {
            System.out.print("Укажите путь к файлу: ");

            String path = new Scanner(System.in).nextLine(); // Получаем введённый путь от пользователя

            File file = new File(path); // Создаем объект класса File для проверки существования файла

            if (!file.exists()) {      // Если файл не существует
                System.out.println("Файл не найден!!!");
                continue;              // Продолжаем цикл заново
            }

            if (file.isDirectory()) {  // Если путь ведет к директории (папке)
                System.out.println("Указанный путь ведёт к папке, укажите путь к файлу!!!");
                continue;              // Продолжаем цикл заново
            }

            // Иначе (файл существует и не является директорией):
            validFilesCount++;       // Увеличиваем счётчик правильно введённых файлов
            System.out.println("Путь указан верно");
            System.out.printf("Это файл номер %d\n", validFilesCount); // Выводим номер файла

            try (FileReader fileReader = new FileReader(path);
                 BufferedReader reader = new BufferedReader(fileReader)) {

                int countLines = 0;  // Количество строк
                int maxLength = 0;   // Максимальная длина строки
                int minLength = Integer.MAX_VALUE; // Минимальная длина строки

                String line;
                while ((line = reader.readLine()) != null) {
                    int len = line.length();

                    // Проверяем максимальную длину строки
                    if (len > 1024) {
                        throw new LongLineException("Встречена строка длиной более 1024 символов.");
                    }

                    // Обновляем минимальное и максимальное значения длины строки
                    maxLength = Math.max(maxLength, len);
                    minLength = Math.min(minLength, len);

                    countLines++;
                }

                // Выводим статистику по файлу
                System.out.println("Количество строк в файле: " + countLines);
                System.out.println("Максимальная длина строки: " + maxLength);
                System.out.println("Минимальная длина строки: " + minLength);

            } catch (LongLineException e) {
                System.out.println("Ошибка: " + e.getMessage());
                break; // Прервать выполнение программы
            } catch (IOException e) {
                e.printStackTrace(); // выводим трассировку стека в случае ошибки
            }
        }
    }
}
