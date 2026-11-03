package ftms.service;

import ftms.dao.UserDAO;
import ftms.exception.DatabaseException;
import ftms.model.User;

public class ProfileService {
    private UserDAO userDAO = new UserDAO();

    public User getUserById(int userId) throws DatabaseException {
        try {
            return userDAO.getUserById(userId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve user profile.", e);
        }
    }

    public boolean updateContactDetails(int userId, String contactNo) throws DatabaseException {
        try {
            return userDAO.updateContactNo(userId, contactNo);
        } catch (Exception e) {
            throw new DatabaseException("Failed to update contact details.", e);
        }
    }
    public boolean updateEmail(int userId, String email) throws DatabaseException {
        try {
            return userDAO.updateEmail(userId, email);
        } catch (Exception e) {
            throw new DatabaseException("Failed to update email.", e);
        }
    }

    public boolean updateProfilePicture(int userId, String path) throws DatabaseException {
        try {
            return userDAO.updateProfilePicture(userId, path);
        } catch (Exception e) {
            throw new DatabaseException("Failed to update profile picture.", e);
        }
    }

    // Copies the chosen image into the project's profile_pics folder
    // (same pattern as MaterialStorageService) and returns the stored path.
    public String saveProfilePicture(java.nio.file.Path sourceFile) throws java.io.IOException {
        java.nio.file.Path folder = java.nio.file.Paths.get("profile_pics");
        java.nio.file.Files.createDirectories(folder);
        String fileName = System.currentTimeMillis() + "_" + sourceFile.getFileName().toString();
        java.nio.file.Path target = folder.resolve(fileName);
        java.nio.file.Files.copy(sourceFile, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }
}