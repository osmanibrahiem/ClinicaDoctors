package com.clinica.doctors.Models.Doctor;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Localization;

public class Address implements Parcelable {

    private String governorate, city, state, buildingNumber, streetName, floor, apartment, landmark;
    private double lat, lng;

    public Address() {
    }

    protected Address(Parcel in) {
        governorate = in.readString();
        city = in.readString();
        state = in.readString();
        buildingNumber = in.readString();
        streetName = in.readString();
        floor = in.readString();
        apartment = in.readString();
        landmark = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
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

    public String getAddress(Context context, int localization) {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(buildingNumber))
            builder.append(getBuildingNumber()).append(" ");
        if (!TextUtils.isEmpty(streetName))
            builder.append(getStreetName()).append(" - ");
        if (!TextUtils.isEmpty(floor))
            if (localization == Localization.ARABIC_VALUE)
                builder.append(context.getString(R.string.floor_ar)).append(" ").append(getFloor()).append(" - ");
            else
                builder.append(context.getString(R.string.floor_en)).append(" ").append(getFloor()).append(" - ");
        if (!TextUtils.isEmpty(apartment))
            if (localization == Localization.ARABIC_VALUE)
                builder.append(context.getString(R.string.apartment_ar)).append(" ").append(getApartment()).append(" - ");
            else
                builder.append(context.getString(R.string.apartment_en)).append(" ").append(getApartment()).append(" - ");
        if (!TextUtils.isEmpty(landmark))
            builder.append(getLandmark()).append(" - ");
        if (!TextUtils.isEmpty(state))
            builder.append(getState());

        return builder.toString();
    }

    public String getFullAddress(Context context, int localization) {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(buildingNumber))
            builder.append(getBuildingNumber()).append(" ");
        if (!TextUtils.isEmpty(streetName))
            builder.append(getStreetName()).append(" - ");
        if (!TextUtils.isEmpty(floor))
            if (localization == Localization.ARABIC_VALUE)
                builder.append(context.getString(R.string.floor_ar)).append(" ").append(getFloor()).append(" - ");
            else
                builder.append(context.getString(R.string.floor_en)).append(" ").append(getFloor()).append(" - ");
        if (!TextUtils.isEmpty(apartment))
            if (localization == Localization.ARABIC_VALUE)
                builder.append(context.getString(R.string.apartment_ar)).append(" ").append(getApartment()).append(" - ");
            else
                builder.append(context.getString(R.string.apartment_en)).append(" ").append(getApartment()).append(" - ");
        if (!TextUtils.isEmpty(landmark))
            builder.append(getLandmark()).append("\n");
        if (!TextUtils.isEmpty(state))
            builder.append(getState()).append(" - ");
        if (!TextUtils.isEmpty(city))
            builder.append(getCity()).append("\n");
        if (!TextUtils.isEmpty(governorate))
            builder.append(getGovernorate());

        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(governorate);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(buildingNumber);
        dest.writeString(streetName);
        dest.writeString(floor);
        dest.writeString(apartment);
        dest.writeString(landmark);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
}
