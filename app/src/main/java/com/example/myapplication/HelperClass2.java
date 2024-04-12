package com.example.myapplication;

public class HelperClass2 {


    String email, password,status;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HelperClass2(String email, String password,String status) {
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public HelperClass2() {
    }
}
