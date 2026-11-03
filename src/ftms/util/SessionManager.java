package ftms.util;

import ftms.model.User;

public class SessionManager {
    private static User currentUser;
    private static int currentStudentId = -1;   // NEW: cached student_id of the logged-in UG
    private static String currentRegistrationNo = null;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    // NEW: called once, right after login
    public static void setCurrentStudentId(int studentId) {
        currentStudentId = studentId;
    }

    // NEW: every panel reads this — no lookups anywhere else
    public static int getCurrentStudentId() {
        return currentStudentId;
    }
    public static void setCurrentRegistrationNo(String regNo) {
        currentRegistrationNo = regNo;
    }

    public static String getCurrentRegistrationNo() {
        return currentRegistrationNo;
    }

    // NEW: one-line role check every panel can reuse
    public static boolean isUndergraduate() {
        return currentUser != null
                && "UNDERGRADUATE".equalsIgnoreCase(currentUser.getRole().trim());
    }

    public static void clearSession() {
        currentUser = null;
        currentStudentId = -1;   // NEW: never leak the previous student's ID into the next login
        currentRegistrationNo = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}