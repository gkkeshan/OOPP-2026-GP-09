package ftms.view.course;

import ftms.model.EnrolledCourse;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class EnrolledCourseTableModel extends AbstractTableModel {

    private final String[] columns = {"Code", "Name", "Credits", "Type",
            "Academic Year", "Semester", "Status"};
    private List<EnrolledCourse> rows;

    public EnrolledCourseTableModel(List<EnrolledCourse> rows) {
        this.rows = rows;
    }

    @Override
    public int getRowCount() { return rows.size(); }

    @Override
    public int getColumnCount() { return columns.length; }

    @Override
    public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        EnrolledCourse c = rows.get(row);
        switch (col) {
            case 0: return c.getCourseCode();
            case 1: return c.getCourseName();
            case 2: return c.getCreditHours();
            case 3: return c.getCourseType();
            case 4: return c.getAcademicYear();
            case 5: return c.getSemester();
            case 6: return c.getStatus();
            default: return null;
        }
    }
}