package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.Notice;

import java.sql.*;
import java.util.*;
public class NoticeDAO {
    public boolean addNotice(Notice notice) {
        String query = "INSERT INTO notice (admin_id, title, description, target_audience, date_posted) VALUES (?, ?, ?, ?, ?)";        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);) {

            pstmt.setInt(1,notice.getAdmin_id());
            pstmt.setString(2,notice.getTitle());
            pstmt.setString(3,notice.getDescription());
            pstmt.setString(4, notice.getTarget_audience());
            pstmt.setDate(5, new java.sql.Date(notice.getDate_posted().getTime()));
            return  pstmt.executeUpdate() > 0;



        } catch (SQLException e) {
            System.err.println("Error adding notice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    public List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        String query = "SELECT * FROM notice ORDER BY date_posted DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);){

            while (rs.next()) {
                Notice n = new Notice(
                        rs.getInt("notice_id"),
                        rs.getInt("admin_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date_posted"));
                n.setTarget_audience(rs.getString("target_audience"));
                notices.add(n);
            }




        } catch (SQLException e) {
            System.err.println("Error fetching notices: " + e.getMessage());
            e.printStackTrace();
        }
        return notices;
    }
    public List<Notice> getNoticesForRole(String role) {
        List<Notice> notices = new ArrayList<>();
        String query = "SELECT * FROM notice WHERE target_audience = 'ALL' OR target_audience = ? ORDER BY date_posted DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notice n = new Notice(
                            rs.getInt("notice_id"),
                            rs.getInt("admin_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getDate("date_posted"));
                    n.setTarget_audience(rs.getString("target_audience"));
                    notices.add(n);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching notices for role: " + e.getMessage());
        }
        return notices;   // return the list you built — never List.of()!
    }

    public boolean deleteNotice(int noticeId){
        String query = "DELETE FROM notice WHERE notice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);){

            pstmt.setInt(1,noticeId);
            return pstmt.executeUpdate() > 0;


        } catch (SQLException e) {
            System.err.println("Error deleting notice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateNotice(Notice notice) {
        String query = "UPDATE notice SET title = ?, description = ?, target_audience = ? WHERE notice_id = ?";        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, notice.getTitle());
            pstmt.setString(2, notice.getDescription());
            pstmt.setString(3, notice.getTarget_audience());
            pstmt.setInt(4, notice.getNotice_id());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating notice: " + e.getMessage());
            return false;
        }
    }
}
