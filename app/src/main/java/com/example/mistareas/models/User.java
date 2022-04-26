package com.example.mistareas.models;

public class User {
    private String id;
    private String email;
    private String username;
    private String password;

    private String phone;
    private long timestamp; //Para la fecha en el que se creo el usuario
    private String imageProfile;


    public User() {
    }


    public User(String id, String email, String username, String password, String phone, long timestam,String imageProfile) {

        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.timestamp = timestamp;
        this.imageProfile = imageProfile;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
