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
// Исключение, которое ожидаем при вводе не-числа
import java.util.InputMismatchException;

// Статические assert-методы: assertTrue, assertThrows, assertNotNull
import static org.junit.jupiter.api.Assertions.*;

// Тесты для класса Calc: проверяем вывод при сложении и обработку ошибки ввода
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

    // Параметризованный тест: каждая строка CsvSource — это входные a, b и ожидаемая сумма
    @ParameterizedTest
    // Тег "smoke": позволяет запускать только быстрые базовые тесты (./gradlew test -PincludeTags=smoke)
    @Tag("smoke")
    @DisplayName("Сложение двух валидных чисел")
    @CsvSource({
            "5, 3, 8",                        // обычные положительные числа
            "-10, 4, -6",                     // отрицательное первое число
            "0, 0, 0",                        // нули
            "2147483647, 0, 2147483647"       // граница типа int (Integer.MAX_VALUE)
    })
    public void testAddition(int a, int b, int expected) {
        // Формируем ввод построчно так, как его ожидает Scanner (a, затем b)
        String input = "Иван\n" + a + "\n" + b + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Calc.main(new String[]{});

        // Проверяем, что в перехваченном выводе есть строка с ожидаемой суммой
        assertTrue(outputStream.toString().contains("Сумма чисел = " + expected));
    }

    private String runMain(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Calc.main(new String[]{});
        return outputStream.toString();
    }

    // Проверка ошибки: ввод не-числа (дробного)
    @Test
    @DisplayName("Проверка ошибки")
    public void testAdditionWithFloatNumbers_throwsException() {
        // "2.5" не является целым, поэтому Scanner.nextInt() должен выбросить исключение
        String input = "Иван\n6\n2.5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Ожидаем, что main выбросит InputMismatchException
        InputMismatchException exception = assertThrows(
                InputMismatchException.class,
                () -> Calc.main(new String[]{})
        );

        // Можно дополнительно проверить сообщение (зависит от версии JDK)
        assertNotNull(exception);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Приветствие с именем")
    public void testGreetingWithName() {
        String output = runMain("Иван\n1\n2\n");
        assertTrue(output.contains("Привет, Иван"));
        assertTrue(output.contains("Давно не виделись!"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Приветствие с другим именем")
    public void testGreetingWithAnotherName() {
        String output = runMain("Пётр\n1\n2\n");
        assertTrue(output.contains("Привет, Пётр"));
        assertFalse(output.contains("Давно не виделись!"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Пустое имя → гость")
    public void testGreetingEmptyName() {
        String output = runMain("\n1\n2\n");
        assertTrue(output.contains("Привет, гость!"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Имя из пробела → гость")
    public void testGreetingWhitespaceName() {
        String output = runMain(" \n1\n2\n");
        assertTrue(output.contains("Привет, гость!"));
    }
}
