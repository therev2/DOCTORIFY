package com.example.myapplication;

public class HelperClass3 {
    String pat_email, doc_email, date, time;


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


    public HelperClass3(String pat_email, String doc_email, String date, String time) {
        this.pat_email = pat_email;
        this.doc_email = doc_email;
        this.date = date;
        this.time = time;
    }

    public HelperClass3() {
    }
}
