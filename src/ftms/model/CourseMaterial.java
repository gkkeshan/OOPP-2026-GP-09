package com.fot.management.models;

import java.time.LocalDate;

public class CourseMaterial extends BaseEntity {

    private int materialId;
    private String courseCode;
    private String title;
    private String filePath;
    private LocalDate uploadedDate;

    public CourseMaterial(int materialId, String courseCode, String title, String filePath, LocalDate uploadedDate) {
        this.materialId = materialId;
        setCourseCode(courseCode);
        setTitle(title);
        setFilePath(filePath);
        setUploadedDate(uploadedDate);
    }

    public CourseMaterial(String courseCode, String title, String filePath, LocalDate uploadedDate) {
        this(0, courseCode, title, filePath, uploadedDate);
    }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = requireText(courseCode, "Course code", 10).toUpperCase(); }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = requireText(title, "Title", 150); }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = requireText(filePath, "File path", 255); }

    public LocalDate getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(LocalDate uploadedDate) { this.uploadedDate = requireNotNull(uploadedDate, "Uploaded date"); }

    @Override public Object getId() { return materialId; }
    @Override public String display() { return courseCode + ": " + title; }
}