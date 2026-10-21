

package ftms.view.attendance;

import ftms.model.Medical;
import ftms.service.MedicalService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MedicalRecordPanel extends JPanel {

    private MedicalService medicalService = new MedicalService();
    private JTable table;
    private MedicalTableModel tableModel;
    private JTextField studentIdField;

    public MedicalRecordPanel() {

        setLayout(new BorderLayout());

        studentIdField = new JTextField(10);

        JButton searchButton = new JButton("Search");
        JButton viewAllButton = new JButton("View All");
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");

        JPanel topPanel = new JPanel();

        topPanel.add(new JLabel("Student ID:"));
        topPanel.add(studentIdField);
        topPanel.add(searchButton);
        topPanel.add(viewAllButton);

        tableModel = new MedicalTableModel(new java.util.ArrayList<>());
        table = new JTable(tableModel);

        JPanel bottomPanel = new JPanel();

        bottomPanel.add(approveButton);
        bottomPanel.add(rejectButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchMedicals());
        viewAllButton.addActionListener(e -> viewAllMedicals());
        approveButton.addActionListener(e -> updateStatus("Approved"));
        rejectButton.addActionListener(e -> updateStatus("Rejected"));
    }

    private void searchMedicals() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());
            List<Medical> medicals = medicalService.getByStudent(studentId);
            tableModel = new MedicalTableModel(medicals);
            table.setModel(tableModel);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid Student ID.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void viewAllMedicals() {
        try {
            List<Medical> medicals = medicalService.getAll();
            tableModel = new MedicalTableModel(medicals);
            table.setModel(tableModel);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a medical record.");
            return;
        }

        int medicalId = Integer.parseInt(
                table.getValueAt(row, 0).toString());

        try {
            if (medicalService.updateStatus(medicalId, status)) {
                JOptionPane.showMessageDialog(this, "Status updated.");
                viewAllMedicals();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
