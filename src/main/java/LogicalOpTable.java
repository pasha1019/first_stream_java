// Печать таблицы истинности для операций AND, OR, XOR и NOT.
public class LogicalOpTable {
    public static void main(String[] args) {
        // Все возможные значения одной переменной: true и false.
        boolean[] values = {true, false};

        // Шапка таблицы: \t — табуляция, чтобы столбцы выровнялись.
        System.out.println("P\tQ\tAND\tOR\tXOR\tNOT");

        // Внешний цикл перебирает значения P, внутренний — значения Q.
        for (boolean p : values) {
            for (boolean q : values) {
                // Тернарный оператор (x ? 1 : 0) печатает 1 вместо true и 0 вместо false.
                System.out.print((p ? 1 : 0) + "\t" + (q ? 1 : 0) + "\t");

                // & — логическое И (true, только если оба операнда true).
                // | — логическое ИЛИ (true, если хотя бы один операнд true).
                System.out.print(((p & q) ? 1 : 0) + "\t" + ((p | q) ? 1 : 0) + "\t");

                // ^ — логическое ИСКЛЮЧАЮЩЕЕ ИЛИ (true, если значения различаются).
                // ! — логическое НЕ (инвертирует значение).
                System.out.println(((p ^ q) ? 1 : 0) + "\t" + ((!p) ? 1 : 0));
            }
        }
}
}
