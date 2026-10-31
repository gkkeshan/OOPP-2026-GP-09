package ftms.view.users.admin;

import ftms.exception.DatabaseException;
import ftms.model.Notice;
import ftms.service.NoticeService;
import ftms.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class NoticeManagementPanel extends JPanel {
    private final NoticeService noticeService = new NoticeService();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Title", "Audience", "Posted On"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField titleField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(4, 20);
    private static final String[] AUDIENCE_VALUES = {"ALL", "UNDERGRADUATE", "LECTURER", "TECHNICAL_OFFICER"};
    private final JComboBox<String> audienceCombo =
            new JComboBox<>(new String[]{"All", "Students", "Lecturers", "Technical Officers"});
    private List<Notice> noticeList = List.of();

    public NoticeManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- form (NORTH) ---
        JPanel titleRow = new JPanel(new BorderLayout(5, 5));
        titleRow.add(new JLabel("Title:"), BorderLayout.WEST);
        titleRow.add(titleField, BorderLayout.CENTER);

        JPanel audienceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        audienceRow.add(new JLabel("Audience:"));
        audienceRow.add(audienceCombo);

        JPanel topForm = new JPanel(new BorderLayout(5, 5));
        topForm.add(titleRow, BorderLayout.NORTH);
        topForm.add(audienceRow, BorderLayout.CENTER);

        JPanel descRow = new JPanel(new BorderLayout(5, 5));
        descRow.add(new JLabel("Description:"), BorderLayout.NORTH);
        descRow.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        descRow.setPreferredSize(new Dimension(0, 120));

        JPanel form = new JPanel(new BorderLayout(5, 5));
        form.add(topForm, BorderLayout.NORTH);
        form.add(descRow, BorderLayout.CENTER);
        add(form, BorderLayout.NORTH);

        // --- table (CENTER) ---
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- buttons (SOUTH) ---
        JButton postBtn = new JButton("Post Notice");
        JButton updateBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton clearBtn = new JButton("Clear Form");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(postBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);
        add(buttons, BorderLayout.SOUTH);

        // row click -> fill form (description comes from the list, not the table)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                if (row < noticeList.size()) {
                    Notice n = noticeList.get(row);
                    String value = n.getTarget_audience();
                    for (int i = 0; i < AUDIENCE_VALUES.length; i++) {
                        if (AUDIENCE_VALUES[i].equals(value)) {
                            audienceCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                    titleField.setText(n.getTitle());
                    descriptionArea.setText(n.getDescription());
                }
            }
        });

        postBtn.addActionListener(e -> postNotice());
        updateBtn.addActionListener(e -> updateSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        clearBtn.addActionListener(e -> clearForm());

        loadNotices();
    }

    private void loadNotices() {
        model.setRowCount(0);
        try {
            noticeList = noticeService.getAllNotices();
            for (Notice n : noticeList) {
                model.addRow(new Object[]{
                        n.getNotice_id(), n.getTitle(), n.getTarget_audience(),
                        String.valueOf(n.getDate_posted())});
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void postNotice() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Notice notice = new Notice(0, 0, title, descriptionArea.getText().trim(), new Date());
        notice.setTarget_audience(AUDIENCE_VALUES[audienceCombo.getSelectedIndex()]);
        try {
            if (noticeService.createNotice(notice, SessionManager.getCurrentUser().getUserID())) {
                JOptionPane.showMessageDialog(this, "Notice posted.");
                clearForm();
                loadNotices();
            } else {
                JOptionPane.showMessageDialog(this, "Could not post the notice.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelected() {
        int noticeId = selectedNoticeId();
        if (noticeId == -1) return;
        Notice notice = new Notice(noticeId, 0, titleField.getText().trim(),
                descriptionArea.getText().trim(), null);
        notice.setTarget_audience(AUDIENCE_VALUES[audienceCombo.getSelectedIndex()]);
        try {
            if (noticeService.updateNotice(notice)) {
                JOptionPane.showMessageDialog(this, "Notice updated.");
                loadNotices();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Could not update the notice.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int noticeId = selectedNoticeId();
        if (noticeId == -1) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete this notice permanently?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (noticeService.deleteNotice(noticeId)) {
                loadNotices();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete the notice.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int selectedNoticeId() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a notice in the table first.", "Validation", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        int row = table.convertRowIndexToModel(viewRow);
        return ((Integer) model.getValueAt(row, 0)).intValue();
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        audienceCombo.setSelectedIndex(0);
        table.clearSelection();
    }
}