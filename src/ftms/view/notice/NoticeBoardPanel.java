package ftms.view.notice;

import ftms.exception.DatabaseException;
import ftms.model.Notice;
import ftms.service.NoticeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NoticeBoardPanel extends JPanel {
    private final NoticeService noticeService = new NoticeService();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Posted On", "Title"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextArea descriptionArea = new JTextArea();
    private List<Notice> noticeList = List.of();

    public NoticeBoardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel heading = new JLabel("Notice Board", SwingConstants.CENTER);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 16f));
        add(heading, BorderLayout.NORTH);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(0, 130));

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(descScroll, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                if (row < noticeList.size()) {
                    descriptionArea.setText(noticeList.get(row).getDescription());
                }
            }
        });

        loadNotices();
    }

    private void loadNotices() {
        model.setRowCount(0);
        try {
            noticeList = noticeService.getAllNotices();
            for (Notice n : noticeList) {
                model.addRow(new Object[]{String.valueOf(n.getDate_posted()), n.getTitle()});
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}