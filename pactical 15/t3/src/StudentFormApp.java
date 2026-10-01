import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentFormApp extends JFrame {
    // Form component fields
    private JTextField txtName, txtID, txtEmail, txtPhone, txtDOB, txtGPA, txtAddress;
    private JRadioButton rbMale, rbFemale, rbOther;
    private ButtonGroup genderGroup;
    private JComboBox<String> cmbDept, cmbCourse, cmbYear;

    public StudentFormApp() {
        // Step (c): Initialize window layout configurations
        setTitle("Student Information Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 550);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Row 0: Student Name ---
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Student Name:"), gbc);
        txtName = new JTextField(20);
        gbc.gridx = 1; add(txtName, gbc);

        // --- Row 1: Student ID ---
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Student ID:"), gbc);
        txtID = new JTextField(20);
        gbc.gridx = 1; add(txtID, gbc);

        // --- Row 2: Email ---
        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(20);
        gbc.gridx = 1; add(txtEmail, gbc);

        // --- Row 3: Phone Number ---
        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Phone Number:"), gbc);
        txtPhone = new JTextField(20);
        gbc.gridx = 1; add(txtPhone, gbc);

        // --- Row 4: Date of Birth ---
        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Date of Birth:"), gbc);
        txtDOB = new JTextField(20);
        gbc.gridx = 1; add(txtDOB, gbc);

        // --- Row 5: Gender (Radio Buttons) ---
        gbc.gridx = 0; gbc.gridy = 5; add(new JLabel("Gender:"), gbc);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        rbOther = new JRadioButton("Other");
        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderGroup.add(rbOther);
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        genderPanel.add(rbOther);
        gbc.gridx = 1; add(genderPanel, gbc);

        // --- Row 6: Department (Dropdown) ---
        gbc.gridx = 0; gbc.gridy = 6; add(new JLabel("Department:"), gbc);
        String[] depts = {"", "ICT", "ET", "BST", "Multidisciplinary"};
        cmbDept = new JComboBox<>(depts);
        gbc.gridx = 1; add(cmbDept, gbc);

        // --- Row 7: Course (Dropdown) ---
        gbc.gridx = 0; gbc.gridy = 7; add(new JLabel("Course:"), gbc);
        String[] courses = {"", "Software Engineering", "Network Technology", "Biomedical", "Agricultural"};
        cmbCourse = new JComboBox<>(courses);
        gbc.gridx = 1; add(cmbCourse, gbc);

        // --- Row 8: Year of Study (Dropdown) ---
        gbc.gridx = 0; gbc.gridy = 8; add(new JLabel("Year of Study:"), gbc);
        String[] years = {"", "1st Year", "2nd Year", "3rd Year", "4th Year"};
        cmbYear = new JComboBox<>(years);
        gbc.gridx = 1; add(cmbYear, gbc);

        // --- Row 9: GPA ---
        gbc.gridx = 0; gbc.gridy = 9; add(new JLabel("GPA:"), gbc);
        txtGPA = new JTextField(20);
        gbc.gridx = 1; add(txtGPA, gbc);

        // --- Row 10: Address ---
        gbc.gridx = 0; gbc.gridy = 10; add(new JLabel("Address:"), gbc);
        txtAddress = new JTextField(20);
        gbc.gridx = 1; add(txtAddress, gbc);

        // --- Step (d): Submit Button Placement ---
        JButton btnSubmit = new JButton("Submit");
        gbc.gridx = 0; gbc.gridy = 11;
        gbc.gridwidth = 2; // Center row alignment span
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(btnSubmit, gbc);

        // Step (e): Validation action handling logic
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processFormSubmission();
            }
        });
    }

    private void processFormSubmission() {
        // Collect text inputs
        String name = txtName.getText().trim();
        String id = txtID.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String dob = txtDOB.getText().trim();
        String gpa = txtGPA.getText().trim();
        String address = txtAddress.getText().trim();

        // Collect radio gender item
        String gender = "";
        if (rbMale.isSelected()) gender = "Male";
        else if (rbFemale.isSelected()) gender = "Female";
        else if (rbOther.isSelected()) gender = "Other";

        // Collect dropdown selections
        String dept = (String) cmbDept.getSelectedItem();
        String course = (String) cmbCourse.getSelectedItem();
        String year = (String) cmbYear.getSelectedItem();

        // Step (e-i): If any fields are completely empty or unselected
        if (name.isEmpty() || id.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                dob.isEmpty() || gender.isEmpty() || dept.isEmpty() ||
                course.isEmpty() || year.isEmpty() || gpa.isEmpty() || address.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "You need to fill out all the data",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            // Step (e-ii): If all data is entered successfully, compile and print
            String successMessage = "--- Student Registration Summary ---\n\n" +
                    "Name: " + name + "\n" +
                    "Student ID: " + id + "\n" +
                    "Email: " + email + "\n" +
                    "Phone: " + phone + "\n" +
                    "DOB: " + dob + "\n" +
                    "Gender: " + gender + "\n" +
                    "Department: " + dept + "\n" +
                    "Course: " + course + "\n" +
                    "Year of Study: " + year + "\n" +
                    "GPA: " + gpa + "\n" +
                    "Address: " + address;

            JOptionPane.showMessageDialog(this,
                    successMessage,
                    "Form Submission Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentFormApp().setVisible(true));
    }
}
