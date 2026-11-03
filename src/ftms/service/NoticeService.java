package ftms.service;

import ftms.dao.NoticeDAO;
import ftms.dao.UserDAO;
import ftms.exception.DatabaseException;
import ftms.model.Notice;

import java.util.List;

public class NoticeService {
    private final NoticeDAO noticeDAO = new NoticeDAO();
    private final UserDAO userDAO = new UserDAO();

    // NEW: whitelist of audiences a notice may target
    private static final List<String> ALLOWED_AUDIENCES =
            List.of("ALL", "UNDERGRADUATE", "LECTURER", "TECHNICAL_OFFICER");

    public List<Notice> getAllNotices() throws DatabaseException {
        try {
            return noticeDAO.getAllNotices();
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch notices.", e);
        }
    }

    // NEW method #2: role-filtered list for the NoticeBoardPanel (its OWN method — not inside updateNotice!)
    public List<Notice> getNoticesForRole(String role) throws DatabaseException {
        try {
            return noticeDAO.getNoticesForRole(role.trim().toUpperCase());
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch notices.", e);
        }
    }

    public boolean createNotice(Notice notice, int loggedInUserId) throws DatabaseException {
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            throw new DatabaseException("Notice title is required.");
        }
        String audience = notice.getTarget_audience();                    // NEW check
        if (audience == null || !ALLOWED_AUDIENCES.contains(audience)) {  // NEW check
            throw new DatabaseException("Invalid notice audience.");
        }
        int adminId = userDAO.getAdminIdByUserId(loggedInUserId);
        if (adminId == -1) {
            throw new DatabaseException("Logged-in user has no admin profile.");
        }
        notice.setAdmin_id(adminId);
        try {
            return noticeDAO.addNotice(notice);
        } catch (Exception e) {
            throw new DatabaseException("Failed to post the notice.", e);
        }
    }

    public boolean updateNotice(Notice notice) throws DatabaseException {
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            throw new DatabaseException("Notice title is required.");
        }
        String audience = notice.getTarget_audience();                    // NEW check
        if (audience == null || !ALLOWED_AUDIENCES.contains(audience)) {  // NEW check
            throw new DatabaseException("Invalid notice audience.");
        }
        try {
            return noticeDAO.updateNotice(notice);
        } catch (Exception e) {
            throw new DatabaseException("Failed to update the notice.", e);
        }
    }

    public boolean deleteNotice(int noticeId) throws DatabaseException {
        try {
            return noticeDAO.deleteNotice(noticeId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete the notice.", e);
        }
    }
}