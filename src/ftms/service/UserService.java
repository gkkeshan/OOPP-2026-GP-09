package ftms.service;

import ftms.dao.UserDAO;
import ftms.exception.DatabaseException;
import ftms.exception.DuplicateUserException;
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

    public boolean createUser(String userName, String email, String rawPassword,
                              String role, String contactNo, String extra)
            throws DatabaseException, DuplicateUserException {
        if (userName.isBlank() || email.isBlank() || rawPassword.isBlank()) {
            throw new DatabaseException("Name, email and password are required.");
        }
        try {
            return userDAO.insertUser(userName.trim(), email.trim(),
                    PasswordHasher.hashPassword(rawPassword), role,
                    contactNo == null ? null : contactNo.trim(), extra) > 0;
        } catch (DuplicateUserException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to create user.", e);
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