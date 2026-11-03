package ftms.view;

import ftms.util.NavigationManager;
import ftms.util.SessionManager;
import ftms.util.Theme;
import ftms.view.attendance.AttendanceEntryPanel;
import ftms.view.attendance.AttendanceReportPanel;
import ftms.view.attendance.AttendanceViewPanel;
import ftms.view.attendance.MedicalRecordPanel;
import ftms.view.course.*;
import ftms.view.marks.EligibilityPanel;
import ftms.view.marks.GpaDashboardPanel;
import ftms.view.marks.MarksEntryPanel;
import ftms.view.marks.ResultsSheetPanel;
import ftms.view.notice.NoticeBoardPanel;
import ftms.view.profile.ProfilePanel;
import ftms.view.users.admin.AdminDashboardPanel;
import ftms.view.users.admin.DepartmentManagementPanel;
import ftms.view.users.admin.NoticeManagementPanel;
import ftms.view.users.admin.UserManagementPanel;
import ftms.view.users.lecturer.LecturerDashboardPanel;
import ftms.view.users.technical.TechnicalOfficerDashboardPanel;
import ftms.view.users.undergraduate.UndergraduateDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel cardContainer;
    private CardLayout cardLayout;
    private JPanel sidebar;
    private final List<JButton> navButtons = new ArrayList<>();

    public MainFrame() {
        setTitle("FTAMS - Faculty Management System (" + SessionManager.getCurrentUser().getRole() + ")");
        setSize(1020, 680);
        setMinimumSize(new Dimension(880, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(Theme.CLOUD);
        NavigationManager.initialize(cardContainer, cardLayout);

        // Sidebar panel with INK background (#1E1E1E)
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.INK);
        sidebar.putClientProperty("customBackground", Boolean.TRUE);

        String first;
        switch (SessionManager.getCurrentUser().getRole().trim().toUpperCase()) {
            case "ADMIN":
                first = "ADMIN_DASHBOARD";
                addPage("Dashboard",   "ADMIN_DASHBOARD",   new AdminDashboardPanel());
                addPage("Courses",     "COURSE_PANEL",      new CourseManagementPanel());
                addPage("Users",       "USER_MANAGEMENT",   new UserManagementPanel());
                addPage("Notices",     "NOTICE_MANAGEMENT", new NoticeManagementPanel());
                addPage("Departments", "DEPARTMENTS",       new DepartmentManagementPanel());
                addPage("Timetables",  "TIMETABLE_MANAGE",  new TimetableManagementPanel());
                addPage("Attendance",  "ATTENDANCE_VIEW",   new AttendanceViewPanel());
                addPage("Medicals",    "MEDICAL_VIEW",      new MedicalRecordPanel());
                addPage("Eligibility", "ELIGIBILITY",       new EligibilityPanel());
                addPage("Results",     "RESULTS",           new ResultsSheetPanel());
                addPage("GPA",         "GPA",               new GpaDashboardPanel());
                addPage("Profile",     "PROFILE",           new ProfilePanel());
                break;
            case "LECTURER":
                first = "LECTURER_DASHBOARD";
                addPage("Dashboard",   first,                new LecturerDashboardPanel());
                addPage("Notices",     "NOTICE_BOARD",       new NoticeBoardPanel());
                addPage("Materials",   "MATERIALS",          new CourseMaterialsPanel());
                addPage("Marks Entry", "MARKS_ENTRY",        new MarksEntryPanel());
                addPage("Eligibility", "ELIGIBILITY",        new EligibilityPanel());
                addPage("Results",     "RESULTS",            new ResultsSheetPanel());
                addPage("GPA",         "GPA",                new GpaDashboardPanel());
                addPage("Attendance",  "ATTENDANCE_VIEW",    new AttendanceViewPanel());
                addPage("Medicals",    "MEDICAL_VIEW",       new MedicalRecordPanel());
                addPage("Timetable",   "TIMETABLE_VIEW",     new TimetableViewPanel());
                addPage("Profile",     "PROFILE",            new ProfilePanel());
                break;
            case "TECHNICAL_OFFICER":
                first = "TECHNICAL_OFFICER_DASHBOARD";
                addPage("Dashboard",        first,               new TechnicalOfficerDashboardPanel());
                addPage("Notices",          "NOTICE_BOARD",      new NoticeBoardPanel());
                addPage("Attendance Entry", "ATTENDANCE_ENTRY",  new AttendanceEntryPanel());
                addPage("Attendance View",  "ATTENDANCE_VIEW",   new AttendanceViewPanel());
                addPage("Medicals",         "MEDICAL_VIEW",      new MedicalRecordPanel());
                addPage("Report",           "ATTENDANCE_REPORT", new AttendanceReportPanel());
                addPage("Timetable",        "TIMETABLE_VIEW",    new TimetableViewPanel());
                addPage("Profile",          "PROFILE",           new ProfilePanel());
                break;
            case "UNDERGRADUATE":
                first = "UNDERGRADUATE_DASHBOARD";
                addPage("Dashboard",   first,             new UndergraduateDashboardPanel());
                addPage("Notices",     "NOTICE_BOARD",    new NoticeBoardPanel());
                addPage("Eligibility", "ELIGIBILITY",     new EligibilityPanel());
                addPage("Results",     "RESULTS",         new ResultsSheetPanel());
                addPage("GPA",         "GPA",             new GpaDashboardPanel());
                addPage("Attendance",  "ATTENDANCE_VIEW", new AttendanceViewPanel());
                addPage("Medicals",    "MEDICAL_VIEW",    new MedicalRecordPanel());
                addPage("Timetable",   "TIMETABLE_VIEW",  new TimetableViewPanel());
                addPage("Courses",     "MY_COURSES",      new MyCoursesPanel());
                addPage("Profile",     "PROFILE",         new ProfilePanel());
                break;
            default:
                throw new IllegalStateException("Unknown role");
        }

        // West Sidebar container with Brand header & Logout button
        JPanel west = new JPanel(new BorderLayout());
        west.setBackground(Theme.INK);
        west.setPreferredSize(new Dimension(190, 0));
        west.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(40, 40, 40)));

        // Sidebar Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(Theme.INK);
        brandPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 45)),
                BorderFactory.createEmptyBorder(18, 16, 16, 16)
        ));

        JLabel brandTitle = new JLabel("FTMS");
        brandTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        brandTitle.setForeground(Theme.WHITE);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        String roleName = SessionManager.getCurrentUser().getRole().replace("_", " ");
        JLabel brandSubtitle = new JLabel(roleName.toUpperCase());
        brandSubtitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        brandSubtitle.setForeground(Theme.GREY_TEXT);
        brandSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(brandTitle);
        brandPanel.add(Box.createVerticalStrut(3));
        brandPanel.add(brandSubtitle);

        // Scrollable sidebar buttons
        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setBorder(BorderFactory.createEmptyBorder());
        sidebarScroll.setBackground(Theme.INK);
        sidebarScroll.getViewport().setBackground(Theme.INK);
        sidebarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Logout Button at bottom
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Theme.INK);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(45, 45, 45)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JButton logout = new JButton("Logout");
        Theme.styleSidebarButton(logout);
        logout.putClientProperty("isSidebarBtn", Boolean.TRUE);
        logout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.INK_SOFT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        logout.addActionListener(e -> {
            SessionManager.clearSession();
            dispose();
            new LoginFrame().setVisible(true);
        });
        bottomBar.add(logout, BorderLayout.CENTER);

        west.add(brandPanel, BorderLayout.NORTH);
        west.add(sidebarScroll, BorderLayout.CENTER);
        west.add(bottomBar, BorderLayout.SOUTH);

        add(west, BorderLayout.WEST);
        add(cardContainer, BorderLayout.CENTER);

        // Activate first page
        if (!navButtons.isEmpty()) {
            setActiveButton(navButtons.get(0));
        }
        NavigationManager.showPanel(first);
    }

    private void addPage(String label, String key, JPanel panel) {
        Theme.applyTheme(panel);
        cardContainer.add(panel, key);

        JButton b = new JButton(label);
        b.putClientProperty("isSidebarBtn", Boolean.TRUE);
        Theme.styleSidebarButton(b);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);

        b.addActionListener(e -> {
            setActiveButton(b);
            NavigationManager.showPanel(key);
        });

        navButtons.add(b);
        sidebar.add(b);
    }

    private void setActiveButton(JButton activeBtn) {
        for (JButton b : navButtons) {
            if (b == activeBtn) {
                b.setBackground(Theme.INK_SOFT);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 1, 0, Theme.WHITE),
                        BorderFactory.createEmptyBorder(10, 10, 10, 14)
                ));
            } else {
                b.setBackground(Theme.INK);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 45)),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            }
        }
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