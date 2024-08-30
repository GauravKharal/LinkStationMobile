package com.example.linkstation.model;

import java.util.Date;

public class UpdateUserDetailsRequest {
    private String fullName;
    private Date dateOfBirth;

    public UpdateUserDetailsRequest(String fullName, Date dateOfBirth) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName() {
        return fullName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
