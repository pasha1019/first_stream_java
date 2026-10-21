import java.util.Scanner;


public class Weight0nMoon {
    public static void main(String[] args) {
        // weight_earth — вес на Земле, weight_moon — на Луне.
        // double, потому что вес может быть дробным (например, 70.5)
        double weight_moon, weight_earth;

        System.out.println("Введите вес на земле, кг");

        // Scanner читает числа из System.in
        Scanner scanner = new Scanner(System.in);
        // nextDouble() читает число сразу: пробелы вокруг него допустимы.
        // Если ввести не число (например, "abc"), метод бросит InputMismatchException
        weight_earth = scanner.nextDouble();

        // На Луне сила притяжения примерно в 6 раз меньше, поэтому вес — около 17% земного
        weight_moon = weight_earth * 0.17;

        // Склеиваем строку и число оператором +
        System.out.println("Вес на луне = " + weight_moon);
    }
}
