public class ConvertFeetInches {
    public static void main(String[] args) {
        // inches — дюймы, meters — метры; counter — счётчик строк в группе
        double inches, meters;
        int counter;

        counter = 0;
        // Печатаем таблицу от 1 до 100 дюймов.
        // inches объявлена выше, поэтому в for мы присваиваем ей значение, а не объявляем заново
        for (inches = 1; inches <= 100; inches++) {
            // 1 / 39.37 — сколько метров в одном дюйме.
            // Деление int на double даёт double: 1 превращается в 1.0,
            // поэтому результат не обрезается до нуля, как было бы в int-делении
            meters = 1 / 39.37 * inches;
            // Склеиваем строку и число оператором +
            System.out.println(inches + " дюймам соответствует "
                    + meters + " метра.");
            counter++;
            // Каждые 12 строк печатаем пустую строку — так группы таблицы отделены друг от друга
            if (counter == 12) {
                System.out.println();
                counter = 0;
            }
        }
    }
}
