package ftms.view.users.admin;

import ftms.model.Department;
import ftms.service.CourseService;

import javax.swing.*;
import java.awt.*;

public class DepartmentManagementPanel extends JPanel {

    private CourseService courseService = new CourseService();

    private DepartmentTableModel tableModel;
    private JTable departmentTable;

    private JTextField txtName;

    public DepartmentManagementPanel() {
        setLayout(new BorderLayout(10, 10));

        // 1. Title Header
        JLabel headerLabel = new JLabel("Department Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(headerLabel, BorderLayout.NORTH);

        // 2. Input Form (West)
        add(buildFormPanel(), BorderLayout.WEST);

        // 3. Department Table (Center)
        tableModel = new DepartmentTableModel(courseService.getDepartments());
        departmentTable = new JTable(tableModel);
        departmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            onRowSelect();
        });
        add(new JScrollPane(departmentTable), BorderLayout.CENTER);

        // 4. Action Buttons (South)
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Department");
        JButton btnUpdate = new JButton("Update Department");
        JButton btnDelete = new JButton("Delete Department");
        JButton btnRefresh = new JButton("Refresh");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> refreshTable());
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Department Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtName = new JTextField(12);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);

        return formPanel;
    }

    // 5. Actions

    private void onAdd() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a department name");
            return;
        }
        try {
            courseService.addDepartment(name);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Department added successfully");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot add department: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        int row = departmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a department from the table first");
            return;
        }
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a department name");
            return;
        }
        try {
            Department selected = tableModel.getDepartmentAt(row);
            courseService.updateDepartment(selected.getDepartmentId(), name);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Department updated successfully");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot update department: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = departmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a department from the table first");
            return;
        }
        Department selected = tableModel.getDepartmentAt(row);
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete department \"" + selected.getDepartmentName() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            courseService.deleteDepartment(selected.getDepartmentId());
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Department deleted successfully");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete department: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelect() {
        int row = departmentTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Department d = tableModel.getDepartmentAt(row);
        txtName.setText(d.getDepartmentName());
    }

    // 6. Helpers

    private void refreshTable() {
        tableModel = new DepartmentTableModel(courseService.getDepartments());
        departmentTable.setModel(tableModel);
    }

    private void clearForm() {
        txtName.setText("");
    }

    // 7. Standalone Test Runner

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Department Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new DepartmentManagementPanel());
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}