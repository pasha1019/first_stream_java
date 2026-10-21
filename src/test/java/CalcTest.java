// Аннотации JUnit Jupiter: @Test, @Tag, @BeforeEach, @AfterEach, @DisplayName
import org.junit.jupiter.api.*;
// Параметризованные тесты и источник данных в формате CSV
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// Потоки для подмены стандартного ввода/вывода в тестах
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
// Статические assert-методы: assertTrue, assertFalse
import static org.junit.jupiter.api.Assertions.*;

// Тесты для класса Calc: приветствие, операции + - * / % и обработка ошибок ввода
public class CalcTest {

    // Сохраняем исходные потоки ввода/вывода, чтобы вернуть их после каждого теста
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    // Буфер, в который будет перенаправлен вывод программы
    private ByteArrayOutputStream outputStream;

    // @BeforeEach — выполняется перед каждым тестом: подменяем System.out на буфер
    @BeforeEach
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    // @AfterEach — выполняется после каждого теста: возвращаем настоящие System.in и System.out
    @AfterEach
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    // Параметризованный тест: каждая строка CsvSource — это op, a, b и ожидаемая сумма
    @ParameterizedTest
    // Тег "unit": позволяет запускать только быстрые базовые тесты (./gradlew test -PincludeTags=unit)
    @Tag("unit")
    @DisplayName("Сложение двух валидных чисел")
    // Столбцы CSV сопоставляются с параметрами метода по порядку
    @CsvSource({
            "+, 5, 3, 8",                        // обычные положительные числа
            "+, -10, 4, -6",                     // отрицательное первое число
            "+, 0, 0, 0",                        // нули
            "+, 2147483647, 0, 2147483647"       // граница типа int (Integer.MAX_VALUE)
    })
    public void testAddition(String op, int a, int b, int expected) {
        // Формируем ввод построчно так, как его ожидает Scanner (имя, операция, a, затем b)
        String input = "Иван\n" + op + "\n" + a + "\n" + b + "\n";
        // ByteArrayInputStream отдаёт Scanner байты из массива — так подменяем ввод
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Calc.main(new String[]{});

        // contains ищет подстроку: тест не ломается из-за приветствия и подсказок
        assertTrue(outputStream.toString().contains("Результат = " + expected));
    }

    // Параметризованный тест: каждая строка CsvSource — это op, a, b и ожидаемая разность
    @ParameterizedTest
    // Тег "unit": позволяет запускать только быстрые базовые тесты (./gradlew test -PincludeTags=unit)
    @Tag("unit")
    @DisplayName("Вычитание двух валидных чисел")
    @CsvSource({
            "-, 5, 3, 2",                        // обычные положительные числа
            "-, -10, 4, -14",                     // отрицательное первое число
            "-, 0, 0, 0",                        // нули
            "-, 2147483647, 0, 2147483647"       // граница типа int (Integer.MAX_VALUE)
    })
    public void testSubtraction(String op, int a, int b, int expected) {
        // Формируем ввод построчно так, как его ожидает Scanner (имя, операция, a, затем b)
        String input = "Иван\n" + op + "\n" + a + "\n" + b + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Calc.main(new String[]{});

        assertTrue(outputStream.toString().contains("Результат = " + expected));
    }

    // Основные операции: столбцы op, a, b и ожидаемый результат
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Операции +, -, *, /, %")
    @CsvSource({
            "+, 10, 5, 15",                      // сложение
            "-, 10, 5, 5",                       // вычитание
            "*, 10, 5, 50",                      // умножение
            "/, 10, 3, 3",                       // целочисленное деление: 10 / 3 == 3
            "%, 10, 3, 1",                       // остаток от деления
            "/, -7, 2, -3",                      // деление усекается к нулю
            "%, -7, 2, -1",                      // знак остатка — как у делимого
            "*, 2147483647, 2, 4294967294"       // произведение считается в long
    })
    public void testOperations(String op, int a, int b, long expected) {
        String input = "Иван\n" + op + "\n" + a + "\n" + b + "\n";
        String output = runMain(input);

        assertTrue(output.contains("Результат = " + expected));
    }

    // Деление и остаток на ноль: сообщаем об ошибке и не считаем результат
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Деление на ноль → ошибка")
    @CsvSource({
            "/, 10, 0",                          // деление на ноль
            "%, 10, 0"                           // остаток от деления на ноль
    })
    public void testDivisionByZero(String op, int a, int b) {
        String input = "Иван\n" + op + "\n" + a + "\n" + b + "\n";
        String output = runMain(input);

        assertTrue(output.contains("Ошибка: деление на ноль"));
        // Результата быть не должно — программа ничего не посчитала
        assertFalse(output.contains("Результат = "));
    }

    // Неизвестная операция: сообщаем, что операция не распознана, и ничего не считаем
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Неизвестная операция → ошибка")
    @CsvSource({
            "?, 10, 5",                          // неизвестный символ
            "abc, 10, 5"                         // неизвестная строка (берём первый символ)
    })
    public void testUnknownOperation(String op, int a, int b) {
        String input = "Иван\n" + op + "\n" + a + "\n" + b + "\n";
        String output = runMain(input);

        assertTrue(output.contains("Ошибка: неизвестная операция \"" + op + "\""));
        assertFalse(output.contains("Результат = "));
    }

    // Пустая строка с операцией: charAt(0) на ней бросил бы исключение, поэтому проверяем
    @Test
    @Tag("unit")
    @DisplayName("Пустая операция → «неизвестная операция»")
    public void testEmptyOperation() {
        String output = runMain("Иван\n\n10\n5\n");

        assertTrue(output.contains("Ошибка: неизвестная операция \"\""));
        assertFalse(output.contains("Результат = "));
    }

    // Переполнение int при сложении: считаем в long, поэтому MAX_VALUE + 1 даёт 2147483648, а не MIN_VALUE.
    // Столбцы: op, a, b, expected (long — 2147483648 в int не помещается), wrong (мусор старого переполнения)
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Переполнение int при сложении → результат считается в long")
    @CsvSource({
            "+, 2147483647, 1, 2147483648, -2147483648",     // MAX_VALUE + 1
            "+, -2147483648, -1, -2147483649, 2147483647",   // MIN_VALUE - 1
            "+, -2147483648, -2147483648, -4294967296, 0"    // MIN_VALUE + MIN_VALUE
    })
    public void testAdditionOverflowCountedInLong(
            String op, int a, int b, long expected, int wrong) {
        String input = "Иван\n" + op + "\n" + a + "\n" + b + "\n";
        String output = runMain(input);

        // Правильный результат (посчитанный в long) присутствует
        assertTrue(output.contains("Результат = " + expected));
        // Мусорный результат старой реализации (переполнение int) в выводе отсутствует
        assertFalse(output.contains("Результат = " + wrong));
    }

    // Переполнение int при вычитании: MIN_VALUE - 1 даёт -2147483649, а не MAX_VALUE
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Переполнение int при вычитании → результат считается в long")
    @CsvSource({
            "-, -2147483648, 1, -2147483649, 2147483647",   // MIN_VALUE - 1
            "-, 2147483647, -1, 2147483648, -2147483648"    // MAX_VALUE - (-1)
    })
    public void testSubtractionOverflowCountedInLong(
            String op, int a, int b, long expected, int wrong) {
        String input = "Иван\n" + op + "\n" + a + "\n" + b + "\n";
        String output = runMain(input);

        assertTrue(output.contains("Результат = " + expected));
        // Мусорный результат старой реализации (переполнение int) в выводе отсутствует
        assertFalse(output.contains("Результат = " + wrong));
    }

    // Переполнение int → печатаем предупреждение, но результат всё равно выводим
    @Test
    @Tag("unit")
    @DisplayName("Переполнение int → предупреждение о выходе за диапазон")
    public void testOverflowWarningIsPrinted() {
        String output = runMain("Иван\n+\n2147483647\n1\n");

        // Предупреждение есть
        assertTrue(output.contains("Внимание: Результат не помещается в int, результат вычислен в long"));
        // И результат всё равно напечатан — программа не упала
        assertTrue(output.contains("Результат = 2147483648"));
    }

    // MIN_VALUE / -1 не помещается в int: в int это было бы ArithmeticException, а в long — обычное число
    @Test
    @Tag("unit")
    @DisplayName("MIN_VALUE / -1 → результат больше int, но считаем в long")
    public void testDivisionOverflowCountedInLong() {
        String output = runMain("Иван\n/\n-2147483648\n-1\n");

        assertTrue(output.contains("Результат = 2147483648"));
        assertTrue(output.contains("Внимание: Результат не помещается в int"));
    }

    // Обычные числа переполнения не вызывают → предупреждения нет
    @Test
    @Tag("unit")
    @DisplayName("Обычные числа → без предупреждения о переполнении")
    public void testNoOverflowWarningForNormalValues() {
        String output = runMain("Иван\n+\n5\n3\n");

        assertFalse(output.contains("Внимание"));
        assertTrue(output.contains("Результат = 8"));
    }

    // Граница int сама по себе не является переполнением → предупреждения нет
    @Test
    @Tag("unit")
    @DisplayName("Граница int без переполнения → без предупреждения")
    public void testNoOverflowWarningAtIntBoundary() {
        String output = runMain("Иван\n+\n2147483647\n0\n");

        assertFalse(output.contains("Внимание"));
        assertTrue(output.contains("Результат = 2147483647"));
    }

    // Хелпер: подменяет System.in строкой input, запускает main и возвращает перехваченный вывод
    private String runMain(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Calc.main(new String[]{});
        // toString() превращает байты буфера в строку — это и есть вывод программы
        return outputStream.toString();
    }

    // Проверка ошибки: мусор вместо числа — программа не падает, а просит ввод заново.
    // Столбцы: op, a, мусорный ввод, число после мусора, ожидаемый результат
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Не целое число → ошибка и повторный запрос")
    @CsvSource({
            "+, 6, 2.5, 7, 13",                // дробное
            "+, 0, 2147483648, 1, 1",          // вылезает за int
            "+, 0, 99999999999999999999999999, 1, 1"  // не влезает даже в long
    })
    public void testAdditionWithFloatNumber_repromptsAndContinues(
            String op, int a, String badInput, int retry, int expected) {
        String input = "Иван\n" + op + "\n" + a + "\n" + badInput + "\n" + retry + "\n";
        String output = runMain(input);

        assertTrue(output.contains("Ошибка: число вне допустимого диапазона"));
        assertTrue(output.contains("Результат = " + expected));
    }

    // Проверка ошибки: буквы вместо числа — сообщаем, что это не число, и просим ввод заново
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Буквы в числе → «не является числом» и повторный запрос")
    @CsvSource({
            "+, 10, abc, 4, 14",               // сложение: 10 + 4
            "-, 10, Abc, 4, 6",                // вычитание: 10 - 4
            "+, 10, 12abc, 4, 14"              // цифры с буквами вперемешку
    })
    public void testLettersInput_reportsNotANumber(
            String op, int a, String badInput, int retry, int expected) {
        String input = "Иван\n" + op + "\n" + a + "\n" + badInput + "\n" + retry + "\n";
        String output = runMain(input);

        assertTrue(output.contains("Ошибка: \"" + badInput + "\" не является числом"));
        // Сообщение о «числе вне диапазона» здесь быть не должно — это другой тип ошибки
        assertFalse(output.contains("Ошибка: число вне допустимого диапазона"));
        assertTrue(output.contains("Результат = " + expected));
    }

    // Одиночный сценарий: один конкретный ввод без набора параметров
    @Test
    @Tag("unit")
    @DisplayName("Приветствие с именем")
    public void testGreetingWithName() {
        String output = runMain("Иван\n+\n2\n3");
        assertTrue(output.contains("Привет, Иван"));
        assertTrue(output.contains("Давно не виделись!"));
    }

    @Test
    @Tag("unit")
    @DisplayName("Приветствие с другим именем")
    public void testGreetingWithAnotherName() {
        String output = runMain("Пётр\n+\n2\n3");
        assertTrue(output.contains("Привет, Пётр"));
        // Для неизвестного пользователя «Давно не виделись!» не печатается
        assertFalse(output.contains("Давно не виделись!"));
    }

    @Test
    @Tag("unit")
    @DisplayName("Пустое имя → гость")
    public void testGreetingEmptyName() {
        // Первая строка ввода пустая — значит, имя не введено
        String output = runMain("\n+\n2\n3");
        assertTrue(output.contains("Привет, гость!"));
    }

    @Test
    @Tag("unit")
    @DisplayName("Имя из пробела → гость")
    public void testGreetingWhitespaceName() {
        // Имя из одного пробела: после trim() строка становится пустой
        String output = runMain(" \n+\n2\n3");
        assertTrue(output.contains("Привет, гость!"));
    }
}
