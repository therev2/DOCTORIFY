package com.example.myapplication;

public class HelperClass {

    String name, email, password, exp, charge, time, degree, speacilist, image,visibility;

    public HelperClass(String email, String password, String name, String exp, String charge, String time, String degree, String speacilist, String image,String visibility) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.exp = exp;
        this.charge = charge;
        this.time = time;
        this.degree = degree;
        this.speacilist = speacilist;
        this.image = image;
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getvisibility() {
        return visibility;
    }

    public void setvisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSpeacilist() {
        return speacilist;
    }

    public void setSpeacilist(String speacilist) {
        this.speacilist = speacilist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public HelperClass() {
    }



}
