package ftms.view.course;

import ftms.exception.TimetableConflictException;
import ftms.model.Batch;
import ftms.model.SessionType;
import ftms.model.Timetable;
import ftms.model.Venue;
import ftms.service.TimetableService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimetableManagementPanel extends JPanel {

    private TimetableService timetableService = new TimetableService();

    private JTextField txtCourseCode = new JTextField(10);
    private JComboBox<String> cmbBatch = new JComboBox<>();
    private JComboBox<String> cmbVenue = new JComboBox<>();
    private JComboBox<SessionType> cmbType = new JComboBox<>(SessionType.values());
    private JComboBox<String> cmbDay = new JComboBox<>(
            new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"});
    private JTextField txtStart = new JTextField(8);
    private JTextField txtEnd = new JTextField(8);

    private List<Batch> batchList = new ArrayList<>();
    private List<Venue> venueList = new ArrayList<>();
    private JTable table = new JTable();

    public TimetableManagementPanel() {
        setLayout(new BorderLayout());


        JLabel title = new JLabel("Timetable Management", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Session Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCourseCode, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Batch:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbBatch, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Venue:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbVenue, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Session Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbType, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbDay, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Start (hh:mm):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStart, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("End (hh:mm):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEnd, gbc);

        add(formPanel, BorderLayout.WEST);


        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Session");
        JButton btnUpdate = new JButton("Update Session");
        JButton btnDelete = new JButton("Delete Session");
        JButton btnRefresh = new JButton("Refresh");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);


        loadBatches();
        loadVenues();
        refreshTable();


        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> refreshTable());


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillForm(table.getSelectedRow());
            }
        });
    }

    private void loadBatches() {
        cmbBatch.removeAllItems();
        batchList = timetableService.getBatches();
        for (Batch b : batchList) {
            cmbBatch.addItem(b.getBatchId() + " - " + b.display());
        }
    }

    private void loadVenues() {
        cmbVenue.removeAllItems();
        venueList = timetableService.getVenues();
        for (Venue v : venueList) {
            cmbVenue.addItem(v.getVenueId() + " - " + v.getVenueName());
        }
    }

    private void refreshTable() {
        table.setModel(new TimetableTableModel(timetableService.getAllSessions()));
    }

    private Timetable readForm(int timetableId) {
        String courseCode = txtCourseCode.getText().trim();
        int batchIndex = cmbBatch.getSelectedIndex();
        int venueIndex = cmbVenue.getSelectedIndex();
        SessionType type = (SessionType) cmbType.getSelectedItem();
        String day = (String) cmbDay.getSelectedItem();
        LocalTime start = LocalTime.parse(txtStart.getText().trim());
        LocalTime end = LocalTime.parse(txtEnd.getText().trim());

        if (courseCode.isEmpty()) {
            throw new IllegalArgumentException("Course code is required");
        }
        if (batchIndex == -1) {
            throw new IllegalArgumentException("Please select a batch");
        }
        if (venueIndex == -1) {
            throw new IllegalArgumentException("Please select a venue");
        }
        Batch batch = batchList.get(batchIndex);
        Venue venue = venueList.get(venueIndex);
        return new Timetable(timetableId, courseCode, batch.getBatchId(), venue.getVenueId(), start, end, type, day);
    }

    private void onAdd() {
        try {
            Timetable t = readForm(0);
            timetableService.addSession(t);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Session added");
        } catch (TimetableConflictException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a session to update");
            return;
        }
        try {
            Timetable selected = ((TimetableTableModel) table.getModel()).getTimetableAt(row);
            Timetable t = readForm(selected.getTimetableId());
            timetableService.updateSession(t);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Session updated");
        } catch (TimetableConflictException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a session to delete");
            return;
        }
        try {
            Timetable selected = ((TimetableTableModel) table.getModel()).getTimetableAt(row);
            timetableService.deleteSession(selected.getTimetableId());
            refreshTable();
            JOptionPane.showMessageDialog(this, "Session deleted");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }


    private void fillForm(int row) {
        Timetable t = ((TimetableTableModel) table.getModel()).getTimetableAt(row);
        txtCourseCode.setText(t.getCourseCode());
        for (int i = 0; i < batchList.size(); i++) {
            if (batchList.get(i).getBatchId() == t.getBatchId()) {
                cmbBatch.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < venueList.size(); i++) {
            if (venueList.get(i).getVenueId() == t.getVenueId()) {
                cmbVenue.setSelectedIndex(i);
            }
        }
        cmbType.setSelectedItem(t.getSessionType());
        cmbDay.setSelectedItem(t.getDayOfWeek());
        txtStart.setText(t.getStartTime().toString());
        txtEnd.setText(t.getEndTime().toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Timetable Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new TimetableManagementPanel());
            frame.setSize(950, 560);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}