package ftms.view.marks;

import ftms.model.Evaluation;
import ftms.model.Mark;
import ftms.service.MarkService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class MarksEntryPanel extends JPanel {
    private final MarkService markService;
    private final JTextField courseCodeField = new JTextField(10);
    private final JTextField studentIdField = new JTextField(8);
    private final JTextField marksField = new JTextField(7);
    private final JComboBox<Evaluation> evaluationBox = new JComboBox<>();
    private final JButton loadButton = new JButton("Load Assessments");
    private final JButton addButton = new JButton("Add Mark");
    private final JButton saveButton = new JButton("Save Changes");
    private final JTable marksTable = new JTable();
    private MarkTableModel tableModel = new MarkTableModel(List.of());
    private boolean replacingAssessments;

    public MarksEntryPanel() {
        this(new MarkService());
    }

    public MarksEntryPanel(MarkService markService) {
        this.markService = markService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Marks Entry", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBorder(BorderFactory.createTitledBorder("Assessment and Mark"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 6, 5, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        addField(controls, constraints, 0, "Course Code:", courseCodeField);
        constraints.gridx = 2;
        constraints.gridy = 0;
        controls.add(loadButton, constraints);
        addField(controls, constraints, 1, "Assessment:", evaluationBox);
        addField(controls, constraints, 2, "Student ID:", studentIdField);
        addField(controls, constraints, 3, "Marks (0-100):", marksField);
        constraints.gridx = 2;
        constraints.gridy = 3;
        controls.add(addButton, constraints);

        evaluationBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected,
                                                          boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                if (value instanceof Evaluation) {
                    Evaluation evaluation = (Evaluation) value;
                    setText(evaluation.getEvaluationName() + " (ID "
                            + evaluation.getEvaluationId() + ")");
                }
                return this;
            }
        });

        marksTable.setModel(tableModel);
        marksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.add(title, BorderLayout.NORTH);
        header.add(controls, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(marksTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(saveButton);
        add(actions, BorderLayout.SOUTH);

        loadButton.addActionListener(event -> loadAssessments());
        evaluationBox.addActionListener(event -> {
            if (!replacingAssessments) {
                loadSelectedAssessment();
            }
        });
        addButton.addActionListener(event -> addMark());
        saveButton.addActionListener(event -> saveChanges());
    }

    private void addField(JPanel panel, GridBagConstraints constraints, int row,
                          String label, Component field) {
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.weightx = 0;
        panel.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(field, constraints);
    }

    private void loadAssessments() {
        String courseCode = courseCodeField.getText().trim();
        runTask(() -> markService.getEvaluationsForCourse(courseCode), value -> {
            @SuppressWarnings("unchecked")
            List<Evaluation> evaluations = (List<Evaluation>) value;
            replacingAssessments = true;
            evaluationBox.setModel(new DefaultComboBoxModel<>(
                    evaluations.toArray(new Evaluation[0])));
            replacingAssessments = false;
            if (evaluations.isEmpty()) {
                setMarks(List.of());
            } else {
                loadSelectedAssessment();
            }
        });
    }

    private void loadSelectedAssessment() {
        Object selected = evaluationBox.getSelectedItem();
        if (!(selected instanceof Evaluation)) {
            return;
        }
        Evaluation evaluation = (Evaluation) selected;
        runTask(() -> markService.getMarksForEvaluation(evaluation.getEvaluationId()),
                value -> {
                    @SuppressWarnings("unchecked")
                    List<Mark> marks = (List<Mark>) value;
                    setMarks(marks);
                });
    }

    private void addMark() {
        Evaluation evaluation = (Evaluation) evaluationBox.getSelectedItem();
        if (evaluation == null) {
            showMessage("Load and select an assessment first.", "No Assessment",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String studentId = studentIdField.getText().trim();
        final double markValue;
        try {
            markValue = Double.parseDouble(marksField.getText().trim());
        } catch (NumberFormatException exception) {
            showMessage("Enter a numeric mark between 0 and 100.", "Invalid Mark",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        runTask(() -> markService.saveMark(
                evaluation.getEvaluationId(), studentId, markValue), ignored -> {
            marksField.setText("");
            showMessage("Mark saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSelectedAssessment();
        });
    }

    private void saveChanges() {
        if (marksTable.isEditing() && !marksTable.getCellEditor().stopCellEditing()) {
            return;
        }
        List<Mark> modifiedMarks = tableModel.getModifiedMarks();
        if (modifiedMarks.isEmpty()) {
            showMessage("There are no mark changes to save.", "No Changes",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        runTask(() -> {
            for (Mark mark : modifiedMarks) {
                if (!markService.updateMark(mark)) {
                    throw new SQLException("Mark " + mark.getMarkId() + " was not updated.");
                }
            }
            return null;
        }, ignored -> {
            tableModel.markChangesSaved();
            marksTable.repaint();
            showMessage("Mark changes saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void setMarks(List<Mark> marks) {
        tableModel = new MarkTableModel(marks);
        marksTable.setModel(tableModel);
    }

    private void runTask(Callable<?> task, Consumer<Object> onSuccess) {
        setControlsEnabled(false);
        new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                setControlsEnabled(true);
                try {
                    onSuccess.accept(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showMessage("The operation was interrupted.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (java.util.concurrent.ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    showMessage(cause == null ? exception.getMessage() : cause.getMessage(),
                            "Assessment Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void setControlsEnabled(boolean enabled) {
        loadButton.setEnabled(enabled);
        evaluationBox.setEnabled(enabled);
        addButton.setEnabled(enabled);
        saveButton.setEnabled(enabled);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
