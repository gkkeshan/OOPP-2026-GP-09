package ftms.view.course;

import ftms.model.Course;
import ftms.service.CourseService;
import ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyCoursesPanel extends JPanel {

    private final CourseService courseService = new CourseService();
    private CourseTableModel tableModel;
    private JTable table;

    public MyCoursesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("My Course Details");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        tableModel = new CourseTableModel(new java.util.ArrayList<>());
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyCourses());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadMyCourses();
    }

    private void loadMyCourses() {
        try {
            int userId = SessionManager.getCurrentUser().getUserID();
            List<Course> myCourses = courseService.getCoursesByStudent(userId);

            tableModel = new CourseTableModel(myCourses);
            table.setModel(tableModel);

            if (myCourses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No enrolled courses found for your account.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + ex.getMessage());
        }
    }
}