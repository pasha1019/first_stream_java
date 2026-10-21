// BigDecimal — разбирает строку как число: так отличаем «2.5» (число, но не целое) от «abc» (не число)
// Scanner — чтение стандартного ввода; Pattern — проверка текстового вида целого числа
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Calc {
    // Целое число в текстовом виде: необязательный знак + цифры
    private static final Pattern INTEGER_LITERAL = Pattern.compile("[+-]?\\d+");

    public static void main(String[] args) {
        // Scanner читает System.in построчно
        Scanner scanner = new Scanner(System.in);

        // print — без перевода строки, чтобы ответ вводился сразу после вопроса
        System.out.print("Введите имя пользователя: ");
        // nextLine() читает строку целиком, trim() убирает пробелы по краям
        String username = scanner.nextLine().trim();
        // Пустое имя (или одно пробелы) → приветствуем гостем
        if (username.isEmpty()) {
            System.out.printf("Привет, гость!%n");
        } else {
            // printf: %s — подставляет строку, %n — перевод строки
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
                // parseInt бросает NumberFormatException на нечисловом вводе
                op = Integer.parseInt(operation);
                if (op == 0 || op == 1) break; // break — выход из бесконечного цикла
                System.out.println("Нужно ввести 0 или 1");
            } catch (NumberFormatException e) {
                // Исключение перехвачено — программа продолжает работать
                System.out.println("Это не число, попробуйте снова");
            }
        }

        // Два операнда: readInt сам переспрашивает при мусорном вводе
        int a = readInt(scanner, "Введите первое число: ");
        int b = readInt(scanner, "Введите второе число: ");

        // Считаем и выводим результат выбранной операции.
        // Считаем в long: int переполняется (MAX_VALUE + 1 → MIN_VALUE), long — нет.
        // Приводим левый операнд — тогда всё выражение считается в long
        if (op == 0) {
            long sum = (long) a + b;
            printResult("Сумма чисел", sum, isIntOverflowSum(a, b));
        } else {
            long difference = (long) a - b;
            printResult("Разность чисел", difference, isIntOverflowDifference(a, b));
        }

        scanner.close();
    }

    // Проверяет, помещается ли сумма в int: addExact бросает ArithmeticException при переполнении
    private static boolean isIntOverflowSum(int a, int b) {
        try {
            Math.addExact(a, b);
            return false;
        } catch (ArithmeticException e) {
            return true;
        }
    }

    // То же для вычитания: subtractExact бросает ArithmeticException при переполнении
    private static boolean isIntOverflowDifference(int a, int b) {
        try {
            Math.subtractExact(a, b);
            return false;
        } catch (ArithmeticException e) {
            return true;
        }
    }

    // Печатает результат; если он не помещается в int — предупреждаем, но результат всё равно выводим
    private static void printResult(String label, long value, boolean overflow) {
        if (overflow) {
            System.out.printf("Внимание: %s не помещается в int, результат вычислен в long%n", label);
        }
        // long печатается без суффикса L, поэтому «Сумма чисел = 2147483647» выглядит как раньше
        System.out.println(label + " = " + value);
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
                    // Совсем не число (например, abc) — другой тип ошибки
                    System.out.printf("Ошибка: \"%s\" не является числом%n", line);
                }
                continue; // continue — следующая итерация цикла, то есть новый запрос
            }

            try {
                // Парсим как long, чтобы поймать большие числа
                long value = Long.parseLong(line);
                // Проверяем границы int
                if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                    System.out.println("Ошибка: число вне допустимого диапазона");
                    continue;
                }
                return (int) value; // сужающее приведение long → int, здесь оно безопасное
            } catch (NumberFormatException e) {
                // Сюда попадают числа длиннее long (например, 999...999)
                System.out.println("Ошибка: число вне допустимого диапазона");
            }
        }
    }

    // Проверяет, является ли строка числом в любом виде (в том числе дробным)
    private static boolean isNumber(String line) {
        try {
            new BigDecimal(line); // конструктор бросает NumberFormatException на не-числе
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
