package ftms.view.attendance;

import ftms.model.Medical;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class MedicalTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Medical ID",
            "Student ID",
            "TO ID",
            "Medical Date",
            "Submitted Date",
            "Approval Status",
            "Document"
    };

    private final List<Medical> medicalRecords;

    public MedicalTableModel(List<Medical> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    @Override
    public int getRowCount() {
        return medicalRecords.size();
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
        Medical m = medicalRecords.get(row);

        switch (col) {
            case 0:
                return m.getMedicalId();
            case 1:
                return m.getStudentId();
            case 2:
                return m.getToId();
            case 3:
                return m.getMedicalDate();
            case 4:
                return m.getSubmittedDate();
            case 5:
                return m.getApprovalStatus();
            case 6:
                return m.getDocument();
            default:
                return null;
        }
    }
}
