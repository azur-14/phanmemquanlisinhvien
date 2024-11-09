package com.example.giuaky;

public class Student extends User {

    private String studentID;
    private String faculty;
    private String major;

    public Student(String role, String name, String email, String phoneNumber, String status ,String studentID, int age,String faculty, String major) {
        super(role, name, email, phoneNumber, age, status);
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

