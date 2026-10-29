package ftms.view;

import ftms.util.NavigationManager;
import ftms.util.SessionManager;
import ftms.view.attendance.AttendanceEntryPanel;
import ftms.view.attendance.AttendanceReportPanel;
import ftms.view.attendance.AttendanceViewPanel;
import ftms.view.attendance.MedicalRecordPanel;
import ftms.view.course.CourseMaterialsPanel;
import ftms.view.course.TimetableManagementPanel;
import ftms.view.course.TimetableViewPanel;
import ftms.view.marks.EligibilityPanel;
import ftms.view.marks.GpaDashboardPanel;
import ftms.view.marks.MarksEntryPanel;
import ftms.view.marks.ResultsSheetPanel;
import ftms.view.users.admin.AdminDashboardPanel;
import ftms.view.users.admin.DepartmentManagementPanel;
import ftms.view.users.admin.NoticeManagementPanel;
import ftms.view.users.admin.UserManagementPanel;
import ftms.view.course.CourseManagementPanel;
import ftms.view.users.lecturer.LecturerDashboardPanel;
import ftms.view.users.technical.TechnicalOfficerDashboardPanel;
import ftms.view.users.undergraduate.UndergraduateDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel cardContainer;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("FTAMS - Faculty Management System (" + SessionManager.getCurrentUser().getRole() + ")");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private JPanel sidebar;

    private void initComponents() {
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        NavigationManager.initialize(cardContainer, cardLayout);

        sidebar = new JPanel(new GridLayout(0, 1, 5, 5));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String first;
        switch (SessionManager.getCurrentUser().getRole().trim().toUpperCase()) {
            case "ADMIN":
                first = "ADMIN_DASHBOARD";
                addPage("Dashboard",   "ADMIN_DASHBOARD",   new AdminDashboardPanel());
                addPage("Courses", "COURSE_PANEL", new CourseManagementPanel());
                addPage("Users",       "USER_MANAGEMENT",   new UserManagementPanel());
                addPage("Notices",     "NOTICE_MANAGEMENT", new NoticeManagementPanel());
                addPage("Departments", "DEPARTMENTS",       new DepartmentManagementPanel());
                addPage("Timetables", "TIMETABLE_MANAGE", new TimetableManagementPanel());
                addPage("Attendance", "ATTENDANCE_VIEW", new AttendanceViewPanel());
                addPage("Medicals",   "MEDICAL_VIEW",    new MedicalRecordPanel());
                addPage("Eligibility", "ELIGIBILITY", new EligibilityPanel());
                addPage("Results",     "RESULTS",     new ResultsSheetPanel());
                addPage("GPA",         "GPA",         new GpaDashboardPanel());
                break;
            case "LECTURER":
                first = "LECTURER_DASHBOARD";
                addPage("Dashboard",   first,                new LecturerDashboardPanel());
                addPage("Materials",   "MATERIALS",          new CourseMaterialsPanel());
                addPage("Marks Entry", "MARKS_ENTRY",        new MarksEntryPanel());
                addPage("Eligibility", "ELIGIBILITY",        new EligibilityPanel());
                addPage("Results",     "RESULTS",            new ResultsSheetPanel());
                addPage("GPA",         "GPA",                new GpaDashboardPanel());
                addPage("Attendance",  "ATTENDANCE_VIEW",    new AttendanceViewPanel());
                addPage("Medicals",    "MEDICAL_VIEW",       new MedicalRecordPanel());
                addPage("Timetable",   "TIMETABLE_VIEW",     new TimetableViewPanel());
                break;
            case "TECHNICAL_OFFICER":
                first = "TECHNICAL_OFFICER_DASHBOARD";
                addPage("Dashboard",        first,               new TechnicalOfficerDashboardPanel());
                addPage("Attendance Entry", "ATTENDANCE_ENTRY",  new AttendanceEntryPanel());
                addPage("Attendance View",  "ATTENDANCE_VIEW",   new AttendanceViewPanel());
                addPage("Medicals",         "MEDICAL_VIEW",      new MedicalRecordPanel());
                addPage("Report",           "ATTENDANCE_REPORT", new AttendanceReportPanel());
                addPage("Timetable",        "TIMETABLE_VIEW",    new TimetableViewPanel());
                break;
            case "UNDERGRADUATE":
                first = "UNDERGRADUATE_DASHBOARD";
                addPage("Dashboard",   first,             new UndergraduateDashboardPanel());
                addPage("Eligibility", "ELIGIBILITY",     new EligibilityPanel());
                addPage("Results",     "RESULTS",         new ResultsSheetPanel());
                addPage("GPA",         "GPA",             new GpaDashboardPanel());
                addPage("Attendance",  "ATTENDANCE_VIEW", new AttendanceViewPanel());
                addPage("Medicals",    "MEDICAL_VIEW",    new MedicalRecordPanel());
                addPage("Timetable",   "TIMETABLE_VIEW",  new TimetableViewPanel());
                break;
            default:
                throw new IllegalStateException("Unknown role");
        }

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            SessionManager.clearSession();
            dispose();
            new LoginFrame().setVisible(true);
        });
        sidebar.add(logout);

        JPanel west = new JPanel(new BorderLayout());
        west.add(sidebar, BorderLayout.NORTH);
        west.setPreferredSize(new Dimension(170, 0));
        add(west, BorderLayout.WEST);
        add(cardContainer, BorderLayout.CENTER);

        NavigationManager.showPanel(first);
    }

    private void addPage(String label, String key, JPanel panel) {
        cardContainer.add(panel, key);
        JButton b = new JButton(label);
        b.addActionListener(e -> NavigationManager.showPanel(key));
        sidebar.add(b);
    }

//    private void initComponents() {
//        cardLayout = new CardLayout();
//        cardContainer = new JPanel(cardLayout);
//
//        // Initialize Navigation Manager
//        NavigationManager.initialize(cardContainer, cardLayout);
//
//        // Add role-specific or general panels
//        cardContainer.add(new AdminDashboardPanel(), "ADMIN_DASHBOARD");
//        cardContainer.add(new LecturerDashboardPanel(), "LECTURER_DASHBOARD");
//        cardContainer.add(new TechnicalOfficerDashboardPanel(), "TECHNICAL_OFFICER_DASHBOARD");
//        cardContainer.add(new UndergraduateDashboardPanel(), "UNDERGRADUATE_DASHBOARD");
//        cardContainer.add(new CourseManagementPanel(), "COURSE_PANEL");
//        cardContainer.add(new UserManagementPanel(), "USER_MANAGEMENT");
//        cardContainer.add(new NoticeManagementPanel(), "NOTICE_MANAGEMENT");
//
//
//        add(cardContainer, BorderLayout.CENTER);
//
//        // Show dashboard based on user role
//        String userRole = SessionManager.getCurrentUser().getRole();
//        switch (userRole.toUpperCase()) {
//            case "ADMIN":
//                NavigationManager.showPanel("ADMIN_DASHBOARD");
//                addPage("Departments", "DEPARTMENTS", new DepartmentManagementPanel());
//                break;
//            case "LECTURER":
//                NavigationManager.showPanel("LECTURER_DASHBOARD");
//                break;
//            case "TECHNICAL_OFFICER":
//                NavigationManager.showPanel("TECHNICAL_OFFICER_DASHBOARD");
//                break;
//            case "UNDERGRADUATE":
//                NavigationManager.showPanel("UNDERGRADUATE_DASHBOARD");
//                break;
//            default:
//                NavigationManager.showPanel("ADMIN_DASHBOARD");
//        }
//    }
}