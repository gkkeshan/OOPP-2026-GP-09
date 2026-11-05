package ftms.service;

import ftms.model.Grade;
import ftms.model.SemesterResult;

import java.util.List;

public class GradeCalculator {
    public double calculateGpa(List<Grade> grades, List<Double> credits) {
        if (grades == null || credits == null || grades.size() != credits.size()) {
            throw new IllegalArgumentException("Each grade must have matching course credits.");
        }

        double weightedPoints = 0;
        double totalCredits = 0;
        for (int index = 0; index < grades.size(); index++) {
            Grade grade = grades.get(index);
            Double courseCredits = credits.get(index);
            if (grade == null || courseCredits == null || !Double.isFinite(courseCredits)
                    || courseCredits <= 0) {
                throw new IllegalArgumentException("Grades and credits must be valid.");
            }
            weightedPoints += grade.getGradePoint() * courseCredits;
            totalCredits += courseCredits;
        }
        return totalCredits == 0 ? 0 : weightedPoints / totalCredits;
    }

    public double calculateCgpa(List<SemesterResult> results) {
        if (results == null) {
            throw new IllegalArgumentException("Semester results are required.");
        }

        double weightedPoints = 0;
        double totalCredits = 0;
        for (SemesterResult result : results) {
            if (result == null || !Double.isFinite(result.getSgpa())
                    || !Double.isFinite(result.getTotalCredits())
                    || result.getSgpa() < 0 || result.getSgpa() > 4
                    || result.getTotalCredits() < 0) {
                throw new IllegalArgumentException("Semester results contain invalid GPA data.");
            }
            weightedPoints += result.getSgpa() * result.getTotalCredits();
            totalCredits += result.getTotalCredits();
        }
        return totalCredits == 0 ? 0 : weightedPoints / totalCredits;
    }
}
