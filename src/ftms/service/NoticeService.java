package ftms.service;

import ftms.dao.NoticeDAO;
import ftms.exception.DatabaseException;
import ftms.model.Notice;

import java.util.List;

public class NoticeService {
    private NoticeDAO noticeDAO = new NoticeDAO();

    public List<Notice> getAllNotices() throws DatabaseException {
        try {
            return noticeDAO.getAllNotices();
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch notices.", e);
        }
    }

    public boolean createNotice(Notice notice) throws DatabaseException {
        try {
            return noticeDAO.addNotice(notice);
        } catch (Exception e) {
            throw new DatabaseException("Failed to post the notice.", e);
        }
    }
}