
package ftms.model;

import java.time.LocalDate;

public class Medical {

    private int medicalId;
    private int studentId;
    private int toId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate submittedDate;
    private String approvalStatus;
    private String document;

    public Medical() {
        this.approvalStatus = "Pending";
    }

    public Medical(int medicalId, int studentId, int toId,
                   LocalDate startDate, LocalDate endDate,
                   LocalDate submittedDate,
                   String approvalStatus, String document) {
        this.medicalId = medicalId;
        this.studentId = studentId;
        this.toId = toId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.submittedDate = submittedDate;
        setApprovalStatus(approvalStatus);
        this.document = document;
    }

    public int getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(int medicalId) {
        this.medicalId = medicalId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDate submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        if (approvalStatus != null
                && !approvalStatus.equals("Pending")
                && !approvalStatus.equals("Approved")
                && !approvalStatus.equals("Rejected")) {
            throw new IllegalArgumentException(
                    "Status must be Pending, Approved or Rejected");
        }

        this.approvalStatus = approvalStatus;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
