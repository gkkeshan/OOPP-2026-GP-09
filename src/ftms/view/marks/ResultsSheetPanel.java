package ftms.view.marks;

import ftms.model.SemesterResult;
import ftms.service.ResultSheetService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ResultsSheetPanel extends JPanel {
    private final ResultSheetService resultSheetService;
    private final JTextField studentIdField = new JTextField(10);
    private final JButton loadButton = new JButton("Load Results");
    private final JTable resultsTable = new JTable(new ResultsTableModel(List.of()));

    public ResultsSheetPanel() {
        this(new ResultSheetService());
    }

    public ResultsSheetPanel(ResultSheetService resultSheetService) {
        this.resultSheetService = resultSheetService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Student Results Sheet", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search.setBorder(BorderFactory.createTitledBorder("Find Student Results"));
        search.add(new JLabel("Student ID:"));
        search.add(studentIdField);
        search.add(loadButton);
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.add(title, BorderLayout.NORTH);
        header.add(search, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        loadButton.addActionListener(event -> loadResults());
    }

    private void loadResults() {
        String studentId = studentIdField.getText().trim();
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
                    resultsTable.setModel(new ResultsTableModel(results));
                    if (results.isEmpty()) {
                        JOptionPane.showMessageDialog(ResultsSheetPanel.this,
                                "No results were found for this student.",
                                "No Results", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showError("Loading results was interrupted.");
                } catch (java.util.concurrent.ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    showError(cause == null ? exception.getMessage() : cause.getMessage());
                }
            }
        }.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Results Error",
                JOptionPane.ERROR_MESSAGE);
    }

}
