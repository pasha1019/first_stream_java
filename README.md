# first_stream_java

Пет-проект: консольный калькулятор на Java.

`Calc.main` читает два целых числа из стандартного ввода через `Scanner` и выводит их сумму в виде `Сумма чисел = <сумма>`.

## Требования
- Java 17
- Gradle-обёртка 9.6.0 (используйте `./gradlew`, системный `gradle` не нужен)

## Запуск
Плагин `application` не подключён, поэтому задачи `./gradlew run` нет. Запускайте класс из IDE или вручную:

```bash
./gradlew build
java -cp build/classes/java/main Calc
```

## Тесты
```bash
./gradlew test                                  # все тесты
./gradlew test --tests CalcTest                 # один класс
./gradlew test -PincludeTags=smoke              # только smoke-тесты
```

Тесты построены на JUnit 5 и подменяют `System.in`/`System.out`, чтобы управлять вводом и проверять вывод `Calc.main`.
