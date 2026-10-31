package ftms.service;

import ftms.dao.DashboardDAO;
import ftms.exception.DatabaseException;

public class DashboardService {
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public int countUsers() throws DatabaseException { return wrap(() -> dashboardDAO.countUsers()); }
    public int countCourses() throws DatabaseException { return wrap(() -> dashboardDAO.countCourses()); }
    public int countStudents() throws DatabaseException { return wrap(() -> dashboardDAO.countStudents()); }
    public int countMedicals() throws DatabaseException { return wrap(() -> dashboardDAO.countMedicals()); }
    public int countPendingMedicals() throws DatabaseException { return wrap(() -> dashboardDAO.countPendingMedicals()); }
    public int countNotices() throws DatabaseException { return wrap(() -> dashboardDAO.countNotices()); }
    public int countActiveEnrollments(int studentId) throws DatabaseException { return wrap(() -> dashboardDAO.countActiveEnrollments(studentId)); }
    public int countPendingMedicalsFor(int studentId) throws DatabaseException { return wrap(() -> dashboardDAO.countPendingMedicalsFor(studentId)); }

    public Double latestCgpa(int studentId) throws DatabaseException {
        return wrap(() -> dashboardDAO.latestCgpa(studentId));
    }

    public Integer attendancePercent(int studentId) throws DatabaseException {
        return wrap(() -> dashboardDAO.attendancePercent(studentId));
    }

    private interface DaoCall<T> { T run() throws Exception; }

    private <T> T wrap(DaoCall<T> call) throws DatabaseException {
        try {
            return call.run();
        } catch (Exception e) {
            throw new DatabaseException("Failed to load dashboard statistics.", e);
        }
    }
}