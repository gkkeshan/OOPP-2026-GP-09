public class BMIResult {
    private double bmi;

    public BMIResult(double bmi) {
        this.bmi = bmi;
    }

    public double getBmi() {
        return bmi;
    }

    public String getCategory() {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obesity";
        }
    }
}