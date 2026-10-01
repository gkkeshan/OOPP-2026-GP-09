public class BMICalculator {
    public double calculateBMI(User user) {
        if (user == null || user.getHeightM() <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero.");
        }

        return user.getWeightKg() / (user.getHeightM() * user.getHeightM());
    }

    public String getCategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25.0) {
            return "Normal";
        } else if (bmi < 30.0) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }
}