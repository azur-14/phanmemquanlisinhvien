package com.example.giuaky;

import java.io.Serializable;
import java.util.List;

public class Student implements Serializable{

    private String studentID;
    private String name;
    private String faculty;
    private String major;
    private String phonenumber;
    private int age;
    private String email;
    private String image;
    private List<Certificate> certificates;
    public  Student(String studentID, String name, String faculty, String major, int age, String phonenumber, String email, String image) {
        this.studentID = studentID;
        this.name = name;
        this.faculty = faculty;
        this.major = major;
        this.age=age;
        this.phonenumber = phonenumber;
        this.email=email;
        this.image = image;
    }

    public Student(String mssv, String name, String faculty, int i, String faculty1, String major, String email, String phonenumber, String image) {
    }

    public String getStudentID() {
        return studentID;
    }
    public int getAge() {
        return age;
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
    public void setAge(int age) {
        this.age = age;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhonenumber(String phone) {
        this.phonenumber = phone;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phonenumber;
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
    public void setImage(String image) {
        this.image = image;
    }
    public String getImage() {
        return image;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}

