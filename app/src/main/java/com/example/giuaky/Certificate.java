package com.example.giuaky;

import java.io.Serializable;

public class Certificate implements Serializable {
    private String name;          // Name of the certificate
    private String issuedBy;      // Organization that issued the certificate
    private String dateIssued;     // Date the certificate was issued
    private double score;         // Exam score associated with the certificate
    private String remarks;       // Additional remarks (optional)

    // Constructor
    public Certificate(String name, String issuedBy, String dateIssued, double score, String remarks) {
        this.name = name;
        this.issuedBy = issuedBy;
        this.dateIssued = dateIssued;
        this.score = score;
        this.remarks = remarks;
    }

    // Getters and Setters
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