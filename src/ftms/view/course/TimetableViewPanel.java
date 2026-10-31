package ftms.view.course;

import ftms.model.Batch;
import ftms.model.Timetable;
import ftms.model.Venue;
import ftms.service.TimetableService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimetableViewPanel extends JPanel {

    private TimetableService timetableService = new TimetableService();

    private JComboBox<String> cmbBatch = new JComboBox<>();
    private JComboBox<String> cmbDay = new JComboBox<>(
            new String[]{"All Days", "Today", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"});
    private JTextField txtCourseCode = new JTextField(10);
    private JTable table = new JTable();
    private JLabel lblInfo = new JLabel(" ");

    private List<Batch> batchList = new ArrayList<>();

    public TimetableViewPanel() {
        setLayout(new BorderLayout());


        JLabel title = new JLabel("Weekly Timetable", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        filterPanel.add(new JLabel("Batch:"));
        filterPanel.add(cmbBatch);
        filterPanel.add(new JLabel("Day:"));
        filterPanel.add(cmbDay);
        filterPanel.add(new JLabel("Course Code:"));
        filterPanel.add(txtCourseCode);

        JButton btnShow = new JButton("Show");
        JButton btnShowAll = new JButton("Show All");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        buttonPanel.add(btnShow);
        buttonPanel.add(btnShowAll);

        JPanel northPanel = new JPanel(new GridLayout(0, 1, 0, 2));
        northPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 0, 6));
        northPanel.add(title);
        northPanel.add(filterPanel);
        northPanel.add(buttonPanel);
        add(northPanel, BorderLayout.NORTH);


        add(new JScrollPane(table), BorderLayout.CENTER);


        JPanel southPanel = new JPanel();
        southPanel.add(lblInfo);
        add(southPanel, BorderLayout.SOUTH);


        cmbBatch.setPrototypeDisplayValue("9999 - Batch 00 (2024/2025)");

        loadBatches();
        showAll();


        btnShow.addActionListener(e -> showFiltered());
        btnShowAll.addActionListener(e -> showAll());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                showVenueInfo(table.getSelectedRow());
            }
        });
    }

    /** Fills the batch filter combo; the panel still opens if the database fails. */
    private void loadBatches() {
        try {
            cmbBatch.removeAllItems();
            cmbBatch.addItem("All Batches");
            batchList = timetableService.getBatches();
            for (Batch b : batchList) {
                cmbBatch.addItem(b.getBatchId() + " - " + b.display());
            }
        } catch (RuntimeException e) {
            lblInfo.setText("Could not load batches: " + errorMessage(e));
        }
    }

    /** Friendly text for exceptions without a message (e.g. a failed DB connection). */
    private String errorMessage(RuntimeException e) {
        return e.getMessage() != null ? e.getMessage() : e.toString();
    }


    private void showFiltered() {
        try {
            List<Timetable> sessions = timetableService.getAllSessions();

            int batchIndex = cmbBatch.getSelectedIndex();
            if (batchIndex > 0) {   // 0 = All Batches
                int batchId = batchList.get(batchIndex - 1).getBatchId();
                sessions.removeIf(t -> t.getBatchId() != batchId);
            }

            String day = (String) cmbDay.getSelectedItem();
            if (!day.equals("All Days")) {
                if (day.equals("Today")) {
                    day = LocalDate.now().getDayOfWeek().name();   // e.g. "MONDAY"
                }
                String selectedDay = day;
                sessions.removeIf(t -> !t.getDayOfWeek().equals(selectedDay));
            }

            String courseCode = txtCourseCode.getText().trim().toUpperCase();
            if (!courseCode.isEmpty()) {
                sessions.removeIf(t -> !t.getCourseCode().equals(courseCode));
            }

            table.setModel(new TimetableTableModel(sessions));
            lblInfo.setText(sessions.size() + " session(s) found");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAll() {
        try {
            List<Timetable> sessions = timetableService.getAllSessions();
            table.setModel(new TimetableTableModel(sessions));
            lblInfo.setText(sessions.size() + " session(s) in total");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }


    private void showVenueInfo(int row) {
        try {
            Timetable t = ((TimetableTableModel) table.getModel()).getTimetableAt(row);
            Venue venue = timetableService.getVenue(t.getVenueId());
            lblInfo.setText(t.getCourseCode() + " at " + venue.getVenueName()
                    + ", " + venue.getBuilding() + " (capacity " + venue.getCapacity() + ")");
        } catch (RuntimeException e) {
            lblInfo.setText("Venue details not found");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Weekly Timetable");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new TimetableViewPanel());
            frame.setSize(950, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}