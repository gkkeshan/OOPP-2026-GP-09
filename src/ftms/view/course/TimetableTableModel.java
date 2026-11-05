package ftms.view.course;

import ftms.model.Timetable;

import javax.swing.table.AbstractTableModel;
import java.util.List;


public class TimetableTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Course", "Batch", "Day", "Start", "End", "Type", "Venue"};
    private List<Timetable> sessions;

    public TimetableTableModel(List<Timetable> sessions) {
        this.sessions = sessions;
    }

    public int getRowCount() { return sessions.size(); }
    public int getColumnCount() { return columns.length; }
    public String getColumnName(int col) { return columns[col]; }

    public Object getValueAt(int row, int col) {
        Timetable t = sessions.get(row);
        switch (col) {
            case 0: return t.getTimetableId();
            case 1: return t.getCourseCode();
            case 2: return t.getBatchId();
            case 3: return t.getDayOfWeek();
            case 4: return t.getStartTime();
            case 5: return t.getEndTime();
            case 6: return t.getSessionType().name();
            case 7: return t.getVenueId();
            default: return null;
        }
    }


    public Timetable getTimetableAt(int row) {
        return sessions.get(row);
    }
}