public class BMICalculator {
    public double calculateBMI(User user) {
        return user.getWeight() / (user.getHeight() * user.getHeight());
    }
}