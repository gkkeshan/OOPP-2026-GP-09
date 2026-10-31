package ftms.view.profile;

import ftms.model.User;
import ftms.service.ProfileService;
import ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProfilePanel extends JPanel {

    private final ProfileService profileService = new ProfileService();

    private final boolean isUG = "UNDERGRADUATE".equalsIgnoreCase(
            SessionManager.getCurrentUser().getRole().trim());

    private final JTextField nameField = new JTextField(15);
    private final JTextField roleField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JTextField contactField = new JTextField(15);
    private final JTextField pictureField = new JTextField(15);
    private final JLabel preview = new JLabel("No picture", SwingConstants.CENTER);

    public ProfilePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("My Profile"));

        nameField.setEditable(false);   // username locked for EVERY role
        roleField.setEditable(false);

        form.add(new JLabel("Username:"));
        form.add(nameField);
        form.add(new JLabel("Role:"));
        form.add(roleField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Contact No:"));
        form.add(contactField);
        form.add(new JLabel("Profile Picture:"));
        form.add(pictureField);

        if (isUG) {
            emailField.setEditable(false);   // UG: contact + picture ONLY
            emailField.setToolTipText(
                    "Undergraduates can update contact details and profile picture only");
        }

        JButton browse = new JButton("Browse...");
        JButton savePicture = new JButton("Save Picture");
        JButton saveDetails = new JButton("Save Details");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(browse);
        buttons.add(savePicture);
        buttons.add(saveDetails);

        add(form, BorderLayout.NORTH);
        add(preview, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        browse.addActionListener(e -> choosePicture());
        savePicture.addActionListener(e -> savePicture());
        saveDetails.addActionListener(e -> saveDetails());

        loadProfile();
    }

    private void loadProfile() {
        try {
            User u = profileService.getUserById(
                    SessionManager.getCurrentUser().getUserID());
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Profile not found.");
                return;
            }
            nameField.setText(u.getUserName());
            roleField.setText(u.getRole());
            emailField.setText(u.getEmail());
            contactField.setText(u.getContactNumber());
            pictureField.setText(u.getProfilePicture());
            showPreview(u.getProfilePicture());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }
    }

    private void choosePicture() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pictureField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void savePicture() {
        try {
            if (pictureField.getText().trim().isEmpty()
                    || !Files.exists(Paths.get(pictureField.getText().trim()))) {
                JOptionPane.showMessageDialog(this, "Select a valid image file first.");
                return;
            }
            String storedPath = profileService.saveProfilePicture(
                    Paths.get(pictureField.getText().trim()));
            profileService.updateProfilePicture(
                    SessionManager.getCurrentUser().getUserID(), storedPath);
            showPreview(storedPath);
            JOptionPane.showMessageDialog(this, "Profile picture updated!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving picture: " + e.getMessage());
        }
    }

    private void saveDetails() {
        try {
            boolean ok = profileService.updateContactDetails(
                    SessionManager.getCurrentUser().getUserID(),
                    contactField.getText().trim());

            if (!isUG) {   // UG cannot touch email (spec: contact + picture only)
                ok &= profileService.updateEmail(
                        SessionManager.getCurrentUser().getUserID(),
                        emailField.getText().trim());
            }
            JOptionPane.showMessageDialog(this, ok ? "Profile updated!" : "Update failed.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage());
        }
    }

    private void showPreview(String path) {
        if (path != null && !path.isBlank() && Files.exists(Paths.get(path))) {
            preview.setIcon(new ImageIcon(new ImageIcon(path).getImage()
                    .getScaledInstance(160, 160, Image.SCALE_DEFAULT)));
            preview.setText(null);
        } else {
            preview.setIcon(null);
            preview.setText("No picture");
        }
    }
}