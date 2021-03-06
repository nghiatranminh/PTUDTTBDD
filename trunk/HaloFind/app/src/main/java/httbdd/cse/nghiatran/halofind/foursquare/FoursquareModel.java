package httbdd.cse.nghiatran.halofind.foursquare;

import java.io.Serializable;

public class FoursquareModel implements Serializable {
    private static final long serialVersionUID = 3790717505065723499L;
    private String name;
    private String city;
    private String longtitude, latitude, address, country;
    private String distance;
    private String icon;
    private String category, category_id;


    public FoursquareModel(String name, String city, String longtitude, String latitude, String address, String country, String category, String distance) {
        this.name = name;
        this.city = city;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.address = address;
        this.country = country;
        this.distance = distance;
        this.setCategory(category);

    }

    public FoursquareModel() {
        this.name = "";
        this.city = "";
        this.longtitude = "";
        this.latitude = "";
        this.address = "";
        this.country = "";
        this.setCategory("");

    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCity() {
        if (city.length() > 0) {
            return city;
        }
        return city;
    }

    public void setCity(String city) {
        if (city != null) {
            this.city = city.replaceAll("\\(", "").replaceAll("\\)", "");
            ;
        }
    }


    public void setCategoryIcon(String icon) {
        this.icon = icon;
    }

    public String getCategoryIcon() {
        return this.icon;
    }

    public void setCategoryID(String category_id) {
        this.category_id = category_id;
    }

    public String getCategoryID() {
        return this.category_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return this.country;

    }


    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public double getLongtitude() {
        return Double.parseDouble(this.longtitude);
    }


    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return Double.parseDouble(this.latitude);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
