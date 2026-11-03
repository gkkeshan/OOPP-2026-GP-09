package ftms.service;

import ftms.dao.CourseDAO;
import ftms.dao.DepartmentDAO;
import ftms.exception.CourseNotFoundException;
import ftms.model.Course;
import ftms.model.Department;
import ftms.model.EnrolledCourse;

import java.util.List;

public class CourseService {

    private CourseDAO courseDAO = new CourseDAO();
    private DepartmentDAO departmentDAO = new DepartmentDAO();

    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    public Course getCourse(String courseCode) throws CourseNotFoundException {
        Course course = courseDAO.findById(courseCode);
        if (course == null) {
            throw new CourseNotFoundException("Course not found: " + courseCode);
        }
        return course;
    }

    public void addCourse(Course course) {
        if (courseDAO.existsById(course.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + course.getCourseCode());
        }
        courseDAO.insert(course);
    }

    public void updateCourse(Course course) throws CourseNotFoundException {
        if (!courseDAO.existsById(course.getCourseCode())) {
            throw new CourseNotFoundException("Course not found: " + course.getCourseCode());
        }
        courseDAO.update(course);
    }

    public void deleteCourse(String courseCode) throws CourseNotFoundException {
        if (!courseDAO.existsById(courseCode)) {
            throw new CourseNotFoundException("Course not found: " + courseCode);
        }
        courseDAO.deleteById(courseCode);
    }

    // ---------- departments (reference data for courses) ----------

    public List<Department> getDepartments() {
        return departmentDAO.findAll();
    }

    public Department addDepartment(String departmentName) {
        if (departmentName == null || departmentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty.");
        }
        String name = departmentName.trim();
        if (departmentDAO.nameExists(name)) {
            throw new IllegalArgumentException("Department already exists: " + name);
        }
        int newId = departmentDAO.insert(new Department(name));
        return departmentDAO.findById(newId);
    }

    public void updateDepartment(int departmentId, String newDepartmentName) {
        if (newDepartmentName == null || newDepartmentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty.");
        }
        String name = newDepartmentName.trim();
        Department department = departmentDAO.findById(departmentId);
        if (department == null) {
            throw new IllegalArgumentException("Department not found: " + departmentId);
        }
        Department sameName = departmentDAO.findByName(name);
        if (sameName != null && sameName.getDepartmentId() != departmentId) {
            throw new IllegalArgumentException("Department already exists: " + name);
        }
        department.setDepartmentName(name);
        departmentDAO.update(department);
    }

    public void deleteDepartment(int departmentId) {
        if (departmentDAO.findById(departmentId) == null) {
            throw new IllegalArgumentException("Department not found: " + departmentId);
        }
        departmentDAO.deleteById(departmentId);
    }
    public List<Course> getCoursesByStudent(int userId) {
        return courseDAO.findByStudent(userId);
    }
    public List<EnrolledCourse> getEnrolledCourseDetails(int userId) {
        return courseDAO.findEnrolledCoursesByStudent(userId);
    }
}