package ftms.view.attendance;

import ftms.model.AttendanceRecord;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Attendance ID",
            "Course Code",
            "Student ID",
            "Attendance %",
            "Session No",
            "Date",
            "Session Type",
            "Status",
            "TO ID",
            "Medical ID"
    };

    private final List<AttendanceRecord> attendanceRecords;
    private final Map<String, int[]> percentagesByStudentCourse = new HashMap<>();

    public AttendanceTableModel(List<AttendanceRecord> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
        for (AttendanceRecord record : attendanceRecords) {
            int[] totals = percentagesByStudentCourse.computeIfAbsent(
                    key(record), ignored -> new int[2]);
            totals[1]++;
            if ("Present".equals(record.getStatus())) {
                totals[0]++;
            }
        }
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
                int[] totals = percentagesByStudentCourse.get(key(record));
                return String.format("%.2f%% (%d/%d)",
                        totals[0] * 100.0 / totals[1], totals[0], totals[1]);
            case 4:
                return record.getSessionNo();
            case 5:
                return record.getDate();
            case 6:
                return record.getSessionType();
            case 7:
                return record.getStatus();
            case 8:
                return record.getToId();
            case 9:
                return record.getMedicalId();
            default:
                return null;
        }
    }

    private String key(AttendanceRecord record) {
        return record.getStudentId() + "\u0000" + record.getCourseCode();
    }
}