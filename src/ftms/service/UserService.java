package ftms.service;

import ftms.dao.UserDAO;
import ftms.exception.DatabaseException;
import ftms.exception.DuplicateUserException;
import ftms.model.Batch;
import ftms.model.User;
import ftms.util.PasswordHasher;

import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public List<User> getAllUsers() throws DatabaseException {
        try {
            return userDAO.getAllUsers();
        } catch (Exception e) {
            throw new DatabaseException("Failed to load users.", e);
        }
    }

    /**
     * Creates a new user with optional profile picture.
     *
     * @param userName User's full name
     * @param email User's email address (must be unique)
     * @param rawPassword Plain text password (will be hashed before storage)
     * @param role User's role (ADMIN, LECTURER, TECHNICAL_OFFICER, UNDERGRADUATE)
     * @param contactNo User's contact number (optional)
     * @param extra Role-specific field (designation for lecturer, dept_id for TO, reg_no for student)
     * @param batch Batch object (required only for UNDERGRADUATE role)
     * @param profilePicPath Absolute path to the profile picture file (optional)
     * @return true if user created successfully, false otherwise
     * @throws DatabaseException if database operation fails
     * @throws DuplicateUserException if email or registration number already exists
     */
    public boolean createUser(String userName, String email, String rawPassword,
                              String role, String contactNo, String extra, Batch batch, String profilePicPath)
            throws DatabaseException, DuplicateUserException {
        if (userName.isBlank() || email.isBlank() || rawPassword.isBlank()) {
            throw new DatabaseException("Name, email and password are required.");
        }
        if ("UNDERGRADUATE".equals(role) && batch == null) {
            throw new DatabaseException("A batch must be selected for a student.");
        }
        if (!"UNDERGRADUATE".equals(role)) {
            batch = null;   // batch only applies to students
        }

        // Handle profile picture - if no file selected, use default
        String profilePicture = "profile_pics/default.jpg";
        if (profilePicPath != null && !profilePicPath.trim().isEmpty()) {
            // Copy the uploaded image to profile_pics directory and get the new path
            profilePicture = copyProfilePicture(profilePicPath.trim());
        }

        try {
            return userDAO.insertUser(userName.trim(), email.trim(),
                    PasswordHasher.hashPassword(rawPassword), role,
                    contactNo == null ? null : contactNo.trim(), extra, batch, profilePicture) > 0;
        } catch (DuplicateUserException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to create user.", e);
        }
    }

    /**
     * Copies the uploaded profile picture to the profile_pics directory.
     *
     * This method:
     * 1. Validates that the source file exists
     * 2. Creates the profile_pics directory if it doesn't exist
     * 3. Generates a unique filename using timestamp to avoid conflicts
     * 4. Copies the file to the profile_pics directory
     * 5. Returns the relative path for database storage
     *
     * @param sourcePath Absolute path of the source image file
     * @return Relative path of the copied file (e.g., "profile_pics/profile_1699123456789.jpg")
     * @throws DatabaseException if file operations fail
     */
    private String copyProfilePicture(String sourcePath) throws DatabaseException {
        try {
            java.io.File sourceFile = new java.io.File(sourcePath);
            if (!sourceFile.exists()) {
                throw new DatabaseException("Profile picture file not found.");
            }

            // Create profile_pics directory if it doesn't exist
            java.io.File profilePicsDir = new java.io.File("profile_pics");
            if (!profilePicsDir.exists()) {
                profilePicsDir.mkdirs();
            }

            // Generate unique filename using timestamp to prevent filename conflicts
            String extension = sourceFile.getName().substring(sourceFile.getName().lastIndexOf('.'));
            String destFileName = "profile_" + System.currentTimeMillis() + extension;
            java.io.File destFile = new java.io.File(profilePicsDir, destFileName);

            // Copy the uploaded file to the profile_pics directory
            java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath());

            // Return the relative path for storage in database
            return "profile_pics/" + destFileName;
        } catch (Exception e) {
            throw new DatabaseException("Failed to copy profile picture: " + e.getMessage());
        }
    }

    public boolean updateUser(int userId, String userName, String email, String contactNo)
            throws DatabaseException {
        try {
            return userDAO.updateUser(userId, userName, email, contactNo);
        } catch (Exception e) {
            throw new DatabaseException("Failed to update user.", e);
        }
    }

    public boolean deleteUser(int userId) throws DatabaseException {
        try {
            return userDAO.deleteUser(userId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete user.", e);
        }
    }

    public boolean resetPassword(int userId, String rawPassword) throws DatabaseException {
        try {
            return userDAO.resetPassword(userId, PasswordHasher.hashPassword(rawPassword));
        } catch (Exception e) {
            throw new DatabaseException("Failed to reset password.", e);
        }
    }
}