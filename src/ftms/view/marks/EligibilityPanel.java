package ftms.view.marks;

import ftms.model.EligibilityStatus;
import ftms.service.EligibilityService;
import ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class EligibilityPanel extends JPanel {
    private final EligibilityService eligibilityService;
    private final JTextField studentIdField = new JTextField(10);
    private final JTextField courseCodeField = new JTextField(10);
    private final JButton checkButton = new JButton("Check Eligibility");
    private final EligibilityTableModel tableModel = new EligibilityTableModel();

    public EligibilityPanel() {
        this(new EligibilityService());
    }

    public EligibilityPanel(EligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Exam Eligibility", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student and Course"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 8, 6, 8);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        form.add(new JLabel("Student ID:"), constraints);
        constraints.gridx = 1;
        form.add(studentIdField, constraints);
        constraints.gridx = 2;
        form.add(new JLabel("Course Code:"), constraints);
        constraints.gridx = 3;
        form.add(courseCodeField, constraints);
        constraints.gridx = 4;
        form.add(checkButton, constraints);

        JTable table = new JTable(tableModel);
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.add(title, BorderLayout.NORTH);
        header.add(form, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        checkButton.addActionListener(event -> checkEligibility());

        if (SessionManager.isUndergraduate()
                && SessionManager.getCurrentStudentId() != -1) {
            studentIdField.setText(SessionManager.getCurrentRegistrationNo());
            studentIdField.setEditable(false);
            // no auto-check — the student still types the Course Code
        }
    }

    private void checkEligibility() {
        String studentId = SessionManager.isUndergraduate()
                ? String.valueOf(SessionManager.getCurrentStudentId())
                : studentIdField.getText().trim();
        String courseCode = courseCodeField.getText().trim();
        checkButton.setEnabled(false);
        new SwingWorker<EligibilityStatus, Void>() {
            @Override
            protected EligibilityStatus doInBackground() throws SQLException {
                return eligibilityService.checkEligibility(studentId, courseCode);
            }

            @Override
            protected void done() {
                checkButton.setEnabled(true);
                try {
                    EligibilityStatus status = get();
                    tableModel.addStatus(status);
                    if (status.isEligible()) {
                        showMessage(String.format(
                                        "Eligible for the final examination (Attendance %.2f%%, CA %.2f%%).",
                                        status.getAttendancePercentage(), status.getCaPercentage()),
                                "Eligible", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showMessage(status.getFailureReason()
                                        + String.format(" (Attendance %.2f%%, CA %.2f%%).",
                                        status.getAttendancePercentage(), status.getCaPercentage()),
                                "Not Eligible", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showMessage("Eligibility check was interrupted.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (java.util.concurrent.ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    showMessage(cause == null ? exception.getMessage() : cause.getMessage(),
                            "Eligibility Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
