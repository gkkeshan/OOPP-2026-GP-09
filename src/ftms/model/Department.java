package com.fot.management.models;


public class Department extends BaseEntity {

    private int departmentId;
    private String departmentName;

    public Department(int departmentId, String departmentName) {
        this.departmentId = departmentId;
        setDepartmentName(departmentName);
    }


    public Department(String departmentName) {
        this(0, departmentName);
    }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) {
        this.departmentName = requireText(departmentName, "Department name", 100);
    }

    @Override public Object getId() { return departmentId; }
    @Override public String display() { return departmentName; }
}