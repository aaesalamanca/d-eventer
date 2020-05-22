package es.achraf.deventer.model;

public class Message {

    // Fields
    private String ownerId;
    private String text;
    private String imageUri;
    private long date;

    // Getters

    public String getOwnerId() {
        return ownerId;
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

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
