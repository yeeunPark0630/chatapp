package com.example.chat.Model;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String emailAddress;
    private String lowercaseUsername;
    private String statusMsg;

    public User(String id, String username, String imageURL, String emailAddress, String lowercaseUsername, String statusMsg) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.emailAddress = emailAddress;
        this.lowercaseUsername = lowercaseUsername;
        this.statusMsg = statusMsg;
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

    public void setLowercaseUsername(String lowercaseUsername) {
        this.lowercaseUsername = lowercaseUsername;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
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

    public String getLowercaseUsername() {
        return lowercaseUsername;
    }

    public String getStatusMsg() {
        return statusMsg;
    }
}
