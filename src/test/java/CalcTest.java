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

    // Перед каждым тестом подменяем System.out на буфер, перехватывая вывод Calc.main
    @BeforeEach
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    // После каждого теста возвращаем реальные System.in и System.out
    @AfterEach
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    // Параметризованный тест: каждая строка CsvSource — это op, a, b и ожидаемая сумма
    @ParameterizedTest
    // Тег "smoke": позволяет запускать только быстрые базовые тесты (./gradlew test -PincludeTags=smoke)
    @Tag("smoke")
    @DisplayName("Сложение двух валидных чисел")
    @CsvSource({
            "0, 5, 3, 8",                        // обычные положительные числа
            "0, -10, 4, -6",                     // отрицательное первое число
            "0, 0, 0, 0",                        // нули
            "0, 2147483647, 0, 2147483647"       // граница типа int (Integer.MAX_VALUE)
    })
    public void testAddition(int op, int a, int b, int expected) {
        // Формируем ввод построчно так, как его ожидает Scanner (имя, op, a, затем b)
        String input = "Иван\n" + op +"\n" + a + "\n" + b + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Calc.main(new String[]{});

        // Проверяем, что в перехваченном выводе есть строка с ожидаемой суммой
        assertTrue(outputStream.toString().contains("Сумма чисел = " + expected));
    }

    // Параметризованный тест: каждая строка CsvSource — это op, a, b и ожидаемая разность
    @ParameterizedTest
    // Тег "smoke": позволяет запускать только быстрые базовые тесты (./gradlew test -PincludeTags=smoke)
    @Tag("smoke")
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

        // Проверяем, что в перехваченном выводе есть строка с ожидаемой разностью
        assertTrue(outputStream.toString().contains("Разность чисел = " + expected));
    }

    // Хелпер: подменяет System.in строкой input, запускает main и возвращает перехваченный вывод
    private String runMain(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Calc.main(new String[]{});
        return outputStream.toString();
    }

    // Проверка ошибки: мусор вместо числа — программа не падает, а просит ввод заново
    @ParameterizedTest
    @Tag("smoke")
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
        String prefix = (op == 0) ? "Сумма чисел = " : "Разность чисел = ";
        assertTrue(output.contains(prefix + expected));
    }

    // Проверка ошибки: буквы вместо числа — сообщаем, что это не число, и просим ввод заново
    @ParameterizedTest
    @Tag("smoke")
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
        assertFalse(output.contains("Ошибка: число вне допустимого диапазона"));
        String prefix = (op == 0) ? "Сумма чисел = " : "Разность чисел = ";
        assertTrue(output.contains(prefix + expected));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Приветствие с именем")
    public void testGreetingWithName() {
        String output = runMain("Иван\n1\n2\n3");
        assertTrue(output.contains("Привет, Иван"));
        assertTrue(output.contains("Давно не виделись!"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Приветствие с другим именем")
    public void testGreetingWithAnotherName() {
        String output = runMain("Пётр\n1\n2\n3");
        assertTrue(output.contains("Привет, Пётр"));
        assertFalse(output.contains("Давно не виделись!"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Пустое имя → гость")
    public void testGreetingEmptyName() {
        String output = runMain("\n1\n2\n3");
        assertTrue(output.contains("Привет, гость!"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Имя из пробела → гость")
    public void testGreetingWhitespaceName() {
        String output = runMain(" \n1\n2\n3");
        assertTrue(output.contains("Привет, гость!"));
    }
}
