package ftms.dao;

import ftms.config.DBConnection;
import ftms.model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {
    public boolean addNotice(Notice notice) {
        String query = "INSERT INTO notice (admin_id, title, description, date_posted) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);) {

            pstmt.setInt(1,notice.getAdmin_id());
            pstmt.setString(2,notice.getTitle());
            pstmt.setString(3,notice.getDescription());
            pstmt.setDate(4, (Date) notice.getDate_posted());

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
                notices.add(new Notice(
                        rs.getInt("notice_id"),
                        rs.getInt("admin_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date_posted")

                ));
            }




        } catch (SQLException e) {
            System.err.println("Error fetching notices: " + e.getMessage());
            e.printStackTrace();
        }
        return List.of();
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
}
