package ftms.view.marks;

import ftms.model.Mark;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MarkTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"Student ID", "Marks", "Grade"};

    private final List<Mark> marks;
    private final Set<String> modifiedMarkIds = new HashSet<>();

    public MarkTableModel(List<Mark> marks) {
        this.marks = new ArrayList<>(marks);
    }

    @Override
    public int getRowCount() {
        return marks.size();
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
        return column == 1 ? Double.class : String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Mark mark = marks.get(row);
        switch (column) {
            case 0:
                return mark.getStudentId();
            case 1:
                return mark.getMarks();
            case 2:
                return mark.calculateGrade();
            default:
                throw new IndexOutOfBoundsException("Unknown mark column: " + column);
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (column != 1) {
            return;
        }

        double newMarks;
        try {
            newMarks = Double.parseDouble(String.valueOf(value).trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Marks must be a number.", exception);
        }

        Mark mark = marks.get(row);
        mark.setMarks(newMarks);
        modifiedMarkIds.add(mark.getMarkId());
        fireTableRowsUpdated(row, row);
    }

    public Mark getMarkAt(int row) {
        return marks.get(row);
    }

    public List<Mark> getModifiedMarks() {
        List<Mark> modified = new ArrayList<>();
        for (Mark mark : marks) {
            if (modifiedMarkIds.contains(mark.getMarkId())) {
                modified.add(mark);
            }
        }
        return modified;
    }

    public void markChangesSaved() {
        modifiedMarkIds.clear();
    }
}
