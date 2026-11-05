package ftms.view.course;

import ftms.model.Batch;
import ftms.model.Course;
import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseTableModel extends AbstractTableModel {

    private final String[] columns = {"Code", "Name", "Level", "Batch", "Credits", "Theory", "Practical"};
    private final List<Course> courses;
    private final Map<Integer, String> batchNames = new HashMap<>();

    public CourseTableModel(List<Course> courses, List<Batch> batches) {
        this.courses = courses;
        for (Batch batch : batches) {
            batchNames.put(batch.getBatchId(), batch.getBatchId() + " - " + batch.display());
        }
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
            case 2: return c.getLevel();
            case 3: return batchNames.getOrDefault(c.getBatchId(), String.valueOf(c.getBatchId()));
            case 4: return c.getCreditHours();
            case 5: return c.isHasTheory();
            case 6: return c.isHasPractical();
            default: return null;
        }
    }

    // Returns the Course object of one row (used by the panel for selection).
    public Course getCourseAt(int row) {
        return courses.get(row);
    }
}