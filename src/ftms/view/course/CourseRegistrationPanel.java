package ftms.view.course;

import ftms.dao.EnrollmentDAO;
import ftms.dao.UserDAO;
import ftms.model.Course;
import ftms.model.Enrollment;
import ftms.model.EnrollmentStatus;
import ftms.service.CourseService;
import ftms.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CourseRegistrationPanel extends JPanel {

    private final CourseService courseService = new CourseService();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final UserDAO userDAO = new UserDAO();

    private JTable availableCoursesTable;
    private DefaultTableModel availableCoursesModel;
    private JTable enrolledCoursesTable;
    private DefaultTableModel enrolledCoursesModel;

    private JComboBox<String> cmbAcademicYear;
    private JComboBox<String> cmbSemester;

    private List<Course> availableCourses;
    private int studentDepartmentId;
    private int studentBatchId;

    public CourseRegistrationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Load student's department and batch info
        loadStudentInfo();

        // Header
        JLabel titleLabel = new JLabel("Course Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel with two tables
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Available courses panel
        JPanel availablePanel = createAvailableCoursesPanel();
        centerPanel.add(availablePanel);

        // Enrolled courses panel
        JPanel enrolledPanel = createEnrolledCoursesPanel();
        centerPanel.add(enrolledPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with controls
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Load initial data
        loadAvailableCourses();
        loadEnrolledCourses();
    }

    private void loadStudentInfo() {
        try {
            int studentId = SessionManager.getCurrentStudentId();
            if (studentId == -1) {
                JOptionPane.showMessageDialog(this, "Student information not available. Please login again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get student's department_id and batch_id from undergraduate table
            studentDepartmentId = userDAO.getDepartmentIdByStudentId(studentId);
            studentBatchId = userDAO.getBatchIdByStudentId(studentId);

            if (studentDepartmentId == -1 || studentBatchId == -1) {
                JOptionPane.showMessageDialog(this, "Student department or batch information not found. Please contact administrator.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading student information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createAvailableCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Available Courses"));

        availableCoursesModel = new DefaultTableModel(new Object[]{"Code", "Name", "Credits", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        availableCoursesTable = new JTable(availableCoursesModel);
        availableCoursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(availableCoursesTable), BorderLayout.CENTER);

        JButton enrollButton = new JButton("Enroll in Selected Course");
        enrollButton.addActionListener(e -> enrollSelectedCourse());
        panel.add(enrollButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEnrolledCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("My Enrolled Courses"));

        enrolledCoursesModel = new DefaultTableModel(new Object[]{"Code", "Name", "Year", "Semester", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        enrolledCoursesTable = new JTable(enrolledCoursesModel);
        enrolledCoursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(enrolledCoursesTable), BorderLayout.CENTER);

        JButton dropButton = new JButton("Drop Selected Course");
        dropButton.addActionListener(e -> dropSelectedCourse());
        panel.add(dropButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Academic Year:"));
        cmbAcademicYear = new JComboBox<>(new String[]{"2023/2024", "2024/2025", "2025/2026"});
        panel.add(cmbAcademicYear);

        panel.add(new JLabel("Semester:"));
        cmbSemester = new JComboBox<>(new String[]{"Semester 1", "Semester 2"});
        panel.add(cmbSemester);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            loadAvailableCourses();
            loadEnrolledCourses();
        });
        panel.add(refreshButton);

        return panel;
    }

    private void loadAvailableCourses() {
        try {
            availableCourses = courseService.getCoursesByDepartmentAndBatch(studentDepartmentId, studentBatchId);
            availableCoursesModel.setRowCount(0);

            for (Course course : availableCourses) {
                String type = (course.isHasTheory() && course.isHasPractical()) ? "Theory + Practical"
                        : (course.isHasTheory() ? "Theory" : "Practical");
                availableCoursesModel.addRow(new Object[]{
                        course.getCourseCode(),
                        course.getCourseName(),
                        course.getCreditHours(),
                        type
                });
            }

            if (availableCourses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No courses available for your department and batch.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading available courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEnrolledCourses() {
        try {
            int studentId = SessionManager.getCurrentStudentId();
            List<Enrollment> enrollments = enrollmentDAO.findByStudent(studentId);
            enrolledCoursesModel.setRowCount(0);

            for (Enrollment enrollment : enrollments) {
                enrolledCoursesModel.addRow(new Object[]{
                        enrollment.getCourseCode(),
                        getCourseName(enrollment.getCourseCode()),
                        enrollment.getAcademicYear(),
                        enrollment.getSemester(),
                        enrollment.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading enrolled courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getCourseName(String courseCode) {
        try {
            Course course = courseService.getCourse(courseCode);
            return course != null ? course.getCourseName() : courseCode;
        } catch (Exception e) {
            return courseCode;
        }
    }

    private void enrollSelectedCourse() {
        int row = availableCoursesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Course selectedCourse = availableCourses.get(row);
        String academicYear = (String) cmbAcademicYear.getSelectedItem();
        String semester = (String) cmbSemester.getSelectedItem();

        try {
            int studentId = SessionManager.getCurrentStudentId();

            // Check if already enrolled
            if (enrollmentDAO.existsByStudentAndCourse(studentId, selectedCourse.getCourseCode())) {
                JOptionPane.showMessageDialog(this, "You are already enrolled in this course.", "Already Enrolled", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create enrollment
            Enrollment enrollment = new Enrollment(
                    studentId,
                    selectedCourse.getCourseCode(),
                    academicYear,
                    semester,
                    LocalDate.now(),
                    EnrollmentStatus.ACTIVE
            );

            if (enrollmentDAO.insert(enrollment)) {
                JOptionPane.showMessageDialog(this, "Successfully enrolled in " + selectedCourse.getCourseName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEnrolledCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to enroll in course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error enrolling in course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dropSelectedCourse() {
        int row = enrolledCoursesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course to drop.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = (String) enrolledCoursesModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to drop this course?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int studentId = SessionManager.getCurrentStudentId();
                List<Enrollment> enrollments = enrollmentDAO.findByStudentAndCourse(studentId, courseCode);

                if (!enrollments.isEmpty()) {
                    Enrollment enrollment = enrollments.get(0);
                    if (enrollmentDAO.deleteById(enrollment.getEnrollmentId())) {
                        JOptionPane.showMessageDialog(this, "Successfully dropped course.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadEnrolledCourses();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to drop course.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error dropping course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Course Registration");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new CourseRegistrationPanel());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
