package com.example.studentmanagement.model;

public class User {
    private String id;
    private String name;
    private int age;
    private String phoneNumber;
    private boolean isLocked;
    private int role; // 0: employee, 1: manager, 2: admin

    private String avatar;

    public User() {
    }

    public User(String id, String name, int age, String phoneNumber, int role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.isLocked = false;
        this.role = role;
    }

    public User(String id, String name, int age, String phoneNumber, boolean isLocked, int role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.isLocked = isLocked;
        this.role = role;
    }

    public User(String id, String name, int age, String phoneNumber, boolean isLocked, int role, String avatar) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.isLocked = isLocked;
        this.role = role;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
