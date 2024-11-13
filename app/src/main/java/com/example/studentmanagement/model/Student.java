package com.example.studentmanagement.model;
import java.io.Serializable;

public class Student extends User implements Serializable {

    private String studentID;
    private String faculty;
    private String major;

    public Student(String email, String name ,int age, String phonenumber,Boolean status, int role,String studentID, String faculty,String major) {
        super(email,name,age,phonenumber,status,role);
        this.studentID = studentID;
        this.faculty = faculty;
        this.major = major;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
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
