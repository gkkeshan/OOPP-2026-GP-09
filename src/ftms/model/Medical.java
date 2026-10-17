package ftms.model;

import java.time.LocalDate;

public class Medical {

    private int medicalId;
    private int studentId;
    private int toId;
    private LocalDate medicalDate;
    private LocalDate submittedDate;
    private String approvalStatus;
    private String document;

    public Medical() {
        this.approvalStatus = "Pending";
    }

    public Medical(int medicalId, int studentId, int toId,
                   LocalDate medicalDate, LocalDate submittedDate,
                   String approvalStatus, String document) {
        this.medicalId = medicalId;
        this.studentId = studentId;
        this.toId = toId;
        this.medicalDate = medicalDate;
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

    public LocalDate getMedicalDate() {
        return medicalDate;
    }

    public void setMedicalDate(LocalDate medicalDate) {
        this.medicalDate = medicalDate;
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