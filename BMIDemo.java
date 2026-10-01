import java.util.Scanner;

public class BMIDemo {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = input.nextLine();

        System.out.print("Enter weight in kg: ");
        double weight = input.nextDouble();

        System.out.print("Enter height in meters (e.g. 1.70): ");
        double height = input.nextDouble();

        if (weight <= 0 || height <= 0) {
            System.out.println("Weight and height must be greater than zero.");
            input.close();
            return;
        }

        User user = new User(name, weight, height);
        BMICalculator calculator = new BMICalculator();
        BMIResult result = new BMIResult(calculator.calculateBMI(user));

        System.out.printf("%s, your BMI is %.2f%n", user.getName(), result.getBmi());
        System.out.println("Category: " + result.getCategory());

        input.close();
    }
}