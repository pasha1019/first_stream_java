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

// Тесты для класса Calc: приветствие, сложение/вычитание и обработка ошибок ввода
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
            "0, 5, 3, 8",                        // обычные положительные числа
            "0, -10, 4, -6",                     // отрицательное первое число
            "0, 0, 0, 0",                        // нули
            "0, 2147483647, 0, 2147483647"       // граница типа int (Integer.MAX_VALUE)
    })
    public void testAddition(int op, int a, int b, int expected) {
        // Формируем ввод построчно так, как его ожидает Scanner (имя, op, a, затем b)
        String input = "Иван\n" + op +"\n" + a + "\n" + b + "\n";
        // ByteArrayInputStream отдаёт Scanner байты из массива — так подменяем ввод
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Calc.main(new String[]{});

        // contains ищет подстроку: тест не ломается из-за приветствия и подсказок
        assertTrue(outputStream.toString().contains("Сумма чисел = " + expected));
    }

    // Параметризованный тест: каждая строка CsvSource — это op, a, b и ожидаемая разность
    @ParameterizedTest
    // Тег "unit": позволяет запускать только быстрые базовые тесты (./gradlew test -PincludeTags=unit)
    @Tag("unit")
    @DisplayName("Вычитание двух валидных чисел")
    @CsvSource({
            "1, 5, 3, 2",                        // обычные положительные числа
            "1, -10, 4, -14",                     // отрицательное первое число
            "1, 0, 0, 0",                        // нули
            "1, 2147483647, 0, 2147483647"       // граница типа int (Integer.MAX_VALUE)
    })
    public void testSubtraction(int op, int a, int b, int expected) {
        // Формируем ввод построчно так, как его ожидает Scanner (имя, op, a, затем b)
        String input = "Иван\n" + op +"\n" + a + "\n" + b + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Calc.main(new String[]{});

        assertTrue(outputStream.toString().contains("Разность чисел = " + expected));
    }

    // Переполнение int при сложении: считаем в long, поэтому MAX_VALUE + 1 даёт 2147483648, а не MIN_VALUE.
    // Столбцы: op, a, b, expected (long — 2147483648 в int не помещается), wrong (мусор старого переполнения)
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Переполнение int при сложении → результат считается в long")
    @CsvSource({
            "0, 2147483647, 1, 2147483648, -2147483648",     // MAX_VALUE + 1
            "0, -2147483648, -1, -2147483649, 2147483647",   // MIN_VALUE - 1
            "0, -2147483648, -2147483648, -4294967296, 0"    // MIN_VALUE + MIN_VALUE
    })
    public void testAdditionOverflowCountedInLong(
            int op, int a, int b, long expected, int wrong) {
        String input = "Иван\n" + op + "\n" + a + "\n" + b + "\n";
        String output = runMain(input);

        // Правильный результат (посчитанный в long) присутствует
        assertTrue(output.contains("Сумма чисел = " + expected));
        // Мусорный результат старой реализации (переполнение int) в выводе отсутствует
        assertFalse(output.contains("Сумма чисел = " + wrong));
    }

    // Переполнение int при вычитании: MIN_VALUE - 1 даёт -2147483649, а не MAX_VALUE
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Переполнение int при вычитании → результат считается в long")
    @CsvSource({
            "1, -2147483648, 1, -2147483649, 2147483647",   // MIN_VALUE - 1
            "1, 2147483647, -1, 2147483648, -2147483648"    // MAX_VALUE - (-1)
    })
    public void testSubtractionOverflowCountedInLong(
            int op, int a, int b, long expected, int wrong) {
        String input = "Иван\n" + op + "\n" + a + "\n" + b + "\n";
        String output = runMain(input);

        assertTrue(output.contains("Разность чисел = " + expected));
        // Мусорный результат старой реализации (переполнение int) в выводе отсутствует
        assertFalse(output.contains("Разность чисел = " + wrong));
    }

    // Переполнение int → печатаем предупреждение, но результат всё равно выводим
    @Test
    @Tag("unit")
    @DisplayName("Переполнение int → предупреждение о выходе за диапазон")
    public void testOverflowWarningIsPrinted() {
        String output = runMain("Иван\n0\n2147483647\n1\n");

        // Предупреждение есть
        assertTrue(output.contains("Внимание: Сумма чисел не помещается в int, результат вычислен в long"));
        // И результат всё равно напечатан — программа не упала
        assertTrue(output.contains("Сумма чисел = 2147483648"));
    }

    // Обычные числа переполнения не вызывают → предупреждения нет
    @Test
    @Tag("unit")
    @DisplayName("Обычные числа → без предупреждения о переполнении")
    public void testNoOverflowWarningForNormalValues() {
        String output = runMain("Иван\n0\n5\n3\n");

        assertFalse(output.contains("Внимание"));
        assertTrue(output.contains("Сумма чисел = 8"));
    }

    // Граница int сама по себе не является переполнением → предупреждения нет
    @Test
    @Tag("unit")
    @DisplayName("Граница int без переполнения → без предупреждения")
    public void testNoOverflowWarningAtIntBoundary() {
        String output = runMain("Иван\n0\n2147483647\n0\n");

        assertFalse(output.contains("Внимание"));
        assertTrue(output.contains("Сумма чисел = 2147483647"));
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
            "0, 6, 2.5, 7, 13",                // дробное
            "0, 0, 2147483648, 1, 1",          // вылезает за int
            "0, 0, 99999999999999999999999999, 1, 1"  // не влезает даже в long
    })
    public void testAdditionWithFloatNumber_repromptsAndContinues(
            int op, int a, String badInput, int retry, int expected) {
        String input = "Иван\n" + op + "\n" + a + "\n" + badInput + "\n" + retry + "\n";
        String output = runMain(input);

        assertTrue(output.contains("Ошибка: число вне допустимого диапазона"));
        // Тернарный оператор: префикс зависит от операции
        String prefix = (op == 0) ? "Сумма чисел = " : "Разность чисел = ";
        assertTrue(output.contains(prefix + expected));
    }

    // Проверка ошибки: буквы вместо числа — сообщаем, что это не число, и просим ввод заново
    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Буквы в числе → «не является числом» и повторный запрос")
    @CsvSource({
            "0, 10, abc, 4, 14",               // сложение: 10 + 4
            "1, 10, Abc, 4, 6",                // вычитание: 10 - 4
            "0, 10, 12abc, 4, 14"              // цифры с буквами вперемешку
    })
    public void testLettersInput_reportsNotANumber(
            int op, int a, String badInput, int retry, int expected) {
        String input = "Иван\n" + op + "\n" + a + "\n" + badInput + "\n" + retry + "\n";
        String output = runMain(input);

        assertTrue(output.contains("Ошибка: \"" + badInput + "\" не является числом"));
        // Сообщение о «числе вне диапазона» здесь быть не должно — это другой тип ошибки
        assertFalse(output.contains("Ошибка: число вне допустимого диапазона"));
        String prefix = (op == 0) ? "Сумма чисел = " : "Разность чисел = ";
        assertTrue(output.contains(prefix + expected));
    }

    // Одиночный сценарий: один конкретный ввод без набора параметров
    @Test
    @Tag("unit")
    @DisplayName("Приветствие с именем")
    public void testGreetingWithName() {
        String output = runMain("Иван\n1\n2\n3");
        assertTrue(output.contains("Привет, Иван"));
        assertTrue(output.contains("Давно не виделись!"));
    }

    @Test
    @Tag("unit")
    @DisplayName("Приветствие с другим именем")
    public void testGreetingWithAnotherName() {
        String output = runMain("Пётр\n1\n2\n3");
        assertTrue(output.contains("Привет, Пётр"));
        // Для неизвестного пользователя «Давно не виделись!» не печатается
        assertFalse(output.contains("Давно не виделись!"));
    }

    @Test
    @Tag("unit")
    @DisplayName("Пустое имя → гость")
    public void testGreetingEmptyName() {
        // Первая строка ввода пустая — значит, имя не введено
        String output = runMain("\n1\n2\n3");
        assertTrue(output.contains("Привет, гость!"));
    }

    @Test
    @Tag("unit")
    @DisplayName("Имя из пробела → гость")
    public void testGreetingWhitespaceName() {
        // Имя из одного пробела: после trim() строка становится пустой
        String output = runMain(" \n1\n2\n3");
        assertTrue(output.contains("Привет, гость!"));
    }
}
