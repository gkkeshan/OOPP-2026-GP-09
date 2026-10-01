import javax.swing.*;
import java.awt.*;

public class StudentForm extends JFrame {
    private final JTextField nameField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField courseField = new JTextField();
    private final JTextField emailField = new JTextField();

    public StudentForm() {
        setTitle("Student Information Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitForm());

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);
        panel.add(new JLabel("Course:"));
        panel.add(courseField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel());
        panel.add(submitButton);

        add(panel);
    }

    private void submitForm() {
        if (nameField.getText().trim().isEmpty()
                || studentIdField.getText().trim().isEmpty()
                || courseField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "You need to fill out all the data",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String studentDetails =
                "Name: " + nameField.getText()
                        + "\nStudent ID: " + studentIdField.getText()
                        + "\nCourse: " + courseField.getText()
                        + "\nEmail: " + emailField.getText();

        JOptionPane.showMessageDialog(
                this,
                studentDetails,
                "Student Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new StudentForm().setVisible(true));
    }
}