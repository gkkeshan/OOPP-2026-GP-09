package ftms.service;

import ftms.dao.UserDAO;
import ftms.exception.AuthenticationException;
import ftms.exception.DatabaseException;
import ftms.model.User;
import ftms.util.PasswordHasher;
import ftms.util.SessionManager;

public class AuthService {
    private UserDAO userDAO = new UserDAO();

    public User login(String email, String password) throws AuthenticationException, DatabaseException {

        try {
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                throw new AuthenticationException("No account found with this email address.");
            }

            if (!PasswordHasher.verifyPassword(password, user.getPassword())) {
                throw new AuthenticationException("Incorrect password. Please try again.");
            }

            // Successful login, set current session
            SessionManager.setCurrentUser(user);
            return user;
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw (AuthenticationException) e;
            }
            throw new DatabaseException("Database error occurred during authentication.", e);
        }
    }

    public void logout() {
        SessionManager.clearSession();
    }
}