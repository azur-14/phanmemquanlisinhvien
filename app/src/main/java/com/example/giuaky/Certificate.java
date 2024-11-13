package com.example.giuaky;

import android.widget.TextView;

import java.io.Serializable;

public class Certificate implements Serializable {
    private static String certificateID;   // Unique identifier for the certificate
    private String studentID;       // ID of the student to whom the certificate belongs
    private String name;            // Name of the certificate
    private String issuedBy;        // Organization that issued the certificate
    private String dateIssued;      // Date the certificate was issued
    private double score;           // Exam score associated with the certificate
    private String remarks;         // Additional remarks (optional)

    // Constructor
    public Certificate(String certificateID, String studentID, String name, String issuedBy, String dateIssued, double score, String remarks) {
        this.certificateID = certificateID;
        this.studentID = studentID;
        this.name = name;
        this.issuedBy = issuedBy;
        this.dateIssued = dateIssued;
        this.score = score;
        this.remarks = remarks != null ? remarks : "";
    }

    // Getters and Setters
    public static String getCertificateID() {
        return certificateID;
    }

    public void setCertificateID(String certificateID) {
        this.certificateID = certificateID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
