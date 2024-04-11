package com.example.myapplication;

public class HelperClass3 {
    String pat_email, doc_email, date, time, pat_name,doc_name,doc_image;


    public String getPat_email() {
        return pat_email;
    }

    public void setPat_email(String pat_email) {
        this.pat_email = pat_email;
    }

    public String getDoc_email() {
        return doc_email;
    }

    public void setDoc_email(String doc_email) {
        this.doc_email = doc_email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPat_name() {
        return pat_name;
    }

    public void setPat_name(String pat_name) {
        this.pat_name = pat_name;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getDoc_image() {
        return doc_image;
    }

    public void setDoc_image(String doc_image) {
        this.doc_image = doc_image;
    }


    public HelperClass3(String pat_email, String doc_email, String date, String time, String pat_name, String doc_name, String doc_image) {
        this.pat_email = pat_email;
        this.doc_email = doc_email;
        this.date = date;
        this.time = time;
        this.pat_name = pat_name;
        this.doc_name = doc_name;
        this.doc_image = doc_image;
    }

    public HelperClass3() {
    }
}
