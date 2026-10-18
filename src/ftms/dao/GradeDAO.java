package ftms.dao;

import ftms.model.Grade;

import java.util.List;

/** Read-only access to the grading scale; the scale is fixed data, so no insert/update/delete. */
public class GradeDAO {
    private static final List<Grade> GRADES = List.of(
            new Grade("A+", 4.00, 80, 100, true),
            new Grade("A", 3.75, 75, 79, true),
            new Grade("A-", 3.50, 70, 74, true),
            new Grade("B+", 3.25, 65, 69, true),
            new Grade("B", 3.00, 60, 64, true),
            new Grade("B-", 2.75, 55, 59, true),
            new Grade("C+", 2.50, 50, 54, true),
            new Grade("C", 2.25, 45, 49, true),
            new Grade("C-", 2.00, 40, 44, true),
            new Grade("D", 0.00, 30, 39, false),
            new Grade("E", 0.00, 0, 29, false)
    );

    public List<Grade> findAll() {
        return GRADES.stream()
                .map(grade -> new Grade(grade.getLetter(), grade.getGradePoint(),
                        grade.getMinMarks(), grade.getMaxMarks(), grade.isPass()))
                .toList();
    }

    public Grade findByMarks(double marks) {
        if (!Double.isFinite(marks) || marks < 0 || marks > 100) return null;
        int wholeMarks = (int) Math.floor(marks);
        return GRADES.stream()
                .filter(grade -> wholeMarks >= grade.getMinMarks()
                        && wholeMarks <= grade.getMaxMarks())
                .findFirst()
                .map(grade -> new Grade(grade.getLetter(), grade.getGradePoint(),
                        grade.getMinMarks(), grade.getMaxMarks(), grade.isPass()))
                .orElse(null);
    }

    public Grade findByLetter(String letter) {
        if (letter == null) return null;
        return GRADES.stream()
                .filter(grade -> grade.getLetter().equalsIgnoreCase(letter.trim()))
                .findFirst()
                .map(grade -> new Grade(grade.getLetter(), grade.getGradePoint(),
                        grade.getMinMarks(), grade.getMaxMarks(), grade.isPass()))
                .orElse(null);
    }
}
