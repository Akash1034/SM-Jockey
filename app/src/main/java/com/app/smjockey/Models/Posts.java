package com.app.smjockey.Models;

/**
 * Created by Akash Srivastava on 04-07-2016.
 */
public class Posts {

    String id;
    String profile_image;
    String name;
    String username;
    String text;
    String content_image;
    String type;
    String active;

    public Posts()
    {

    }

    public Posts(String id, String profile_image, String name, String username, String text, String content_image, String type, String active) {
        this.id = id;
        this.profile_image = profile_image;
        this.name = name;
        this.username = username;
        this.text = text;
        this.content_image = content_image;
        this.type = type;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContent_image() {
        return content_image;
    }

    public void setContent_image(String content_image) {
        this.content_image = content_image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
