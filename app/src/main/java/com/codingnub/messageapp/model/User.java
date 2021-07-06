package com.codingnub.messageapp.model;

public class User {


    private String uid;
    private String name;
    private String email;
    private String imgUrl;
    private String status;
    private String search;

    public User(String uid, String name, String email, String imgUrl, String status, String search) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imgUrl = imgUrl;
        this.status = status;
        this.search = search;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
