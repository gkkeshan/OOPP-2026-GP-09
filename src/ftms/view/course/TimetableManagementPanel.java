package ftms.view.course;

import ftms.exception.TimetableConflictException;
import ftms.model.SessionType;
import ftms.model.Timetable;
import ftms.model.Venue;
import ftms.service.TimetableService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TimetableManagementPanel extends JPanel {

    private TimetableService timetableService = new TimetableService();

    private TimetableTableModel tableModel;
    private JTable timetableTable;

    private JTextField txtCourseCode;
    private JComboBox<String> cmbVenue;
    private JComboBox<SessionType> cmbType;
    private JTextField txtDate;
    private JTextField txtStart;
    private JTextField txtEnd;

    private List<Venue> venueList;

    public TimetableManagementPanel() {
        setLayout(new BorderLayout(10, 10));

        // 1. Title Header
        JLabel headerLabel = new JLabel("Timetable Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(headerLabel, BorderLayout.NORTH);

        // 2. Input Form (West)
        add(buildFormPanel(), BorderLayout.WEST);

        // 3. Session Table (Center)
        tableModel = new TimetableTableModel(timetableService.getAllSessions());
        timetableTable = new JTable(tableModel);
        timetableTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            onRowSelect();
        });
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        // 4. Action Buttons (South)
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

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> refreshTable());

        // 5. Load Venues
        loadVenues();
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Session Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtCourseCode = new JTextField(12);
        cmbVenue = new JComboBox<>();
        cmbType = new JComboBox<>(SessionType.values());
        txtDate = new JTextField(12);
        txtStart = new JTextField(12);
        txtEnd = new JTextField(12);

        addFormField(formPanel, gbc, 0, "Course Code", txtCourseCode);
        addFormField(formPanel, gbc, 1, "Venue", cmbVenue);
        addFormField(formPanel, gbc, 2, "Type", cmbType);
        addFormField(formPanel, gbc, 3, "Date (yyyy-mm-dd)", txtDate);
        addFormField(formPanel, gbc, 4, "Start (hh:mm)", txtStart);
        addFormField(formPanel, gbc, 5, "End (hh:mm)", txtEnd);

        return formPanel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    // 6. Form Actions

    private void onAdd() {
        try {
            timetableService.addSession(readForm(0));
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Session added successfully");
        } catch (TimetableConflictException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Timetable Conflict", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot add session: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        int row = timetableTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a session from the table first");
            return;
        }
        try {
            Timetable selected = tableModel.getTimetableAt(row);
            timetableService.updateSession(readForm(selected.getTimetableId()));
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Session updated successfully");
        } catch (TimetableConflictException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Timetable Conflict", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot update session: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = timetableTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a session from the table first");
            return;
        }
        Timetable selected = tableModel.getTimetableAt(row);
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete session #" + selected.getTimetableId() + " (" + selected.getCourseCode() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            timetableService.deleteSession(selected.getTimetableId());
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Session deleted successfully");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete session: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelect() {
        int row = timetableTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Timetable t = tableModel.getTimetableAt(row);
        txtCourseCode.setText(t.getCourseCode());
        cmbVenue.setSelectedIndex(findVenueIndex(t.getVenueId()));
        cmbType.setSelectedItem(t.getSessionType());
        txtDate.setText(t.getSessionDate().toString());
        txtStart.setText(t.getStartTime().toString());
        txtEnd.setText(t.getEndTime().toString());
    }

    // 7. Helpers

    private Timetable readForm(int timetableId) {
        String courseCode = txtCourseCode.getText().trim();
        if (courseCode.isEmpty()) {
            throw new IllegalArgumentException("Please enter a course code");
        }
        int venueIndex = cmbVenue.getSelectedIndex();
        if (venueIndex < 0) {
            throw new IllegalArgumentException("Please select a venue");
        }
        LocalDate date = LocalDate.parse(txtDate.getText().trim());
        LocalTime start = LocalTime.parse(txtStart.getText().trim());
        LocalTime end = LocalTime.parse(txtEnd.getText().trim());
        SessionType type = (SessionType) cmbType.getSelectedItem();
        return new Timetable(timetableId, courseCode, venueList.get(venueIndex).getVenueId(), start, end, type, date);
    }

    private void loadVenues() {
        venueList = timetableService.getVenues();
        cmbVenue.removeAllItems();
        for (Venue v : venueList) {
            cmbVenue.addItem(v.getVenueId() + " - " + v.getVenueName());
        }
    }

    private int findVenueIndex(int venueId) {
        for (int i = 0; i < venueList.size(); i++) {
            if (venueList.get(i).getVenueId() == venueId) {
                return i;
            }
        }
        return -1;
    }

    private void refreshTable() {
        tableModel = new TimetableTableModel(timetableService.getAllSessions());
        timetableTable.setModel(tableModel);
    }

    private void clearForm() {
        txtCourseCode.setText("");
        cmbVenue.setSelectedIndex(-1);
        cmbType.setSelectedIndex(0);
        txtDate.setText("");
        txtStart.setText("");
        txtEnd.setText("");
    }

    // 8. Standalone Test Runner

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Timetable Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new TimetableManagementPanel());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}