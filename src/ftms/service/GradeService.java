package ftms.service;

import ftms.dao.GradeDAO;
import ftms.model.Grade;
import ftms.model.SemesterResult;

import java.util.List;

public class GradeService {
    private final GradeDAO gradeDAO;
    private final GradeCalculator gradeCalculator;

    public GradeService() {
        this(new GradeDAO(), new GradeCalculator());
    }

    public GradeService(GradeDAO gradeDAO, GradeCalculator gradeCalculator) {
        this.gradeDAO = gradeDAO;
        this.gradeCalculator = gradeCalculator;
    }

    public List<Grade> getGradeScale() {
        return gradeDAO.findAll();
    }

    public Grade getGrade(double marks) {
        Grade grade = gradeDAO.findByMarks(marks);
        if (grade == null) {
            throw new IllegalArgumentException("Marks must be between 0 and 100.");
        }
        return grade;
    }

    public double calculateGpa(List<Grade> grades, List<Double> credits) {
        return gradeCalculator.calculateGpa(grades, credits);
    }

    public double calculateCgpa(List<SemesterResult> results) {
        return gradeCalculator.calculateCgpa(results);
    }
}
