package ftms.service;

import ftms.dao.ExamComponentDAO;
import ftms.dao.MarkDAO;
import ftms.exception.InvalidMarkException;
import ftms.model.Evaluation;
import ftms.model.Mark;

import java.sql.SQLException;
import java.util.List;

public class MarkService {
    private final MarkDAO markDAO;
    private final ExamComponentDAO examComponentDAO;

    public MarkService() {
        this(new MarkDAO(), new ExamComponentDAO());
    }

    public MarkService(MarkDAO markDAO, ExamComponentDAO examComponentDAO) {
        this.markDAO = markDAO;
        this.examComponentDAO = examComponentDAO;
    }

    public List<Evaluation> getEvaluationsForCourse(String courseCode) throws SQLException {
        return examComponentDAO.findByCourse(requireText(courseCode, "Course code"));
    }

    public Evaluation getEvaluation(String evaluationId) throws SQLException {
        return examComponentDAO.findById(requireId(evaluationId, "Evaluation ID"));
    }

    public List<Mark> getMarksForEvaluation(String evaluationId) throws SQLException {
        return markDAO.findByEvaluation(requireId(evaluationId, "Evaluation ID"));
    }

    public List<Mark> getMarksForStudentAndCourse(String studentId, String courseCode)
            throws SQLException {
        return markDAO.findByStudentAndCourse(
                requireId(studentId, "Student ID"),
                requireText(courseCode, "Course code"));
    }

    public boolean saveMark(String evaluationId, String studentId, double marks)
            throws SQLException {
        Evaluation evaluation = requireEvaluation(evaluationId);
        validateMark(evaluation, marks);

        int parsedStudentId = Integer.parseInt(requireId(studentId, "Student ID"));
        boolean alreadyExists = markDAO.findByEvaluation(evaluation.getEvaluationId()).stream()
                .anyMatch(mark -> Integer.parseInt(mark.getStudentId()) == parsedStudentId);
        if (alreadyExists) {
            throw new IllegalArgumentException(
                    "A mark already exists for this student and assessment.");
        }

        return markDAO.insert(new Mark(null, evaluation.getEvaluationId(),
                String.valueOf(parsedStudentId), marks));
    }

    public boolean updateMark(Mark mark) throws SQLException {
        if (mark == null) {
            throw new IllegalArgumentException("A mark is required.");
        }
        Evaluation evaluation = requireEvaluation(mark.getEvaluationId());
        validateMark(evaluation, mark.getMarks());
        mark.setGrade(mark.calculateGrade());
        return markDAO.update(mark);
    }

    public boolean deleteMark(String markId) throws SQLException {
        return markDAO.delete(requireId(markId, "Mark ID"));
    }

    public double getCaPercentage(String studentId, String courseCode) throws SQLException {
        String parsedStudentId = requireId(studentId, "Student ID");
        List<Evaluation> evaluations = getEvaluationsForCourse(courseCode);
        double weightedScore = 0;
        double totalWeight = 0;

        for (Evaluation evaluation : evaluations) {
            if (!evaluation.isCA()) {
                continue;
            }
            totalWeight += evaluation.getWeight();
            for (Mark mark : markDAO.findByEvaluation(evaluation.getEvaluationId())) {
                if (parsedStudentId.equals(mark.getStudentId())) {
                    weightedScore += mark.getMarks() / evaluation.getMaxMarks()
                            * evaluation.getWeight();
                    break;
                }
            }
        }

        return totalWeight == 0 ? 0 : weightedScore / totalWeight * 100;
    }

    private Evaluation requireEvaluation(String evaluationId) throws SQLException {
        Evaluation evaluation = getEvaluation(evaluationId);
        if (evaluation == null) {
            throw new IllegalArgumentException("Assessment not found: " + evaluationId);
        }
        return evaluation;
    }

    private void validateMark(Evaluation evaluation, double marks) {
        if (!Double.isFinite(marks) || marks < 0 || marks > 100
                || marks > evaluation.getMaxMarks()) {
            throw new InvalidMarkException(
                    "Mark must be between 0 and " + Math.min(100, evaluation.getMaxMarks()));
        }
    }

    private String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " cannot be empty.");
        }
        return value.trim();
    }

    private String requireId(String value, String label) {
        String id = requireText(value, label);
        try {
            if (Integer.parseInt(id) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(label + " must be a positive whole number.");
        }
        return id;
    }
}
