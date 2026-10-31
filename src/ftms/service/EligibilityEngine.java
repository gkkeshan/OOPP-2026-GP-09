package ftms.service;

import ftms.model.AttendanceSummary;
import ftms.model.EligibilityStatus;

import java.sql.SQLException;

public class EligibilityEngine {
    private final MarkService markService;
    private final AttendanceReportService attendanceReportService;

    public EligibilityEngine() {
        this(new MarkService(), new AttendanceReportService());
    }

    public EligibilityEngine(MarkService markService,
                             AttendanceReportService attendanceReportService) {
        this.markService = markService;
        this.attendanceReportService = attendanceReportService;
    }

    public EligibilityStatus check(String studentId, String courseCode) throws SQLException {
        double caPercentage = markService.getCaPercentage(studentId, courseCode);

        // Spec: eligibility = attendance >= 80% AND CA >= 40%
        AttendanceSummary summary = attendanceReportService.getAttendanceSummary(
                Integer.parseInt(studentId.trim()), courseCode);
        double attendancePercentage = summary.getCombinedPercentage();

        return new EligibilityStatus(studentId.trim(), courseCode.trim(),
                attendancePercentage, caPercentage);
    }
}