package ftms.view.admin;

import ftms.model.Department;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class DepartmentTableModel extends AbstractTableModel {

    private List<Department> departments;
    private String[] columns = {"ID", "Name"};

    public DepartmentTableModel(List<Department> departments) {
        this.departments = departments;
    }

    public int getRowCount() {
        return departments.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    public Object getValueAt(int row, int col) {
        Department d = departments.get(row);
        switch (col) {
            case 0: return d.getDepartmentId();
            case 1: return d.getDepartmentName();
            default: return null;
        }
    }

    public Department getDepartmentAt(int row) {
        return departments.get(row);
    }
}