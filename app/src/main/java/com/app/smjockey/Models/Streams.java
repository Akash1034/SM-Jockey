package com.app.smjockey.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Akash Srivastava on 01-07-2016.
 */
public class Streams implements Serializable {


    private String id;
    private String name;
    private ArrayList<String> tags;
    private String uuid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
