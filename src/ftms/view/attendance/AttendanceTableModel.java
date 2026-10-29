package ftms.view.attendance;

import ftms.model.AttendanceRecord;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class AttendanceTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Attendance ID",
            "Course Code",
            "Student ID",
            "Session No",
            "Date",
            "Session Type",
            "Status",
            "TO ID",
            "Medical ID"
    };

    private final List<AttendanceRecord> attendanceRecords;

    public AttendanceTableModel(List<AttendanceRecord> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }

    @Override
    public int getRowCount() {
        return attendanceRecords.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        AttendanceRecord record = attendanceRecords.get(row);

        switch (col) {
            case 0:
                return record.getAttendanceId();
            case 1:
                return record.getCourseCode();
            case 2:
                return record.getStudentId();
            case 3:
                return record.getSessionNo();
            case 4:
                return record.getDate();
            case 5:
                return record.getSessionType();
            case 6:
                return record.getStatus();
            case 7:
                return record.getToId();
            case 8:
                return record.getMedicalId();
            default:
                return null;
        }
    }
}