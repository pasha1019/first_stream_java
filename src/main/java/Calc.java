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

        // Выбор операции: +, -, *, /, %
        System.out.print("Введите операцию (+, -, *, /, %): ");
        // Читаем строку целиком, но берём только первый символ — это char, примитивный тип
        String opLine = scanner.nextLine().trim();
        // charAt(0) на пустой строке бросил бы исключение, поэтому подставляем заведомо неизвестный символ
        char op = opLine.isEmpty() ? ' ' : opLine.charAt(0);

        // Два операнда: readInt сам переспрашивает при мусорном вводе
        int a = readInt(scanner, "Введите первое число: ");
        int b = readInt(scanner, "Введите второе число: ");

        // Считаем результат выбранной операции.
        // Считаем в long: int переполняется (MAX_VALUE + 1 → MIN_VALUE), long — нет.
        // Приводим левый операнд — тогда всё выражение считается в long
        long result;
        switch (op) {
            case '+':
                result = (long) a + b;
                break;
            case '-':
                result = (long) a - b;
                break;
            case '*':
                result = (long) a * b; // произведение переполнило бы int так же, как сумма
                break;
            case '/':
                // Проверяем делитель сами: у int деление на ноль бросило бы ArithmeticException,
                // у double получился бы Infinity — нам нужен понятный текст ошибки
                if (b == 0) {
                    System.out.println("Ошибка: деление на ноль");
                    return; // завершаем программу
                }
                // Целочисленное деление: 10 / 3 == 3, дробная часть отбрасывается
                result = (long) a / b;
                break;
            case '%':
                // Остаток от деления на ноль считать тоже нельзя
                if (b == 0) {
                    System.out.println("Ошибка: деление на ноль");
                    return;
                }
                // Знак остатка берётся у делимого: -7 % 2 == -1
                result = (long) a % b;
                break;
            default:
                // Операция не известна — сообщаем и завершаемся
                System.out.printf("Ошибка: неизвестная операция \"%s\"%n", opLine);
                return;
        }

        printResult(result);

        scanner.close();
    }

    // Печатает результат; если он не помещается в int — предупреждаем, но результат всё равно выводим
    private static void printResult(long value) {
        // Проверяем границы int: результат считался в long, но он всё равно может быть больше
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            System.out.printf("Внимание: Результат не помещается в int, результат вычислен в long%n");
        }
        // long печатается без суффикса L, поэтому «Результат = 2147483647» выглядит как обычное число
        System.out.println("Результат = " + value);
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
