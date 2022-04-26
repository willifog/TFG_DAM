package com.example.mistareas.models;

public class Post {

    private String id;
    private String title;
    private String description;
    private String image1;  //De tipo string ya que vamos a almacenar la URL
    private String category;
    private String idUser;
    private long timestamp;


    public Post() {
    }

    public Post(String id, String title, String description, String image1, String category, String idUser, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image1 = image1;
        this.category = category;
        this.idUser = idUser;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
