package ftms.view.marks;

import ftms.model.SemesterResult;
import ftms.service.ResultSheetService;
import ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GpaDashboardPanel extends JPanel {
    private final ResultSheetService resultSheetService;
    private final JTextField studentIdField = new JTextField(10);
    private final JButton loadButton = new JButton("View GPA");
    private final JLabel sgpaValue = new JLabel("-");
    private final JLabel cgpaValue = new JLabel("-");
    private final JTable historyTable = new JTable(new ResultsTableModel(List.of()));

    public GpaDashboardPanel() {
        this(new ResultSheetService());
    }

    public GpaDashboardPanel(ResultSheetService resultSheetService) {
        this.resultSheetService = resultSheetService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("GPA Dashboard", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search.setBorder(BorderFactory.createTitledBorder("Student"));
        search.add(new JLabel("Student ID:"));
        search.add(studentIdField);
        search.add(loadButton);

        JPanel summary = new JPanel(new GridLayout(1, 2, 12, 0));
        summary.add(createMetricPanel("Latest SGPA", sgpaValue));
        summary.add(createMetricPanel("Cumulative GPA", cgpaValue));

        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.add(title, BorderLayout.NORTH);
        header.add(search, BorderLayout.CENTER);
        header.add(summary, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        loadButton.addActionListener(event -> loadGpa());

        if (SessionManager.isUndergraduate()
                && SessionManager.getCurrentStudentId() != -1) {
            studentIdField.setText(SessionManager.getCurrentRegistrationNo());
            studentIdField.setEditable(false);
            loadGpa();
        }
    }

    private JPanel createMetricPanel(String title, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        value.setHorizontalAlignment(SwingConstants.CENTER);
        value.setFont(value.getFont().deriveFont(Font.BOLD, 22f));
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    private void loadGpa() {
        String studentId = SessionManager.isUndergraduate()
                ? String.valueOf(SessionManager.getCurrentStudentId())
                : studentIdField.getText().trim();
        loadButton.setEnabled(false);
        new SwingWorker<List<SemesterResult>, Void>() {
            @Override
            protected List<SemesterResult> doInBackground() throws SQLException {
                return resultSheetService.getResultsForStudent(studentId);
            }

            @Override
            protected void done() {
                loadButton.setEnabled(true);
                try {
                    List<SemesterResult> results = get();
                    historyTable.setModel(new ResultsTableModel(results));
                    double cgpa = resultSheetService.calculateCgpa(results);
                    cgpaValue.setText(String.format("%.2f", cgpa));
                    sgpaValue.setText(results.isEmpty() ? "-"
                            : String.format("%.2f", results.get(results.size() - 1).getSgpa()));
                    if (results.isEmpty()) {
                        JOptionPane.showMessageDialog(GpaDashboardPanel.this,
                                "No GPA records were found for this student.",
                                "No Results", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showError("Loading GPA records was interrupted.");
                } catch (java.util.concurrent.ExecutionException
                         | IllegalArgumentException exception) {
                    Throwable cause = exception instanceof java.util.concurrent.ExecutionException
                            ? ((java.util.concurrent.ExecutionException) exception).getCause()
                            : exception;
                    showError(cause == null ? exception.getMessage() : cause.getMessage());
                }
            }
        }.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "GPA Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GPA Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new GpaDashboardPanel());
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
