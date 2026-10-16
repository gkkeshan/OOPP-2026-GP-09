package com.fot.management.models;


public class Batch extends BaseEntity {

    private int batchId;
    private String batchName;
    private String academicYear;
    private int departmentId;

    public Batch(int batchId, String batchName, String academicYear, int departmentId) {
        this.batchId = batchId;
        setBatchName(batchName);
        setAcademicYear(academicYear);
        setDepartmentId(departmentId);
    }

    public Batch(String batchName, String academicYear, int departmentId) {
        this(0, batchName, academicYear, departmentId);
    }

    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) { this.batchId = batchId; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = requireText(batchName, "Batch name", 30); }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = requireText(academicYear, "Academic year", 9); }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) {
        if (departmentId <= 0) throw new IllegalArgumentException("A batch must belong to a department");
        this.departmentId = departmentId;
    }

    @Override public Object getId() { return batchId; }
    @Override public String display() { return batchName + " (" + academicYear + ")"; }
}