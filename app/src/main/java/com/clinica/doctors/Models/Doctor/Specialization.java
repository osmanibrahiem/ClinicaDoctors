package com.clinica.doctors.Models.Doctor;

import android.os.Parcel;
import android.os.Parcelable;

public class Specialization implements Parcelable {

    private String id, titleAr, titleEn, imgURL;

    public Specialization() {
    }

    protected Specialization(Parcel in) {
        id = in.readString();
        titleAr = in.readString();
        titleEn = in.readString();
        imgURL = in.readString();
    }

    public static final Creator<Specialization> CREATOR = new Creator<Specialization>() {
        @Override
        public Specialization createFromParcel(Parcel in) {
            return new Specialization(in);
        }

        @Override
        public Specialization[] newArray(int size) {
            return new Specialization[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(titleAr);
        dest.writeString(titleEn);
        dest.writeString(imgURL);
    }
}
