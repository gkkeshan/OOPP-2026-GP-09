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
}