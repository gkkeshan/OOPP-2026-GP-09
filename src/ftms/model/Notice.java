package ftms.model;

import java.util.Date;

public class Notice {
    private int notice_id;
    private int admin_id;
    private String title;
    private String description;
    private String target_audience;   // ALL, UNDERGRADUATE, LECTURER, TECHNICAL_OFFICER
    private Date date_posted;


    public Notice(int notice_id, int admin_id, String title, String description, Date date_posted) {
        this.notice_id = notice_id;
        this.admin_id = admin_id;
        this.title = title;
        this.description = description;
        this.date_posted = date_posted;
    }

    public int getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(int notice_id) {
        this.notice_id = notice_id;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(Date date_posted) {
        this.date_posted = date_posted;
    }

    public String getTarget_audience() { return target_audience; }
    public void setTarget_audience(String target_audience) { this.target_audience = target_audience; }

    @Override
    public String toString() {
        return "Notice{" +
                "notice_id=" + notice_id +
                ", admin_id=" + admin_id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date_posted=" + date_posted +
                '}';
    }
}

