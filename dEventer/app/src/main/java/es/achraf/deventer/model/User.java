package es.achraf.deventer.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    // Fields
    private String name;
    private String age;
    private String sex;
    private String postalCode;
    private ArrayList<String> alEvent;

    // Constructors

    /**
     * Constructor vacío —por defecto—.
     */
    public User() {
        alEvent = new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public ArrayList<String> getAlEvent() {
        return alEvent;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setAlEvent(ArrayList<String> alEvent) {
        this.alEvent = alEvent;
    }
}
