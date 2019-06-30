package com.clinica.doctors.Activities.Auth.SignUp;

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
import android.util.Patterns;

import com.clinica.doctors.Activities.Auth.UploadPracticeLicenseIdPhotoActivity;
import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Activities.ImagePickerActivity;
import com.clinica.doctors.Models.Doctor.BasicInformation;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.CustomTFSpan;
import com.clinica.doctors.Tools.ToastTool;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

class SignupPresenter extends BasePresenter implements OnFailureListener {

    private SignupView view;
    private SignupActivity activity;

    private static final int REQUEST_IMAGE = 762;
    private Uri imagePath;
    public String specialtyID = "";
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private StorageReference storageReference;
    private String doctorPictureURL = "";
    private Intent intent;

    private static final String TAG = "SignUp";

    SignupPresenter(SignupView view, SignupActivity activity) {
        super(view, activity);
        this.view = view;
        this.activity = activity;
        this.mAuth = FirebaseAuth.getInstance();
        this.mReference = FirebaseDatabase.getInstance().getReference();
        this.storageReference = FirebaseStorage.getInstance().getReference();

        ImagePickerActivity.clearCache(activity);
    }

    private String getRegisterEmail() {
        return activity.emailEt.getText().toString().trim();
    }

    private String getRegisterPassword() {
        return activity.passwordET.getText().toString().trim();
    }

    private String getRegisterPhoneNumber() {
        return activity.phoneET.getText().toString().trim();
    }

    private String getRegisterNameEn() {
        return activity.nameEnET.getText().toString().trim();
    }

    private String getRegisterNameAr() {
        return activity.nameArET.getText().toString().trim();
    }

    private String getRegisterGender() {
        return activity.inputGender.getSelectedItem().toString();
    }

    private String getRegisterGenderEn() {
        String gender = getRegisterGender();
        String[] ITEMS = activity.getResources().getStringArray(R.array.sex_items);
        if (gender.equals(ITEMS[2]))
            return "Male";
        else return "Female";
    }

    private String getRegisterGenderAr() {
        String gender = getRegisterGender();
        String[] ITEMS = activity.getResources().getStringArray(R.array.sex_items);
        if (gender.equals(ITEMS[2]))
            return "ذكر";
        else return "أنثى";
    }

    private String getRegisterBirthDate() {
        return activity.birthdayET.getText().toString().trim();
    }

    private Doctor getRegisterDoctor() {
        try {

            BasicInformation informationEn = new BasicInformation();
            informationEn.setDisplayName(getRegisterNameEn());
            informationEn.setGender(getRegisterGenderEn());
            BasicInformation informationAr = new BasicInformation();
            informationAr.setDisplayName(getRegisterNameAr());
            informationAr.setGender(getRegisterGenderAr());

            Doctor doctor = new Doctor();
            doctor.setEmail(getRegisterEmail());
            doctor.setProfilePhotoUrl(doctorPictureURL);
            doctor.setPhoneNumber(getRegisterPhoneNumber());
            doctor.setBasicInformationEN(informationEn);
            doctor.setBasicInformationAr(informationAr);
            doctor.setCreationTimestamp(new Date().getTime());
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            long birthdayTimestamp = dateFormat.parse(getRegisterBirthDate()).getTime();
            doctor.setBirthDateTimestamp(birthdayTimestamp);
            doctor.setSpecializationID(specialtyID);
            doctor.setAccountStatus(Doctor.UNDER_REVIEW_STATUS);
            if (intent != null) {
                if (intent.getStringExtra(Constants.Intents.ACCOUNT_REGEITERATION)
                        .equals(Constants.Intents.FACEBOOK_REGEITERATION)) {
                    doctor.setFacebookID(intent.getStringExtra(Constants.Intents.Facebook_ID));
                } else {
                    doctor.setGoogleID(intent.getStringExtra(Constants.Intents.Google_ID));
                }
            }
            return doctor;
        } catch (ParseException e) {
            onFailure(e);
        }
        return null;
    }

    boolean isValidInput() {
        String nameAr = getRegisterNameAr();
        String nameEn = getRegisterNameEn();
        String specialty = activity.specialtyET.getText().toString().trim();
        String email = getRegisterEmail();
        String phone = getRegisterPhoneNumber();
        String birthday = getRegisterBirthDate();
        String password = getRegisterPassword();
        String gender = getRegisterGender();

        if (TextUtils.isEmpty(nameAr)) {
            view.showNameArError(R.string.empty_name);
            return false;
        }
        if (TextUtils.isEmpty(nameEn)) {
            view.showNameEnError(R.string.empty_name);
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            view.showEmailError(R.string.empty_email);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showEmailError(R.string.invalid_email);
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            view.showPhoneError(R.string.empty_phone);
            return false;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            view.showPhoneError(R.string.invalid_phone);
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
        if (intent == null) {
            if (TextUtils.isEmpty(password)) {
                view.showPasswordError(R.string.empty_password);
                return false;
            }
            if (password.trim().length() < 8) {
                view.showPasswordError(R.string.invalid_password_2);
                return false;
            }
        }
        String[] ITEMS = activity.getResources().getStringArray(R.array.sex_items);
        if (!gender.equals(ITEMS[1]) && !gender.equals(ITEMS[2])) {
            view.showGenderError(R.string.gender_error);
            return false;
        }
        if (TextUtils.isEmpty(specialty) && TextUtils.isEmpty(specialtyID)) {
            view.showSpecialtyError(R.string.specialty_error);
            return false;
        }
        return true;
    }

    void signUpUserWithEmail() {
        view.showLoading();
        if (intent == null)
            checkEmail();
        else {
            AuthCredential credential;
            if (intent.getStringExtra(Constants.Intents.ACCOUNT_REGEITERATION)
                    .equals(Constants.Intents.FACEBOOK_REGEITERATION)) {
                credential = FacebookAuthProvider.getCredential(intent.getStringExtra(Constants.Intents.Facebook_TOKEN));
            } else {
                credential = GoogleAuthProvider.getCredential(intent.getStringExtra(Constants.Intents.Google_TOKEN), null);
            }
            signUpWithCredential(credential);
        }
    }

    private void checkEmail() {
        view.showLoading();
        String email = getRegisterEmail();
        Log.i(TAG, "checkEmail: " + email);
        mAuth.fetchSignInMethodsForEmail(email).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                        Log.i(TAG, "fetchSignInMethodsForEmail: ");
                        if (signInMethodQueryResult.getSignInMethods().isEmpty()) {
                            Log.i(TAG, "fetchSignInMethodsForEmail: getSignInMethods: email is not found");
                            createUserWithEmail();
                        } else {
                            Log.i(TAG, "fetchSignInMethodsForEmail: getSignInMethods: email is found");
                            view.showEmailError(R.string.message_email_found);
                            view.hideLoading();
                        }
                    }
                });
    }

    private void createUserWithEmail() {
        mAuth.createUserWithEmailAndPassword(getRegisterEmail(), getRegisterPassword()).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (imagePath != null) {
                            uploadProfilePicture(authResult.getUser().getUid());
                        } else {
                            saveDoctor(authResult.getUser().getUid());
                        }
                    }
                });
    }

    private void uploadProfilePicture(final String uid) {
        final StorageReference imageRef = storageReference.child(Constants.Storage.PROFILE_IMAGES_FOLDER).child(uid);
        imageRef.putFile(imagePath).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnFailureListener(SignupPresenter.this)
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        doctorPictureURL = uri.toString();
                                        saveDoctor(uid);
                                    }
                                });
                    }
                });
    }

    private void saveDoctor(String uid) {
        Doctor doctor = getRegisterDoctor();
        if (doctor != null) {
            mReference.child(Constants.DataBase.Doctors_NODE).child(uid)
                    .setValue(doctor).addOnFailureListener(this)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (intent == null)
                                loginWithEmail();
                            else {
                                view.hideLoading();
                                uploadLicense();
                            }
                        }
                    });
        }
    }

    private void loginWithEmail() {
        String email = getRegisterEmail();
        String password = getRegisterPassword();

        mAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        view.hideLoading();
                        uploadLicense();
                    }
                });
    }

    void importImage() {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
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

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(activity, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
        intent.putExtra(ImagePickerActivity.INTENT_SET_CIRCLE_IMAGE, true);

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

    private void launchGalleryIntent() {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_SET_CIRCLE_IMAGE, true);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, Constants.PROFILE_IMAGE_RATIO_WIDTH); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, Constants.PROFILE_IMAGE_RATIO_HIGHT);
        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                imagePath = data.getParcelableExtra("path");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imagePath);
                    view.setProfileImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
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

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.i(TAG, "onFailure: " + e);
        view.hideLoading();
        ToastTool.with(activity, R.string.error_happened).show();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    void setIntent(Intent intent) {
        this.intent = intent;
        if (intent.hasExtra(Constants.Intents.ACCOUNT_PICTURE))
            this.doctorPictureURL = intent.getStringExtra(Constants.Intents.ACCOUNT_PICTURE);
    }

    private void signUpWithCredential(AuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (imagePath != null) {
                            uploadProfilePicture(authResult.getUser().getUid());
                        } else {
                            saveDoctor(authResult.getUser().getUid());
                        }
                    }
                });
    }

    private void uploadLicense() {
        activity.startActivity(new Intent(activity, UploadPracticeLicenseIdPhotoActivity.class));
    }
}
