import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;

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
    @CsvSource({
            "5, 3, 8",                        // обычные положительные числа
            "-10, 4, -6",                     // отрицательное первое число
            "0, 0, 0",                        // нули
            "2147483647, 0, 2147483647"       // граница типа int (Integer.MAX_VALUE)
    })
    public void testAddition(int a, int b, int expected) {
        // Формируем ввод построчно так, как его ожидает Scanner (a, затем b)
        String input = a + "\n" + b + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Calc.main(new String[]{});

        // Проверяем, что в перехваченном выводе есть строка с ожидаемой суммой
        assertTrue(outputStream.toString().contains("Сумма чисел = " + expected));
    }

    // Проверка ошибки: ввод не-числа (дробного)
    @Test
    public void testAdditionWithFloatNumbers_throwsException() {
        // "2.5" не является целым, поэтому Scanner.nextInt() должен выбросить исключение
        String input = "6\n2.5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Ожидаем, что main выбросит InputMismatchException
        InputMismatchException exception = assertThrows(
                InputMismatchException.class,
                () -> Calc.main(new String[]{})
        );

        // Можно дополнительно проверить сообщение (зависит от версии JDK)
        assertNotNull(exception);
    }
}
