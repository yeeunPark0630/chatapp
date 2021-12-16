package com.example.chat.Model;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String emailAddress;
    private String lowercaseUsername;

    public User(String id, String username, String imageURL, String emailAddress, String lowercaseUsername) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.emailAddress = emailAddress;
        this.lowercaseUsername = lowercaseUsername;
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

    public void setLowerUsername(String lowercaseUsername) {
        this.lowercaseUsername = lowercaseUsername;
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

    public String getLowerUsername() {
        return lowercaseUsername;
    }
}
