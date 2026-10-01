public class BMIDemo {

    public static void main(String[] args) {
        Person person = new Person(70.0, 1.75); // weight in kg, height in metres

        BMICalculator calculator = new AdultBMICalculator();
        double bmi = calculator.calculateBMI(person);

        System.out.println("BMI: " + bmi);
        System.out.println("Category: " + calculator.getCategory(bmi));
    }
}
