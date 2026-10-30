package ftms.service;

import ftms.dao.ResultDAO;
import ftms.model.SemesterResult;

import java.sql.SQLException;
import java.util.List;

public class ResultSheetService {
    private final ResultDAO resultDAO;
    private final GradeService gradeService;

    public ResultSheetService() {
        this(new ResultDAO(), new GradeService());
    }

    public ResultSheetService(ResultDAO resultDAO, GradeService gradeService) {
        this.resultDAO = resultDAO;
        this.gradeService = gradeService;
    }

    public List<SemesterResult> getResultsForStudent(String studentId) throws SQLException {
        return resultDAO.findByStudent(requireStudentId(studentId));
    }

    public double calculateCgpa(List<SemesterResult> results) {
        return gradeService.calculateCgpa(results);
    }

    private String requireStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        try {
            if (Integer.parseInt(studentId.trim()) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Student ID must be a positive whole number.");
        }
        return studentId.trim();
    }
}
