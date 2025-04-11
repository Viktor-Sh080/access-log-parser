import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое целое число: ");
        int firstNum = new Scanner(System.in).nextInt();
        System.out.println("Введите второе целое число: ");
        int secondNum = new Scanner(System.in).nextInt();

        int amount = firstNum+secondNum;
        int difference = firstNum-secondNum;
        int multiplication = firstNum*secondNum;
        double quotient = (double) firstNum/ secondNum;

        System.out.println("Сумма чисел = " + amount);
        System.out.println("Разность чисел = " + difference);
        System.out.println("Произведение чисел = " + multiplication);
        System.out.println("Частное чисел = " + quotient);

    }
}
