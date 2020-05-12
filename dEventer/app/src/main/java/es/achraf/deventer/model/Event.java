package es.achraf.deventer.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    // Fields
    private String imageUri;
    private String name;
    private String date;
    private String time;
    private String location;
    private String price;
    private String description;
    private String ownerId;
    private int usersNum;

    // Constructors

    /**
     * Constructor vacío —por defecto—.
     */
    public Event() {

    }

    // Getters

    public String getImageUri() {
        return imageUri;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public int getUsersNum() {
        return usersNum;
    }


    // Setters

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setUsersNum(int usersNum) {
        this.usersNum = usersNum;
    }
}
