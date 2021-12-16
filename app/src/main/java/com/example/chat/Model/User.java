package com.example.chat.Model;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String emailAddress;

    public User(String id, String username, String imageURL, String emailAddress) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.emailAddress = emailAddress;
    }

    public User(){
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
