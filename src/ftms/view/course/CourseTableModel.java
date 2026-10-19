package ftms.view.course;

import ftms.model.Course;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CourseTableModel extends AbstractTableModel {

    private final String[] columns = {"Code", "Name", "Credits", "Theory", "Practical"};
    private List<Course> courses;

    public CourseTableModel(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public int getRowCount() { return courses.size(); }

    @Override
    public int getColumnCount() { return columns.length; }

    @Override
    public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Course c = courses.get(row);
        switch (col) {
            case 0: return c.getCourseCode();
            case 1: return c.getCourseName();
            case 2: return c.getCreditHours();
            case 3: return c.isHasTheory();
            case 4: return c.isHasPractical();
            default: return null;
        }
    }

    // Returns the Course object of one row (used by the panel for selection).
    public Course getCourseAt(int row) {
        return courses.get(row);
    }
}