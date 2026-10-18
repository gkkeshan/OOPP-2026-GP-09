package ftms.model;


public enum EnrollmentStatus {
    ACTIVE("Active"),
    REPEAT("Repeat"),
    BATCH_MISSED("Batch Missed"),
    DROPPED("Dropped");

    private final String dbValue;

    EnrollmentStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static EnrollmentStatus fromDb(String value) {
        for (EnrollmentStatus s : values()) {
            if (s.dbValue.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown enrollment status: " + value);
    }

    @Override
    public String toString() {
        return dbValue;
    }
}


