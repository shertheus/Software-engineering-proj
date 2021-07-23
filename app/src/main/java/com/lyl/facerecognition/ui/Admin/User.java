package com.lyl.facerecognition.ui.Admin;

//使用饿汉模式实现单例的登录用户信息记录
//但是此处是存储用户信息的副本，这点是否合适有待考虑，可以考虑使用User类
//LoginUser相对于模拟登陆，并且作为存储数据库的一个缓冲区
class User {
    private static User _user = new User();
    private long id;
    private String name;
    private String password;
    private Integer remember;
    private byte[] portrait;
    private String region;
    private String gender;
    private String brithday;

    public static User getInstance(){
        return _user;
    }

    public byte[] getPortrait() {
        return portrait;
    }

    public void setPortrait(byte[] portrait) {
        this.portrait = portrait;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBrithday() {
        return brithday;
    }

    public void setBrithday(String brithday) {
        this.brithday = brithday;
    }

    public Integer getRemember() {
        return remember;
    }

    public void setRemember(Integer remember) {
        this.remember = remember;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
