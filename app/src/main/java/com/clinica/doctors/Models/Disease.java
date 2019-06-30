package com.clinica.doctors.Models;

import java.util.Collections;
import java.util.List;

public class Disease {

    private String id, title, imgURL, specializationID, status;
    private List<Details> details = Collections.emptyList();

    public Disease() {
    }

    public Disease(String title, String imgURL, String specializationID, String status, List<Details> details) {
        this.title = title;
        this.imgURL = imgURL;
        this.specializationID = specializationID;
        this.status = status;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getSpecializationID() {
        return specializationID;
    }

    public void setSpecializationID(String specializationID) {
        this.specializationID = specializationID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Details> getDetails() {
        return details;
    }

    public void setDetails(List<Details> details) {
        this.details = details;
    }

    public static class Details {

        private String id, title, description;

        public Details() {
        }

        public Details(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
