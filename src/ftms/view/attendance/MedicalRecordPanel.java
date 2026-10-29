


package ftms.view.attendance;

import ftms.model.Medical;
import ftms.service.MedicalService;
import ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MedicalRecordPanel extends JPanel {

    private final MedicalService medicalService = new MedicalService();
    private final boolean isTO = "TECHNICAL_OFFICER".equalsIgnoreCase(SessionManager.getCurrentUser().getRole().trim());
    private JTable table;
    private MedicalTableModel tableModel;
    private JTextField studentIdField;
    JTextField toField = new JTextField(15);

    public MedicalRecordPanel() {
        setLayout(new BorderLayout());

        studentIdField = new JTextField(10);

        JButton searchButton = new JButton("Search");
        JButton viewAllButton = new JButton("View All");
        JButton addButton = new JButton("Add Medical");
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");

        JPanel topPanel = new JPanel();

        topPanel.add(new JLabel("Student ID:"));
        topPanel.add(studentIdField);
        topPanel.add(searchButton);
        topPanel.add(viewAllButton);
        topPanel.add(addButton);

        tableModel = new MedicalTableModel(
                new java.util.ArrayList<>());

        table = new JTable(tableModel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(approveButton);
        bottomPanel.add(rejectButton);

        addButton.setVisible(isTO);
        bottomPanel.setVisible(isTO);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchMedicals());
        viewAllButton.addActionListener(e -> viewAllMedicals());
        addButton.addActionListener(e -> showAddMedicalForm());
        approveButton.addActionListener(
                e -> updateStatus("Approved"));
        rejectButton.addActionListener(
                e -> updateStatus("Rejected"));

        viewAllMedicals();
    }

    private void showAddMedicalForm() {

        JTextField studentField = new JTextField(15);
        JTextField dateField =
                new JTextField(LocalDate.now().toString(), 15);
        JTextField documentField = new JTextField(20);

        JButton browseButton = new JButton("Browse");

        JPanel documentPanel = new JPanel(new BorderLayout(5, 0));
        documentPanel.add(documentField, BorderLayout.CENTER);
        documentPanel.add(browseButton, BorderLayout.EAST);

        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            int result = chooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                documentField.setText(file.getAbsolutePath());
            }
        });

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));

        form.add(new JLabel("Student ID:"));
        form.add(studentField);

        form.add(new JLabel("TO ID:"));
        JTextField toField = new JTextField(
                String.valueOf(SessionManager.getCurrentUser().getUserID()), 15);
        toField.setEditable(false);   // locked to the logged-in TO
        form.add(toField);
        form.add(toField);

        form.add(new JLabel("Medical Date (YYYY-MM-DD):"));
        form.add(dateField);

        form.add(new JLabel("Medical Document:"));
        form.add(documentPanel);

        int choice = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add Medical Record",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int studentId = Integer.parseInt(
                    studentField.getText().trim());

            int toId = Integer.parseInt(
                    toField.getText().trim());

            LocalDate medicalDate = LocalDate.parse(
                    dateField.getText().trim());

            String document = documentField.getText().trim();

            if (studentId <= 0 || toId <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Student ID and TO ID must be positive.");
                return;
            }

            if (document.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a medical document.");
                return;
            }

            Medical medical = new Medical();

            medical.setStudentId(studentId);
            medical.setToId(toId);
            medical.setMedicalDate(medicalDate);
            medical.setSubmittedDate(LocalDate.now());
            medical.setApprovalStatus("Pending");
            medical.setDocument(document);

            boolean saved = medicalService.addMedical(medical);

            if (saved) {
                JOptionPane.showMessageDialog(
                        this,
                        "Medical record added successfully!");

                viewAllMedicals();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Medical record could not be saved.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter valid numeric Student ID and TO ID.");

        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter the medical date as YYYY-MM-DD.");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + ex.getMessage());
        }
    }

    private void searchMedicals() {
        try {
            int studentId = Integer.parseInt(
                    studentIdField.getText().trim());

            List<Medical> medicals =
                    medicalService.getByStudent(studentId);

            tableModel = new MedicalTableModel(medicals);
            table.setModel(tableModel);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid Student ID.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, e.getMessage());
        }
    }

    private void viewAllMedicals() {
        try {
            List<Medical> medicals = medicalService.getAll();

            tableModel = new MedicalTableModel(medicals);
            table.setModel(tableModel);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, e.getMessage());
        }
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Select a medical record.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int medicalId = Integer.parseInt(
                table.getModel().getValueAt(modelRow, 0).toString());

        try {
            if (medicalService.updateStatus(medicalId, status)) {
                JOptionPane.showMessageDialog(
                        this, "Status updated.");
                viewAllMedicals();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Medical record was not updated.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, e.getMessage());
        }
    }
}

