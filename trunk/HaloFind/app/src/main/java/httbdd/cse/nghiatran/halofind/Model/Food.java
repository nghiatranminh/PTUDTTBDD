package httbdd.cse.nghiatran.halofind.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Food implements Serializable {
    @JsonProperty("title")
    String title;
    @JsonProperty("image")
    String image;
    @JsonProperty("city")
    String city;
    @JsonProperty("category")
    String category;
    @JsonProperty("body")
    String body;
    @JsonProperty("address")
    String address;
    @JsonProperty("latitude")
    double latitude;
    @JsonProperty("longitude")
    double longitude;
    @JsonProperty("district")
    String district;
    @JsonProperty("time")
    String time;
    @JsonProperty("userimage")
    String userimage;
    @JsonProperty("username")
    String username;
    @JsonProperty("gif")
    String gif;

    public Food() {
    }

    public Food(String title, String image, String city, String category, String body, String address, double latitude, double longtitude, String district, String time, String username, String userimage, String gif) {
        this.title = title;
        this.image = image;
        this.city = city;
        this.category = category;
        this.body = body;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longtitude;
        this.district = district;
        this.time = time;
        this.userimage = userimage;
        this.username = username;
        this.gif = gif;
    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//      dest.writeString(title);
//      dest.writeString(image);
//      dest.writeString(city);
//      dest.writeString(address);
//      dest.writeString(category);
//      dest.writeString(body);
//      dest.writeString(district);
//      dest.writeString(time);
//      dest.writeString(userimage);
//      dest.writeString(username);
//      dest.writeDouble(latitude);
//      dest.writeDouble(longitude);
//    }
//    public static final Parcelable.Creator<Food> CREATOR = new Parcelable.Creator<Food>() {
//        public Food createFromParcel(Parcel in) {
//            return new Food(in);
//        }
//
//        public Food[] newArray(int size) {
//            return new Food[size];
//        }
//    };
//    public Food(Parcel in) {
//        title = in.readString();
//        image = in.readString();
//        city = in.readString();
//        category = in.readString();
//        body = in.readString();
//        address = in.readString();
//        latitude = in.readDouble();
//        longitude = in.readDouble();
//        district = in.readString();
//        time = in.readString();
//        userimage = in.readString();
//        username = in.readString();
//    }
}
