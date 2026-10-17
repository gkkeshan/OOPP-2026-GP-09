package ftms.model;


public abstract class BaseEntity {


    public abstract Object getId();


    public abstract String display();

    @Override
    public String toString() {
        return display();
    }


    protected static String requireText(String value, String field, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        String v = value.trim();
        if (v.length() > maxLength) {
            throw new IllegalArgumentException(field + " cannot be longer than " + maxLength + " characters");
        }
        return v;
    }

    protected static <T> T requireNotNull(T value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }

    protected static int requireInRange(int value, int min, int max, String field) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(field + " must be between " + min + " and " + max);
        }
        return value;
    }
}