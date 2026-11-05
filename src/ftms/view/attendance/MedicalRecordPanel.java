
package ftms.view.attendance;

import ftms.model.Medical;
import ftms.service.MedicalService;
import ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MedicalRecordPanel extends JPanel {

    private MedicalService service = new MedicalService();
    private JTable table = new JTable();
    private JTextField studentIdField = new JTextField(10);

    private boolean isTO = SessionManager.getCurrentUser().getRole()
            .trim().equalsIgnoreCase("TECHNICAL_OFFICER");

    private boolean isUG = SessionManager.getCurrentUser().getRole()
            .trim().equalsIgnoreCase("UNDERGRADUATE");

    public MedicalRecordPanel() {

        setLayout(new BorderLayout());

        JButton search = new JButton("Search");
        JButton viewAll = new JButton("View All");
        JButton add = new JButton("Add Medical");
        JButton approve = new JButton("Approve");
        JButton reject = new JButton("Reject");

        JPanel top = new JPanel();
        top.add(new JLabel("Registration No:"));
        top.add(studentIdField);
        top.add(search);
        top.add(viewAll);
        top.add(add);

        JPanel bottom = new JPanel();
        bottom.add(approve);
        bottom.add(reject);

        add.addActionListener(e -> addMedical());
        search.addActionListener(e -> searchMedical());
        viewAll.addActionListener(e -> loadMedicals(null));
        approve.addActionListener(e -> updateStatus("Approved"));
        reject.addActionListener(e -> updateStatus("Rejected"));

        add.setVisible(isTO);
        bottom.setVisible(isTO);

        if (isUG) {
            try {
                studentIdField.setText(
                        SessionManager.getCurrentRegistrationNo());

                studentIdField.setEditable(false);
                viewAll.setVisible(false);

            } catch (Exception e) {
                showError(e);
            }
        }

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        if (isUG) {
            searchMedical();
        } else {
            loadMedicals(null);
        }
    }

    private void addMedical() {

        JTextField student = new JTextField(15);

        JTextField start = new JTextField(
                LocalDate.now().toString(), 15);

        JTextField end = new JTextField(
                LocalDate.now().toString(), 15);

        // Automatically set today's date
        JTextField submittedDate = new JTextField(
                LocalDate.now().toString(), 15);

        // User cannot change the submitted date
        submittedDate.setEditable(false);

        JTextField document = new JTextField(15);

        JButton browse = new JButton("Browse");

        browse.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(this)
                    == JFileChooser.APPROVE_OPTION) {

                document.setText(
                        chooser.getSelectedFile().getAbsolutePath());
            }
        });

        JPanel file = new JPanel(new BorderLayout(5, 0));
        file.add(document, BorderLayout.CENTER);
        file.add(browse, BorderLayout.EAST);

        // Five rows for five input fields
        JPanel form = new JPanel(
                new GridLayout(5, 2, 10, 12));

        form.setPreferredSize(new Dimension(400, 270));

        form.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15));

        form.add(new JLabel("Registration No:"));
        form.add(student);

        form.add(new JLabel("Start Date:"));
        form.add(start);

        form.add(new JLabel("End Date:"));
        form.add(end);

        // Display automatically generated submitted date
        form.add(new JLabel("Submitted Date:"));
        form.add(submittedDate);

        form.add(new JLabel("Medical Document:"));
        form.add(file);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add Medical Record",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String input = student.getText().trim();

            // Find student ID using registration number
            int studentId = resolveStudentId(input);

            if (studentId == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "No student found with registration no: "
                                + input);
                return;
            }

            LocalDate startDate = LocalDate.parse(
                    start.getText().trim().replaceAll(
                            "-(\\d)(-|$)", "-0$1$2"));

            LocalDate endDate = LocalDate.parse(
                    end.getText().trim().replaceAll(
                            "-(\\d)(-|$)", "-0$1$2"));

            // Get submitted date from the form
            LocalDate submitted = LocalDate.parse(
                    submittedDate.getText().trim());

            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(
                        this,
                        "End Date must be after Start Date!");
                return;
            }

            // Create Medical object
            Medical m = new Medical();

            m.setStudentId(studentId);

            m.setToId(
                    SessionManager.getCurrentUser().getUserID());

            m.setStartDate(startDate);
            m.setEndDate(endDate);

            // Save today's submitted date
            m.setSubmittedDate(submitted);

            m.setApprovalStatus("Pending");
            m.setDocument(document.getText());

            // MedicalService checks the 14-day rule
            if (service.addMedical(m)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Medical added successfully!\n"
                                + "Submitted Date: " + submitted);

                loadMedicals(null);
            }

        } catch (java.time.format.DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter dates in YYYY-MM-DD format.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void searchMedical() {

        try {

            List<Medical> records;

            if (isUG) {

                records = service.getByStudent(
                        SessionManager.getCurrentStudentId());

            } else {

                String input = studentIdField.getText().trim();

                if (input.isEmpty()) {
                    loadMedicals(null);
                    return;
                }

                int studentId = resolveStudentId(input);

                if (studentId == -1) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No student found with registration no: "
                                    + input);
                    return;
                }

                records = service.getByStudent(studentId);
            }

            loadMedicals(records);

        } catch (Exception e) {
            showError(e);
        }
    }

    // Accepts registration number or numeric student ID
    private int resolveStudentId(String input) {

        if (input.matches("\\d+")) {
            return Integer.parseInt(input);
        }

        try {
            return service.findStudentIdByRegistrationNo(input);

        } catch (Exception e) {
            showError(e);
            return -1;
        }
    }

    private void loadMedicals(List<Medical> records) {

        try {

            if (records == null) {
                records = service.getAll();
            }

            table.setModel(new MedicalTableModel(records));

        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateStatus(String status) {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Select a record!");
            return;
        }

        try {

            int id = Integer.parseInt(
                    table.getValueAt(row, 0).toString());

            service.updateStatus(id, status);

            loadMedicals(null);

            JOptionPane.showMessageDialog(
                    this, "Status updated!");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}

