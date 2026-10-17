package ftms.model;


public enum SessionType {
    THEORY("Theory"),
    PRACTICAL("Practical");

    private final String dbValue;

    SessionType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static SessionType fromDb(String value) {
        for (SessionType t : values()) {
            if (t.dbValue.equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Unknown session type: " + value);
    }

    @Override
    public String toString() {
        return dbValue;
    }
}