import java.math.BigDecimal;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Calc {
    // Целое число в текстовом виде: необязательный знак + цифры
    private static final Pattern INTEGER_LITERAL = Pattern.compile("[+-]?\\d+");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine().trim();
        // Пустое имя (или одно пробелы) → приветствуем гостем
        if (username.isEmpty()) {
            System.out.printf("Привет, гость!%n");
        } else {
            System.out.printf("Привет, %s!%n", username);
        }
        // Отдельное приветствие для известного пользователя
        if (username.equals("Иван")) {
            System.out.printf("Давно не виделись!%n");
        }

        // Выбор операции: 0 — сложение, 1 — вычитание; всё остальное → повторный запрос
        int op;
        while (true) {
            System.out.print("Выберите операцию: сложение(0) или вычитание(1): ");
            String operation = scanner.nextLine().trim();
            try {
                op = Integer.parseInt(operation);
                if (op == 0 || op == 1) break;
                System.out.println("Нужно ввести 0 или 1");
            } catch (NumberFormatException e) {
                System.out.println("Это не число, попробуйте снова");
            }
        }

        // Два операнда: readInt сам переспрашивает при мусорном вводе
        int a = readInt(scanner, "Введите первое число: ");
        int b = readInt(scanner, "Введите второе число: ");

        // Считаем и выводим результат выбранной операции
        int result;
        if (op == 0) {
            result = a + b;
            System.out.println("Сумма чисел = " + result);
        } else {
            result = a - b;
            System.out.println("Разность чисел = " + result);
        }

        scanner.close();
    }

    // Читает целое число из консоли с проверкой:
    // 1) это вообще число;
    // 2) оно влезает в диапазон int.
    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();

            // Ввод не похож на целое число: уточняем, число это вовсе или просто «не целое»
            if (!INTEGER_LITERAL.matcher(line).matches()) {
                if (isNumber(line)) {
                    // Число есть, но оно не целое (например, 2.5) → в int не помещается
                    System.out.println("Ошибка: число вне допустимого диапазона");
                } else {
                    System.out.printf("Ошибка: \"%s\" не является числом%n", line);
                }
                continue;
            }

            try {
                long value = Long.parseLong(line); // парсим как long, чтобы поймать большие числа
                if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                    System.out.println("Ошибка: число вне допустимого диапазона");
                    continue;
                }
                return (int) value;
            } catch (NumberFormatException e) {
                // Сюда попадают числа длиннее long (например, 999...999)
                System.out.println("Ошибка: число вне допустимого диапазона");
            }
        }
    }

    // Проверяет, является ли строка числом в любом виде (в том числе дробным)
    private static boolean isNumber(String line) {
        try {
            new BigDecimal(line);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}