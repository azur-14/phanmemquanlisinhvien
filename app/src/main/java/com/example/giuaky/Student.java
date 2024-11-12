package com.example.giuaky;

public class Student {

    private String studentID;
    private String name;
    private String faculty;
    private String major;

    public Student(String studentID, String name, String faculty, String major) {
        this.studentID = studentID;
        this.name = name;
        this.faculty = faculty;
        this.major = major;
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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}

