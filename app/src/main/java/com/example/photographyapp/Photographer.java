package com.example.photographyapp;

public class Photographer {
    public String id;
    public String fullName;
    public String studioName;
    public String avatarUrl;

    public Photographer(String id, String fullName, String studioName, String avatarUrl) {
        this.id = id;
        this.fullName = fullName;
        this.studioName = studioName;
        this.avatarUrl = avatarUrl;
    }
}