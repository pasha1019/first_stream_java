import java.util.Scanner;

public class Calc {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.printf("Привет, гость!%n");
        } else {
            System.out.printf("Привет, %s%n!", username);
        }
        if (username.equals("Иван")) {
            System.out.printf("Давно не виделись!%n");
        }
        System.out.print("Введите первое число: ");
        int a = scanner.nextInt(); // считывает первое число

        System.out.print("Введите второе число: ");
        int b = scanner.nextInt(); // считывает второе число

        int result = a + b;
        System.out.println("Сумма чисел = " + result);
        scanner.close(); // закрываем сканер по окончании работы
    }
}
