package ftms.view.marks;

import ftms.model.EligibilityStatus;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EligibilityTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"Student ID", "Course Code", "CA %", "Eligible"};
    private final List<EligibilityStatus> statuses = new ArrayList<>();

    @Override
    public int getRowCount() {
        return statuses.size();
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
        return column == 2 ? Double.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        EligibilityStatus status = statuses.get(row);
        switch (column) {
            case 0:
                return status.getStudentId();
            case 1:
                return status.getCourseCode();
            case 2:
                return status.getCaPercentage();
            case 3:
                return status.isEligible() ? "Eligible" : "Not eligible";
            default:
                throw new IndexOutOfBoundsException("Unknown eligibility column: " + column);
        }
    }

    public void addStatus(EligibilityStatus status) {
        int row = statuses.size();
        statuses.add(status);
        fireTableRowsInserted(row, row);
    }
}
