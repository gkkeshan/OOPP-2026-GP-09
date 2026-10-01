abstract class BaseBMICalculator implements BMICalculator {
    @Override
    public double calculateBMI(Person person) {
        return person.getWeightKg()
                / (person.getHeightM() * person.getHeightM());
    }
}

class AdultBMICalculator extends BaseBMICalculator {
    @Override
    public String getCategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal weight";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }
}