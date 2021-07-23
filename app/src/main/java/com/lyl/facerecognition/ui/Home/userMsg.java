package com.lyl.facerecognition.ui.Home;

public class userMsg{
    public String id;
    public String username;
    public String photo;
    public String gender;
    public String birth;
    public String laster;
    public userMsg(String id, String username, String photo, String gender, String birth){
        this.id = id;
        this.username = username;
        this.birth = birth;
        this.gender = gender;
        this.photo = photo;
    }
    public void setLaster(String ss){
        this.laster = ss;
    }
}