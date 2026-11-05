package ftms.view.marks;

import ftms.model.SemesterResult;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ResultsTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "Academic Year", "Semester", "Credits", "SGPA", "CGPA"
    };

    private final List<SemesterResult> results;

    public ResultsTableModel(List<SemesterResult> results) {
        this.results = new ArrayList<>(results);
    }

    @Override
    public int getRowCount() {
        return results.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column >= 2 ? Double.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        SemesterResult result = results.get(row);
        switch (column) {
            case 0:
                return result.getAcademicYear();
            case 1:
                return result.getSemester();
            case 2:
                return result.getTotalCredits();
            case 3:
                return result.getSgpa();
            case 4:
                return result.getCgpa();
            default:
                throw new IndexOutOfBoundsException("Unknown result column: " + column);
        }
    }
}
