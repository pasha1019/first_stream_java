import java.util.Scanner; // 1. Импортируем класс Scanner

public class Calc {
    public static void main(String[] args) {
        // 2. Создаем объект Scanner и передаем в него System.in
        Scanner scanner = new Scanner(System.in);
        System.out.println("Сложение чисел");
        System.out.print("Введите первое число: ");
        int a = scanner.nextInt(); // считывает первое число

        System.out.print("Введите второе число: ");
        int b = scanner.nextInt(); // считывает второе число

        int result = a + b;
        System.out.println("Сумма чисел = " + result);

        scanner.close(); // закрываем сканер по окончании работы
    }
}
