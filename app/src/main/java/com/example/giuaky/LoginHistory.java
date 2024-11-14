package com.example.giuaky;

import java.util.Date;

public class LoginHistory {
    private String id;
    private Date loginDate;
    private String location;

    public LoginHistory() {
    }

    public LoginHistory(String id, Date loginDate, String location) {
        this.id = id;
        this.loginDate = loginDate;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
