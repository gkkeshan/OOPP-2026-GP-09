package ftms.service;

import ftms.model.EligibilityStatus;

import java.sql.SQLException;

public class EligibilityService {
    private final EligibilityEngine eligibilityEngine;

    public EligibilityService() {
        this(new EligibilityEngine());
    }

    public EligibilityService(EligibilityEngine eligibilityEngine) {
        this.eligibilityEngine = eligibilityEngine;
    }

    public EligibilityStatus checkEligibility(String studentId, String courseCode)
            throws SQLException {
        return eligibilityEngine.check(studentId, courseCode);
    }
}
