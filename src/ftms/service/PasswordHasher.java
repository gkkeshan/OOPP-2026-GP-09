package ftms.service;

public class PasswordHasher {
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        // Matches your database seed configuration (e.g., 'admin123')
        return inputPassword.equals(storedPassword);
    }

    public static String hashPassword(String password) {
        // Can be expanded or kept as-is for the mini-project scope
        return password;
    }
}