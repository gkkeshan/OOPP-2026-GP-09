package ftms.view.users.admin;

import ftms.dao.BatchDAO;
import ftms.dao.DepartmentDAO;
import ftms.exception.DuplicateUserException;
import ftms.model.Batch;
import ftms.model.Department;
import ftms.model.User;
import ftms.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManagementPanel extends JPanel {

    private final UserService userService = new UserService();
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> roleFilter;
    private JTextField searchField;

    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(
                new Object[]{"User_ID", "Name", "Email", "Role", "Contact","Batch"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Filter and Search Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter & Search"));

        filterPanel.add(new JLabel("Role:"));
        String[] roles = {"All", "ADMIN", "LECTURER", "TECHNICAL_OFFICER", "UNDERGRADUATE"};
        roleFilter = new JComboBox<>(roles);
        filterPanel.add(roleFilter);

        filterPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        filterPanel.add(searchField);

        add(filterPanel, BorderLayout.NORTH);

        JButton refresh = new JButton("Refresh");
        JButton create = new JButton("Create User");
        JButton edit = new JButton("Edit Selected");
        JButton delete = new JButton("Delete Selected");
        JButton resetPw = new JButton("Reset Password");

        JPanel buttons = new JPanel();
        buttons.add(refresh);
        buttons.add(create);
        buttons.add(edit);
        buttons.add(delete);
        buttons.add(resetPw);
        add(buttons, BorderLayout.SOUTH);

        refresh.addActionListener(e -> loadUsers());
        create.addActionListener(e -> showCreateDialog());
        edit.addActionListener(e -> showEditDialog());
        delete.addActionListener(e -> deleteSelected());
        resetPw.addActionListener(e -> resetPassword());

        // Add listeners for filter and search
        roleFilter.addActionListener(e -> loadUsers());
        searchField.addActionListener(e -> loadUsers());

        loadUsers();
    }

    private void loadUsers() {
        try {
            model.setRowCount(0);
            for (User u : userService.getAllUsers()) {
                String batch = "";
                if (u instanceof ftms.model.Undergraduate) {
                    batch = ((ftms.model.Undergraduate) u).getBatch();
                    if (batch == null) batch = "";
                }
                model.addRow(new Object[]{u.getUserID(), u.getUserName(),
                        u.getEmail(), u.getRole(), u.getContactNumber(), batch});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private int selectedUserId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return -1;
        }
        return (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
    }

    private void showCreateDialog() {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField password = new JTextField();
        JTextField contact = new JTextField();
        JLabel extraLabel = new JLabel("(not needed)");
        JTextField extra = new JTextField();

        // Batch dropdown — used only when the role is UNDERGRADUATE
        JComboBox<Batch> batchBox = new JComboBox<>();
        Map<Integer, String> deptNames = new HashMap<>();
        try {
            for (Department d : new DepartmentDAO().findAll()) deptNames.put(d.getDepartmentId(), d.getDepartmentName());
            for (Batch b : new BatchDAO().findAll()) batchBox.addItem(b);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load batches: " + e.getMessage());
        }
        batchBox.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                                                                    int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof Batch) {
                    Batch b = (Batch) value;
                    String dept = deptNames.get(b.getDepartmentId());
                    setText(b.display() + (dept == null ? "" : " — " + dept));
                }
                return this;
            }
        });
        JLabel batchLabel = new JLabel("Batch:");

        String[] roles = {"ADMIN", "LECTURER", "TECHNICAL_OFFICER", "UNDERGRADUATE"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        Runnable updateRoleFields = () -> {
            String r = (String) roleBox.getSelectedItem();
            boolean student = "UNDERGRADUATE".equals(r);
            if ("LECTURER".equals(r)) extraLabel.setText("Designation:");
            else if ("TECHNICAL_OFFICER".equals(r)) extraLabel.setText("Department ID:");
            else if (student) extraLabel.setText("Registration No:");
            else extraLabel.setText("(not needed)");
            batchBox.setEnabled(student);      // batch only matters for students
            batchLabel.setEnabled(student);
        };
        roleBox.addActionListener(e -> updateRoleFields.run());
        updateRoleFields.run();   // apply initial state (ADMIN)

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.add(new JLabel("Name:"));        form.add(name);
        form.add(new JLabel("Email:"));       form.add(email);
        form.add(new JLabel("Password:"));    form.add(password);
        form.add(new JLabel("Role:"));        form.add(roleBox);
        form.add(new JLabel("Contact No:"));  form.add(contact);
        form.add(extraLabel);                 form.add(extra);
        form.add(batchLabel);                 form.add(batchBox);

        while (true) {
            int choice = JOptionPane.showConfirmDialog(this, form, "Create User",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) return;
            try {
                String role = (String) roleBox.getSelectedItem();
                boolean student = "UNDERGRADUATE".equals(role);
                if (student && batchBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Select a batch for the student.");
                    continue;   // dialog stays open
                }
                Batch selectedBatch = student ? (Batch) batchBox.getSelectedItem() : null;
                boolean ok = userService.createUser(name.getText(), email.getText(),
                        password.getText(), role,
                        contact.getText(), extra.getText(), selectedBatch);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "User created!");
                    loadUsers();
                    return;
                }
                JOptionPane.showMessageDialog(this, "User could not be created.");
            } catch (DuplicateUserException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage()); // dialog stays open
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                return;
            }
        }
    }

    private void showEditDialog() {
        int userId = selectedUserId();
        if (userId == -1) return;
        int row = table.convertRowIndexToModel(table.getSelectedRow());

        JTextField name = new JTextField((String) model.getValueAt(row, 1));
        JTextField email = new JTextField((String) model.getValueAt(row, 2));
        JTextField contact = new JTextField((String) model.getValueAt(row, 4));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Name:"));        form.add(name);
        form.add(new JLabel("Email:"));       form.add(email);
        form.add(new JLabel("Contact No:"));  form.add(contact);

        if (JOptionPane.showConfirmDialog(this, form, "Edit User #" + userId,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
            return;
        try {
            if (userService.updateUser(userId, name.getText(), email.getText(), contact.getText())) {
                JOptionPane.showMessageDialog(this, "User updated!");
                loadUsers();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteSelected() {
        int userId = selectedUserId();
        if (userId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user #" + userId + "? Their role record is removed too.",
                "Confirm Delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION) return;
        try {
            if (userService.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted.");
                loadUsers();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void resetPassword() {
        int userId = selectedUserId();
        if (userId == -1) return;
        String pw = JOptionPane.showInputDialog(this, "New password for user #" + userId + ":");
        if (pw == null || pw.isBlank()) return;
        try {
            if (userService.resetPassword(userId, pw)) {
                JOptionPane.showMessageDialog(this, "Password reset.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}