package ftms.view.course;

import ftms.exception.CourseNotFoundException;
import ftms.model.Batch;
import ftms.model.Course;
import ftms.service.CourseService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CourseManagementPanel extends JPanel {

    private CourseService courseService;
    private JTable courseTable;
    private CourseTableModel tableModel;

    private JTextField txtCode = new JTextField(12);
    private JTextField txtName = new JTextField(16);
    private JTextField txtLevel = new JTextField(4);
    private JTextField txtCredits = new JTextField(4);
    private JComboBox<String> cmbBatch = new JComboBox<>();
    private JCheckBox chkTheory = new JCheckBox("Theory");
    private JCheckBox chkPractical = new JCheckBox("Practical");
    private JTextField txtLecturerId = new JTextField(6);
    private JTextField txtDeptId = new JTextField(6);
    private List<Batch> batchList = new ArrayList<>();

    private JButton btnAdd = new JButton("Add");
    private JButton btnUpdate = new JButton("Update");
    private JButton btnDelete = new JButton("Delete");
    private JButton btnClear = new JButton("Clear");
    private JButton btnRefresh = new JButton("Refresh");

    private Course selectedCourse;

    public CourseManagementPanel() {
        courseService = new CourseService();
        txtLevel.setEditable(false);
        txtCredits.setEditable(false);
        loadBatches();
        txtCode.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateCalculatedFields(); }
            @Override public void removeUpdate(DocumentEvent e) { updateCalculatedFields(); }
            @Override public void changedUpdate(DocumentEvent e) { updateCalculatedFields(); }
        });
        txtDeptId.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { loadBatches(); }
            @Override public void removeUpdate(DocumentEvent e) { loadBatches(); }
            @Override public void changedUpdate(DocumentEvent e) { loadBatches(); }
        });
        setLayout(new BorderLayout(10, 10));

        // 1. Title Header
        JLabel titleLabel = new JLabel("Course Management Panel", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // 2. Input Form (left side)
        add(buildFormPanel(), BorderLayout.WEST);

        // 3. Table and ScrollPane in Center
        List<Course> courseList = courseService.getAllCourses();
        tableModel = new CourseTableModel(courseList, courseService.getBatches());
        courseTable = new JTable(tableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });
        add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // 4. Action Buttons at Bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnUpdate);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnClear);
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> refreshTable());
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Course Details"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0;
        g.gridy = 0;
        form.add(new JLabel("Course Code:"), g);
        g.gridx = 1;
        form.add(txtCode, g);

        g.gridx = 0;
        g.gridy = 1;
        form.add(new JLabel("Course Name:"), g);
        g.gridx = 1;
        form.add(txtName, g);

        g.gridx = 0;
        g.gridy = 2;
        form.add(new JLabel("Level:"), g);
        g.gridx = 1;
        form.add(txtLevel, g);

        g.gridx = 0;
        g.gridy = 3;
        form.add(new JLabel("Credits:"), g);
        g.gridx = 1;
        form.add(txtCredits, g);

        g.gridx = 0;
        g.gridy = 4;
        form.add(new JLabel("Batch:"), g);
        g.gridx = 1;
        form.add(cmbBatch, g);

        g.gridx = 0;
        g.gridy = 5;
        form.add(new JLabel("Type:"), g);
        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        typeRow.add(chkTheory);
        typeRow.add(chkPractical);
        g.gridx = 1;
        form.add(typeRow, g);

        g.gridx = 0;
        g.gridy = 6;
        form.add(new JLabel("Lecturer ID:"), g);
        g.gridx = 1;
        form.add(txtLecturerId, g);

        g.gridx = 0;
        g.gridy = 7;
        form.add(new JLabel("Department ID:"), g);
        g.gridx = 1;
        form.add(txtDeptId, g);

        return form;
    }

    private void onAdd() {
        try {
            Course course = buildCourseFromForm(null);
            courseService.addCourse(course);
            JOptionPane.showMessageDialog(this,
                    "Course saved: " + course.getCourseCode(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshTable();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this,
                    "Select a course from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Course updated = buildCourseFromForm(selectedCourse.getCourseCode());
            courseService.updateCourse(updated);
            JOptionPane.showMessageDialog(this,
                    "Course updated: " + updated.getCourseCode(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (CourseNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Not Found", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this,
                    "Select a course from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete course \"" + selectedCourse.getCourseCode() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            courseService.deleteCourse(selectedCourse.getCourseCode());
            JOptionPane.showMessageDialog(this,
                    "Course deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshTable();
        } catch (CourseNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Not Found", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Course buildCourseFromForm(String fixedCourseCode) {
        String code;
        if (fixedCourseCode != null) {
            code = fixedCourseCode;
        } else {
            code = txtCode.getText().trim().toUpperCase();
            if (code.isEmpty()) {
                throw new IllegalArgumentException("Course code cannot be empty.");
            }
        }
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty.");
        }
        int credits = Course.creditFromCode(code);
        int level = Course.levelFromCode(code);
        Batch batch = selectedBatch();
        if (batch == null) {
            throw new IllegalArgumentException("Please select a batch.");
        }
        int lecturerId = parseNumber(txtLecturerId.getText().trim(), "Lecturer ID");
        int departmentId = parseNumber(txtDeptId.getText().trim(), "Department ID");
        return new Course(code, name, credits,
                chkTheory.isSelected(), chkPractical.isSelected(),
                lecturerId, departmentId, level, batch.getBatchId());
    }

    private Batch selectedBatch() {
        int index = cmbBatch.getSelectedIndex();
        return index >= 0 && index < batchList.size() ? batchList.get(index) : null;
    }

    private void loadBatches() {
        cmbBatch.removeAllItems();
        batchList.clear();
        int departmentId = parseDepartmentId();
        List<Batch> batches = departmentId > 0
                ? courseService.getBatchesByDepartment(departmentId)
                : courseService.getBatches();
        for (Batch batch : batches) {
            batchList.add(batch);
            cmbBatch.addItem(batch.getBatchId() + " - " + batch.display() + " - "
                    + courseService.getDepartmentName(batch.getDepartmentId()));
        }
    }

    private int parseDepartmentId() {
        try {
            return Integer.parseInt(txtDeptId.getText().trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void selectBatch(int batchId) {
        for (int i = 0; i < batchList.size(); i++) {
            if (batchList.get(i).getBatchId() == batchId) {
                cmbBatch.setSelectedIndex(i);
                return;
            }
        }
        cmbBatch.setSelectedIndex(-1);
    }

    private void updateCalculatedFields() {
        String code = txtCode.getText().trim().toUpperCase();
        try {
            txtLevel.setText(String.valueOf(Course.levelFromCode(code)));
            txtCredits.setText(String.valueOf(Course.creditFromCode(code)));
        } catch (IllegalArgumentException ex) {
            txtLevel.setText("");
            txtCredits.setText("");
        }
    }

    private int parseNumber(String text, String field) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(field + " must be a number.");
        }
    }

    private void fillFormFromSelection() {
        int row = courseTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectedCourse = tableModel.getCourseAt(row);
        txtCode.setText(selectedCourse.getCourseCode());
        txtName.setText(selectedCourse.getCourseName());
        txtLevel.setText(String.valueOf(selectedCourse.getLevel()));
        txtCredits.setText(String.valueOf(selectedCourse.getCreditHours()));
        chkTheory.setSelected(selectedCourse.isHasTheory());
        chkPractical.setSelected(selectedCourse.isHasPractical());
        txtLecturerId.setText(String.valueOf(selectedCourse.getLecturerId()));
        txtDeptId.setText(String.valueOf(selectedCourse.getDepartmentId()));
        loadBatches();
        selectBatch(selectedCourse.getBatchId());
        txtCode.setEditable(false);
    }

    private void clearForm() {
        selectedCourse = null;
        txtCode.setText("");
        txtName.setText("");
        txtLevel.setText("");
        txtCredits.setText("");
        chkTheory.setSelected(false);
        chkPractical.setSelected(false);
        txtLecturerId.setText("");
        txtDeptId.setText("");
        cmbBatch.setSelectedIndex(-1);
        txtCode.setEditable(true);
        courseTable.clearSelection();
    }

    private void refreshTable() {
        try {
            tableModel = new CourseTableModel(courseService.getAllCourses(), courseService.getBatches());
            courseTable.setModel(tableModel);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load courses: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Standalone runner - test this panel individually.
    // Ignored when the panel is embedded in the main app (NavigationManager).
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Course Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new CourseManagementPanel());
            frame.setSize(1050, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}