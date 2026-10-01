public class User {
    private final String name;
    private final double weightKg;
    private final double heightM;

    public User(String name, double weightKg, double heightM) {
        this.name = name;
        this.weightKg = weightKg;
        this.heightM = heightM;
    }

    public String getName() {
        return name;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public double getHeightM() {
        return heightM;
    }
}