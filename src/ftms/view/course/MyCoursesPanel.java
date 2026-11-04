package ftms.view.course;

import ftms.model.CourseMaterial;
import ftms.model.EnrolledCourse;
import ftms.service.CourseService;
import ftms.service.MaterialStorageService;
import ftms.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class MyCoursesPanel extends JPanel {

    private final CourseService courseService = new CourseService();
    private final MaterialStorageService materialService = new MaterialStorageService();
    private EnrolledCourseTableModel tableModel;
    private JTable table;

    public MyCoursesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("My Course Details");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        tableModel = new EnrolledCourseTableModel(new java.util.ArrayList<>());
        table = new JTable(tableModel);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onViewMaterials();
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyCourses());
        JButton viewMaterialsButton = new JButton("View Materials");
        viewMaterialsButton.addActionListener(e -> onViewMaterials());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshButton);
        bottomPanel.add(viewMaterialsButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadMyCourses();
    }

    private void loadMyCourses() {
        try {
            int userId = SessionManager.getCurrentUser().getUserID();
            List<EnrolledCourse> myCourses = courseService.getEnrolledCourseDetails(userId);

            tableModel = new EnrolledCourseTableModel(myCourses);
            table.setModel(tableModel);

            if (myCourses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No enrolled courses found for your account.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage());
        }
    }

    private void onViewMaterials() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EnrolledCourse selectedCourse = tableModel.getCourseAt(row);
        String courseCode = selectedCourse.getCourseCode();
        String courseName = selectedCourse.getCourseName();

        List<CourseMaterial> materials = materialService.getMaterialsForCourse(courseCode);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Course Materials - " + courseCode, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);

        JLabel headerLabel = new JLabel("Materials for: " + courseCode + " - " + courseName, JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(headerLabel, BorderLayout.NORTH);

        DefaultTableModel materialsModel = new DefaultTableModel(new Object[]{"Title", "File", "Uploaded Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        for (CourseMaterial m : materials) {
            materialsModel.addRow(new Object[]{
                    m.getTitle(),
                    new File(m.getFilePath()).getName(),
                    m.getUploadedDate()
            });
        }

        JTable materialsTable = new JTable(materialsModel);
        dialog.add(new JScrollPane(materialsTable), BorderLayout.CENTER);

        JButton openButton = new JButton("Open Selected Material");
        JButton closeButton = new JButton("Close");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        openButton.addActionListener(e -> {
            int selectedRow = materialsTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a material to open.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            CourseMaterial selectedMaterial = materials.get(selectedRow);
            try {
                File file = Paths.get(selectedMaterial.getFilePath()).toFile();
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(dialog, "File not found: " + selectedMaterial.getFilePath(), "File Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "Cannot open file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        if (materials.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No materials available for this course.", "No Materials", JOptionPane.INFORMATION_MESSAGE);
        } else {
            dialog.setVisible(true);
        }
    }
}