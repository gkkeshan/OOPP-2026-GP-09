package ftms.view.course;

import ftms.model.Course;
import ftms.model.CourseMaterial;
import ftms.service.CourseService;
import ftms.service.MaterialStorageService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialsPanel extends JPanel {

    private CourseService courseService = new CourseService();
    private MaterialStorageService materialService = new MaterialStorageService();

    private JComboBox<String> cmbCourse;
    private JTextField txtTitle;

    private DefaultTableModel tableModel;
    private JTable materialsTable;

    private List<Course> courseList;
    private List<CourseMaterial> materialList;

    public CourseMaterialsPanel() {
        setLayout(new BorderLayout(10, 10));

        // 1. Title Header
        JLabel headerLabel = new JLabel("Course Materials", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(headerLabel, BorderLayout.NORTH);

        // 2. Select Form (West)
        add(buildFormPanel(), BorderLayout.WEST);

        // 3. Materials Table (Center)
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "File", "Uploaded"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        materialsTable = new JTable(tableModel);
        add(new JScrollPane(materialsTable), BorderLayout.CENTER);

        // 4. Action Buttons (South)
        JPanel buttonPanel = new JPanel();
        JButton btnUpload = new JButton("Upload Material");
        JButton btnOpen = new JButton("Open");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");
        buttonPanel.add(btnUpload);
        buttonPanel.add(btnOpen);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        btnUpload.addActionListener(e -> onUpload());
        btnOpen.addActionListener(e -> onOpen());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> refreshMaterials());

        // 5. Load Courses
        loadCourses();
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Material Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        cmbCourse = new JComboBox<>();
        txtTitle = new JTextField(12);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Course"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbCourse, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Title"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTitle, gbc);

        return formPanel;
    }

    // 6. Actions

    private void onUpload() {
        String code = getSelectedCourseCode();
        if (code == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first");
            return;
        }
        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a title");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            materialService.saveMaterial(code, title, chooser.getSelectedFile().toPath());
            txtTitle.setText("");
            refreshMaterials();
            JOptionPane.showMessageDialog(this, "Material uploaded successfully");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Cannot save file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot upload material: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onOpen() {
        int row = materialsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material from the table first");
            return;
        }
        CourseMaterial m = materialList.get(row);
        try {
            File file = Paths.get(m.getFilePath()).toFile();
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "File not found: " + m.getFilePath(), "File Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Cannot open file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot open file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = materialsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material from the table first");
            return;
        }
        CourseMaterial m = materialList.get(row);
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete \"" + m.getTitle() + "\" and its file?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            materialService.deleteMaterial(m.getMaterialId());
            refreshMaterials();
            JOptionPane.showMessageDialog(this, "Material deleted successfully");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete material: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 7. Helpers

    private String getSelectedCourseCode() {
        int index = cmbCourse.getSelectedIndex();
        if (index < 0) {
            return null;
        }
        return courseList.get(index).getCourseCode();
    }

    private void loadCourses() {
        courseList = courseService.getAllCourses();
        for (Course c : courseList) {
            cmbCourse.addItem(c.getCourseCode() + " - " + c.getCourseName());
        }
        cmbCourse.addActionListener(e -> refreshMaterials());
        refreshMaterials();
    }

    private void refreshMaterials() {
        String code = getSelectedCourseCode();
        if (code == null) {
            materialList = new ArrayList<>();
        } else {
            materialList = materialService.getMaterialsForCourse(code);
        }
        tableModel.setRowCount(0);
        for (CourseMaterial m : materialList) {
            tableModel.addRow(new Object[]{
                    m.getMaterialId(),
                    m.getTitle(),
                    m.getFilePath(),
                    m.getUploadedDate()
            });
        }
    }

    // 8. Standalone Test Runner

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Course Materials");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new CourseMaterialsPanel());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}