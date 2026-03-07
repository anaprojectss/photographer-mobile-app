package com.example.photographyapp;

public class Booking {
    public String id;
    public String clientId;
    public String photographerId;
    public String date;
    public String location;
    public String shootType;
    public String hours;
    public String status;

    public Booking(String id, String clientId, String photographerId,
                   String date, String location, String shootType,
                   String hours, String status) {
        this.id = id;
        this.clientId = clientId;
        this.photographerId = photographerId;
        this.date = date;
        this.location = location;
        this.shootType = shootType;
        this.hours = hours;
        this.status = status;
    }
}