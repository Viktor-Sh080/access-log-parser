import java.util.Scanner;
import java.io.File;

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
        }
    }
}
