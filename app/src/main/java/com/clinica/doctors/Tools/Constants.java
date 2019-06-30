package com.clinica.doctors.Tools;

public final class Constants {

    public static final int PROFILE_IMAGE_RATIO_WIDTH = 1;
    public static final int PROFILE_IMAGE_RATIO_HIGHT = 1;
    public static final int MAXIMUM_PROFILE_IMAGE_WIDTH = 2000;
    public static final int MAXIMUM_PROFILE_IMAGE_HIGHT = 2000;
    public static final int LAST_NEWS_COUNT = 5;
    public static final int LAST_QUESTIONS_COUNT = 5;
    public static final String ACTIVE_STATUS = "Active";
    public static final int HOME_NEWS_VIEW = 909;
    public static final int LIST_NEWS_VIEW = 806;
    public static final int HOME_CONSULTATIONS_VIEW = 708;
    public static final int LIST_CONSULTATIONS_VIEW = 401;
    public static final int NO_ACTION = -1;
    public static final int DOCTOR_ACTION = 1;
    public static final int DISEASE_ACTION = 2;
    public static final int SYMPTOM_ACTION = 3;


    public final class DataBase {

        public static final String USERS_NODE = "Users";
        public static final String USER_Email_NODE = "email";
        public static final String CITIES_NODE = "Cities";
        public static final String ADS_NODE = "Ads";
        public static final String Doctors_NODE = "Doctors";
        public static final String Doctors_Statues_NODE = "accountStatus";
        public static final String Diseases_NODE = "Diseases";
        public static final String Symptoms_NODE = "Symptoms";
        public static final String News_NODE = "News";
        public static final String News_publisherID_NODE = "publisherID";
        public static final String News_specializationID_NODE = "specializationID";
        public static final String News_Date_NODE = "date";
        public static final String Specialization_NODE = "Specialization";
        public static final String Consultation_NODE = "Consultations";
        public static final String Answer_Date_NODE = "answerDate";
        public static final String Bookings_NODE = "Bookings";
        public static final String Notifications_NODE = "Notifications";
    }

    public final class Intents {

        public static final String SELECTION_KEY = "_selection";
        public static final String SELECT_CITY = "_city";
        public static final String SELECT_SPECIALIZATION = "_specialization";
        public static final String SELECT_DISEASES = "_diseases";

        public static final String CITY_DATA = "_city_data";
        public static final String SPECIALIZATION_DATA = "_specialization_data";
        public static final String DISEASES_ID = "_diseases_id";
        public static final String DOCTOR_ID = "_doctor_id";

        public static final String ACCOUNT_REGEITERATION = "_registration";
        public static final String EMAIL_REGEITERATION = "_registration_by_email";
        public static final String GOOGLE_REGEITERATION = "_registration_by_google";
        public static final String FACEBOOK_REGEITERATION = "_registration_by_facebook";
        public static final String Facebook_ID = "_facebook_id";
        public static final String Facebook_TOKEN = "_facebook_token";
        public static final String Google_ID = "_google_id";
        public static final String Google_TOKEN = "_google_token";
        public static final String ACCOUNT_NAME = "_account_name";
        public static final String ACCOUNT_EMAIL = "_account_email";
        public static final String ACCOUNT_PICTURE = "_account_pic";
        public static final String ACCOUNT_BIRTH_DATE = "_account_birth_date";
        public static final String ACCOUNT_GENDER = "_account_gender";
        public static final String MESSAGE_INTENT = "_message";
        public static final String ADDRESS_LOCATION = "_address_location";
        public static final String ADDRESS_AR = "_address_ar";
        public static final String ADDRESS_EN = "_address_en";
        public static final String EDIT_PROFILE = "_edit_profile";
        public static final String EDIT_CLINIC = "_edit_clinic";
        public static final String DAY_APPOINTMENTS = "_day_appointments";
        public static final String BOOKING_ID = "_booking_id";
        public static final String QUESTION_ID = "_question_id";
    }

    public final class Storage {

        public static final String PROFILE_IMAGES_FOLDER = "profile_images";
        public static final String LICENSES_IMAGES_FOLDER = "practiceLicenseIds";
        public static final String CERTIFICATIONS_IMAGES_FOLDER = "certificationsIds";
    }
}
