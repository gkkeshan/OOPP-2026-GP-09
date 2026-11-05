package ftms.service;

import ftms.dao.EnrollmentDAO;
import ftms.model.EligibilityStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EligibilityService {
    private final EligibilityEngine eligibilityEngine;
    private final EnrollmentDAO enrollmentDAO;

    public EligibilityService() {
        this(new EligibilityEngine(), new EnrollmentDAO());
    }

    public EligibilityService(EligibilityEngine eligibilityEngine) {
        this(eligibilityEngine, new EnrollmentDAO());
    }

    public EligibilityService(EligibilityEngine eligibilityEngine, EnrollmentDAO enrollmentDAO) {
        this.eligibilityEngine = eligibilityEngine;
        this.enrollmentDAO = enrollmentDAO;
    }

    public EligibilityStatus checkEligibility(String studentId, String courseCode)
            throws SQLException {
        return eligibilityEngine.check(studentId, courseCode);
    }

    public List<EligibilityStatus> getEligibilityStatuses(Integer studentId, String courseCode)
            throws SQLException {
        List<EligibilityStatus> statuses = new ArrayList<>();
        for (Map.Entry<Integer, String> target
                : enrollmentDAO.findEligibilityTargets(studentId, courseCode)) {
            statuses.add(eligibilityEngine.check(
                    String.valueOf(target.getKey()), target.getValue()));
        }
        return statuses;
    }
}
