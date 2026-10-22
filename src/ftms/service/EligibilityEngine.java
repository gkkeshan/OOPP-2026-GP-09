package ftms.service;

import ftms.model.EligibilityStatus;

import java.sql.SQLException;

public class EligibilityEngine {
    private final MarkService markService;

    public EligibilityEngine() {
        this(new MarkService());
    }

    public EligibilityEngine(MarkService markService) {
        this.markService = markService;
    }

    public EligibilityStatus check(String studentId, String courseCode) throws SQLException {
        double caPercentage = markService.getCaPercentage(studentId, courseCode);
        return new EligibilityStatus(studentId.trim(), courseCode.trim(), caPercentage);
    }
}
