package com.clinica.doctors.Activities.Auth.DoctorProfile;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Activities.ImagePickerActivity;
import com.clinica.doctors.Models.Doctor.BasicInformation;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.Doctor.Specialization;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.CustomTFSpan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

class ProfilePresenter extends BasePresenter implements OnFailureListener {

    private static final int REQUEST_IMAGE = 91;
    String specialtyID;
    private ProfileActivity activity;
    private ProfileView view;
    private DatabaseReference mReference;
    private StorageReference storageReference;
    Doctor thisDoctor;
    private Uri profileImagePath, licenseImagePath, certificationImagePath;

    enum ImageOptions {PROFILE_IMAGE, LICENSE_IMAGE, CERTIFICATION_IMAGE}

    private ImageOptions selectedOption;

    ProfilePresenter(ProfileView view, ProfileActivity activity) {
        super(view, activity);
        this.activity = activity;
        this.view = view;
        this.mReference = FirebaseDatabase.getInstance().getReference();
        this.storageReference = FirebaseStorage.getInstance().getReference();
        ImagePickerActivity.clearCache(activity);
    }

    private String getId() {
        return FirebaseAuth.getInstance().getUid();
    }

    void getDoctor() {
        if (isNetworkAvailable()) {
            view.showLoading();
            mReference.child(Constants.DataBase.Doctors_NODE).child(getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            thisDoctor = dataSnapshot.getValue(Doctor.class);

                            String specializationID = thisDoctor.getSpecializationID();
                            if (!TextUtils.isEmpty(specializationID)) {
                                getSpecialization(specializationID);
                            } else {
                                view.showError(R.drawable.ic_sad, R.string.data_not_found);
                                view.hideLoading();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.showError(R.drawable.ic_sad, R.string.data_not_found);
                            view.hideLoading();
                        }
                    });
        } else {
            view.showError(R.drawable.no_internet, R.string.message_no_internet);
        }
    }

    private void getSpecialization(final String specializationID) {
        mReference.child(Constants.DataBase.Specialization_NODE).child(specializationID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Specialization specialization = dataSnapshot.getValue(Specialization.class);
                        specialization.setId(specializationID);
                        thisDoctor.setSpecialization(specialization);
                        view.setData(thisDoctor);
                        view.hideLoading();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.showError(R.drawable.ic_sad, R.string.data_not_found);
                        view.hideLoading();
                    }
                });
    }

    void importImage(final ImageOptions options) {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions(options);
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions(final ImageOptions options) {
        ImagePickerActivity.showImagePickerOptions(activity, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent(options);
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent(options);
            }
        });
    }

    private void launchCameraIntent(ImageOptions options) {
        selectedOption = options;
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
        intent.putExtra(ImagePickerActivity.INTENT_SET_CIRCLE_IMAGE, (options == ImageOptions.PROFILE_IMAGE));

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, Constants.PROFILE_IMAGE_RATIO_WIDTH); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, Constants.PROFILE_IMAGE_RATIO_HIGHT);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, Constants.MAXIMUM_PROFILE_IMAGE_WIDTH);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, Constants.MAXIMUM_PROFILE_IMAGE_HIGHT);

        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent(ImageOptions options) {
        selectedOption = options;
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_SET_CIRCLE_IMAGE, (options == ImageOptions.PROFILE_IMAGE));

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, Constants.PROFILE_IMAGE_RATIO_WIDTH); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, Constants.PROFILE_IMAGE_RATIO_HIGHT);
        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                switch (selectedOption) {
                    case PROFILE_IMAGE:
                        profileImagePath = data.getParcelableExtra("path");
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), profileImagePath);
                            view.setProfileImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LICENSE_IMAGE:
                        licenseImagePath = data.getParcelableExtra("path");
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), licenseImagePath);
                            view.setLicenseImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case CERTIFICATION_IMAGE:
                        certificationImagePath = data.getParcelableExtra("path");
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), certificationImagePath);
                            view.setCertificationImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Typeface tf = ResourcesCompat.getFont(activity, R.font.typing);
        CustomTFSpan tfSpan = new CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(activity.getString(R.string.dialog_permission_title));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannableString);
        builder.setMessage(activity.getString(R.string.dialog_permission_message));
        builder.setPositiveButton(activity.getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }

    boolean isInputsValid() {
        String nameAr = activity.nameArEt.getText().toString().trim();
        String nameEn = activity.nameEnEt.getText().toString().trim();
        String birthday = activity.birthdayEt.getText().toString().trim();
        String gender = activity.inputGender.getSelectedItem().toString();
        String professionalTitleAr = activity.professionalTitleArEt.getText().toString().trim();
        String professionalTitleEn = activity.professionalTitleEnEt.getText().toString().trim();
        String specialty = activity.specialtyEt.getText().toString().trim();

        if (TextUtils.isEmpty(nameAr)) {
            view.showNameArError(R.string.empty_name);
            return false;
        }
        if (TextUtils.isEmpty(nameEn)) {
            view.showNameEnError(R.string.empty_name);
            return false;
        }
        if (TextUtils.isEmpty(birthday)) {
            view.showBirthdayError(R.string.empty_birthday);
            return false;
        }
        Pattern mPattern = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])[-/.](0[1-9]|1[012])[-/.](19|20)\\d\\d$");
        if (!mPattern.matcher(birthday).matches()) {
            view.showBirthdayError(R.string.invalid_birthday);
            return false;
        }
        String[] ITEMS = activity.getResources().getStringArray(R.array.sex_items);
        if (!gender.equals(ITEMS[1]) && !gender.equals(ITEMS[2])) {
            view.showGenderError(R.string.gender_error);
            return false;
        }
        if (TextUtils.isEmpty(thisDoctor.getPracticeLicenseIdPhotoURL()) && licenseImagePath == null) {
            view.showLicenseError(R.string.required);
            return false;
        }
        if (TextUtils.isEmpty(professionalTitleAr)) {
            view.showProfessionalTitleArError(R.string.required);
            return false;
        }
        if (TextUtils.isEmpty(professionalTitleEn)) {
            view.showProfessionalTitleEnError(R.string.required);
            return false;
        }
        if (TextUtils.isEmpty(specialty) && TextUtils.isEmpty(specialtyID)) {
            view.showSpecialtyError(R.string.specialty_error);
            return false;
        }
        if (TextUtils.isEmpty(thisDoctor.getCertificatePhotoURL()) && certificationImagePath == null) {
            view.showCertificateError(R.string.required);
            return false;
        }
        return true;
    }

    void saveEdits() {
        view.showLoading();
        if (profileImagePath != null) {
            uploadProfileImage();
        } else if (licenseImagePath != null) {
            uploadLicenseImage();
        } else if (certificationImagePath != null) {
            uploadCertificationImage();
        } else {
            saveProfile();
        }
    }

    private void uploadProfileImage() {
        final StorageReference imageRef = storageReference.child(Constants.Storage.PROFILE_IMAGES_FOLDER).child(getId());
        imageRef.putFile(profileImagePath).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnFailureListener(ProfilePresenter.this)
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        thisDoctor.setProfilePhotoUrl(uri.toString());
                                        if (licenseImagePath != null) {
                                            uploadLicenseImage();
                                        } else if (certificationImagePath != null) {
                                            uploadCertificationImage();
                                        } else {
                                            saveProfile();
                                        }
                                    }
                                });
                    }
                });
    }

    private void uploadLicenseImage() {
        final StorageReference imageRef = storageReference.child(Constants.Storage.LICENSES_IMAGES_FOLDER).child(getId());
        imageRef.putFile(licenseImagePath).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnFailureListener(ProfilePresenter.this)
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        thisDoctor.setPracticeLicenseIdPhotoURL(uri.toString());
                                        if (certificationImagePath != null) {
                                            uploadCertificationImage();
                                        } else {
                                            saveProfile();
                                        }
                                    }
                                });
                    }
                });
    }

    private void uploadCertificationImage() {
        final StorageReference imageRef = storageReference.child(Constants.Storage.CERTIFICATIONS_IMAGES_FOLDER).child(getId());
        imageRef.putFile(certificationImagePath).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnFailureListener(ProfilePresenter.this)
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        thisDoctor.setCertificatePhotoURL(uri.toString());
                                        saveProfile();
                                    }
                                });
                    }
                });
    }

    private void saveProfile() {
        String[] ITEMS = activity.getResources().getStringArray(R.array.sex_items);
        String nameAr = activity.nameArEt.getText().toString().trim();
        String nameEn = activity.nameEnEt.getText().toString().trim();
        String birthday = activity.birthdayEt.getText().toString().trim();
        String gender = activity.inputGender.getSelectedItem().toString();
        String aboutAr = activity.aboutArEt.getText().toString().trim();
        String aboutEn = activity.aboutEnEt.getText().toString().trim();
        String professionalTitleAr = activity.professionalTitleArEt.getText().toString().trim();
        String professionalTitleEn = activity.professionalTitleEnEt.getText().toString().trim();

        thisDoctor.setSpecialization(null);

        BasicInformation informationAr = thisDoctor.getBasicInformationAr();
        if (informationAr == null)
            informationAr = new BasicInformation();
        informationAr.setDisplayName(nameAr);
        informationAr.setProfessionalTitle(professionalTitleAr);
        informationAr.setAbout(aboutAr);
        if (gender.equals(ITEMS[2]))
            informationAr.setGender("ذكر");
        else informationAr.setGender("أنثى");

        BasicInformation informationEn = thisDoctor.getBasicInformationEN();
        if (informationEn == null)
            informationEn = new BasicInformation();
        informationEn.setDisplayName(nameEn);
        informationEn.setProfessionalTitle(professionalTitleEn);
        informationEn.setAbout(aboutEn);
        if (gender.equals(ITEMS[2]))
            informationEn.setGender("Male");
        else informationEn.setGender("Female");

        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            long birthdayTimestamp = dateFormat.parse(birthday).getTime();
            thisDoctor.setBirthDateTimestamp(birthdayTimestamp);
        } catch (ParseException e) {
            Log.i("saveEdits", "saveEdits: " + e.toString());
        }

        if (!TextUtils.isEmpty(specialtyID)) {
            thisDoctor.setSpecializationID(specialtyID);
        }

        mReference.child(Constants.DataBase.Doctors_NODE).child(getId())
                .setValue(thisDoctor).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (checkClinic(thisDoctor)) {
                            if (!TextUtils.isEmpty(thisDoctor.getAccountStatus())) {
                                switch (thisDoctor.getAccountStatus()) {
                                    case Doctor.ACTIVE_STATUS:
                                        view.hideLoading();
                                        view.openMain();
                                        break;
                                    case Doctor.UNDER_REVIEW_STATUS:
                                        view.hideLoading();
                                        view.openMessageActivity(R.string.profile_under_review);
                                        break;
                                    case Doctor.BLOCKED_STATUS:
                                        view.hideLoading();
                                        view.openMessageActivity(R.string.profile_blocked);
                                        break;
                                    case Doctor.DELETED_STATUS:
                                        view.hideLoading();
                                        view.openMessageActivity(R.string.profile_deleted);
                                        break;
                                    default:
                                        setAccountStatus();
                                }
                            } else {
                                setAccountStatus();
                            }
                        } else {
                            view.openClinicActivity();
                        }
                    }
                });
    }

    private void setAccountStatus() {
        mReference.child(Constants.DataBase.Doctors_NODE).child(getId())
                .child(Constants.DataBase.Doctors_Statues_NODE).setValue(Doctor.UNDER_REVIEW_STATUS)
                .addOnFailureListener(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                view.hideLoading();
                view.openMessageActivity(R.string.profile_under_review);
            }
        });
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        view.hideLoading();
        view.toastError(R.string.error_happened_2);
    }
}
