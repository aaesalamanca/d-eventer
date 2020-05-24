package es.achraf.deventer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {

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

    // Parcelable implementation

    protected Event(Parcel in) {
        imageUri = in.readString();
        name = in.readString();
        date = in.readString();
        time = in.readString();
        location = in.readString();
        price = in.readString();
        description = in.readString();
        ownerId = in.readString();
        usersNum = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUri);
        dest.writeString(name);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(location);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(ownerId);
        dest.writeInt(usersNum);
    }
}
