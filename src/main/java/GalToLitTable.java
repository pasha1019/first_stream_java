public class GalToLitTable {
    public static void main(String[] args) {
        // gallons — галлоны, litres — литры; counter — счётчик строк в группе
        double gallons, litres;
        int counter;

        counter = 0;
        // Печатаем таблицу от 1 до 100 галлонов.
        // gallons объявлена выше, поэтому в for мы присваиваем ей значение, а не объявляем заново
        for (gallons = 1; gallons <= 100; gallons++) {
            // 3.7854 — сколько литров в одном галлоне; умножение даёт вещественный результат
            litres = gallons * 3.7854;
            // Склеиваем строку и число оператором +
            System.out.println(gallons + " галлонам соответствует "
                    + litres + " литра.");
            counter++;
            // Каждые 10 строк печатаем пустую строку — так группы таблицы отделены друг от друга
            if (counter == 10) {
                System.out.println();
                counter = 0;
            }
        }
    }
}
