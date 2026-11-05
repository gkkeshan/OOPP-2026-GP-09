package ftms.view.marks;

import ftms.model.EligibilityStatus;
import ftms.service.EligibilityService;
import ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EligibilityPanel extends JPanel {
    private final EligibilityService eligibilityService;
    private final JTextField courseCodeField = new JTextField(10);
    private final JButton checkButton = new JButton("Search");
    private final EligibilityTableModel tableModel = new EligibilityTableModel();
    private final JTable table = new JTable(tableModel);

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
        form.setBorder(BorderFactory.createTitledBorder("Filter by Course"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 8, 6, 8);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        form.add(new JLabel("Course Code:"), constraints);
        constraints.gridx = 1;
        form.add(courseCodeField, constraints);
        constraints.gridx = 2;
        form.add(checkButton, constraints);

        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.add(title, BorderLayout.NORTH);
        header.add(form, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        checkButton.addActionListener(event -> checkEligibility());

        loadEligibility();
    }

    private void checkEligibility() {
        loadEligibility();
    }

    private void loadEligibility() {
        Integer studentId = SessionManager.isUndergraduate()
                ? SessionManager.getCurrentStudentId() : null;
        String courseCode = courseCodeField.getText().trim();
        checkButton.setEnabled(false);
        new SwingWorker<List<EligibilityStatus>, Void>() {
            @Override
            protected List<EligibilityStatus> doInBackground() throws SQLException {
                return eligibilityService.getEligibilityStatuses(studentId, courseCode);
            }

            @Override
            protected void done() {
                checkButton.setEnabled(true);
                try {
                    tableModel.setStatuses(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showMessage("Eligibility check was interrupted.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (ExecutionException exception) {
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
