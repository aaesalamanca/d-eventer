package es.achraf.deventer.model;

public class Message {

    // Fields
    private String name;
    private String profileImageUri;
    private String text;
    private String imageUri;
    private long date;

    // Getters

    public String getName() {
        return name;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public String getText() {
        return text;
    }

    public String getImageUri() {
        return imageUri;
    }

    public long getDate() {
        return date;
    }

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
