package com.example.giuaky;

public class Student {
    private String name;
    private String studentId; // Add studentId field
    private int age; // or any other fields you need
    private String phoneNumber;
    private String status;

    public Student() {
        // Firestore requires an empty constructor
    }

    public Student(String name, String studentId, int age, String phoneNumber, String status) {
        this.name = name;
        this.studentId = studentId; // Initialize studentId
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId; // Getter for studentId
    }

    public int getAge() {
        return age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }
}