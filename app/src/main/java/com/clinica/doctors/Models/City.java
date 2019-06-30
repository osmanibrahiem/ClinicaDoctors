package com.clinica.doctors.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {

    private int id;
    private String titleAr, titleEn;
    private double lat, lng;

    public City() {
    }

    protected City(Parcel in) {
        id = in.readInt();
        titleAr = in.readString();
        titleEn = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(titleAr);
        dest.writeString(titleEn);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
}
