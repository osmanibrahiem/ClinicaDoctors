package com.clinica.doctors.Activities.Auth.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.clinica.doctors.Activities.Auth.SignUp.SignupActivity;
import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.ToastTool;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

class LoginPresenter extends BasePresenter implements OnFailureListener {

    private LoginActivity activity;
    private LoginView view;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = "Login";
    private static final int RC_SIGN_IN = 77;

    LoginPresenter(final LoginView view, final LoginActivity activity) {
        super(view, activity);
        this.activity = activity;
        this.view = view;
        this.mAuth = FirebaseAuth.getInstance();
        this.mReference = FirebaseDatabase.getInstance().getReference();

        Log.i(TAG, "LoginPresenter: initPresenter");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "FacebookCallback : onSuccess: token : " + loginResult.getAccessToken());
                final String facebookToken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject facebookAccount, GraphResponse response) {
                                Log.i(TAG, "FacebookCallback : onSuccess: gettingAccount :onCompleted: ");
                                try {
                                    if (facebookAccount.has("email")) {
                                        String loginEmail = facebookAccount.getString("email");
                                        Log.i(TAG, "FacebookCallback : onSuccess: gettingAccount :gettingEmail: " + loginEmail);
                                        checkFacebookEmail(loginEmail, facebookToken, facebookAccount);
                                    }
                                } catch (JSONException e) {
                                    Log.i(TAG, "FacebookCallback : onSuccess: gettingAccount : JSONException: " + e.getLocalizedMessage());
                                    onFailure(e);
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,picture.width(2000)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "FacebookCallback : onCancel: ");
                view.hideLoading();
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "FacebookCallback : onError: FacebookException: " + error.getLocalizedMessage());
                onFailure(error);
            }
        };
        LoginManager.getInstance().registerCallback(callbackManager, loginResultFacebookCallback);
    }

    private String getLoginEmail() {
        return activity.emailET.getText().toString().trim();
    }

    private String getLoginPassword() {
        return activity.passwordET.getText().toString().trim();
    }

    boolean isValidEmail() {
        String email = getLoginEmail();
        if (TextUtils.isEmpty(email)) {
            view.showEmailError(R.string.empty_email);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showEmailError(R.string.invalid_email);
            return false;
        }
        return true;
    }

    boolean isValidPassword() {
        String password = getLoginPassword();
        if (TextUtils.isEmpty(password)) {
            view.showPasswordError(R.string.empty_password);
            return false;
        }
        if (password.trim().length() < 8) {
            view.showPasswordError(R.string.invalid_password);
            return false;
        }
        return true;
    }

    void loginWithFacebook() {
        view.showLoading();
        Log.i(TAG, "loginWithFacebook: ");
        LoginManager.getInstance().logInWithReadPermissions(activity,
                Arrays.asList("public_profile", "email", "user_birthday", "user_gender"));
    }

    void loginWithGoogle() {
        view.showLoading();
        Log.i(TAG, "loginWithGoogle: ");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    void checkEmail() {
        view.showLoading();
        String email = getLoginEmail();
        Log.i(TAG, "checkEmail: " + email);
        mAuth.fetchSignInMethodsForEmail(email).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                        Log.i(TAG, "fetchSignInMethodsForEmail: ");
                        if (signInMethodQueryResult.getSignInMethods().isEmpty()) {
                            Log.i(TAG, "fetchSignInMethodsForEmail: getSignInMethods: email is not found");
                            view.showEmailError(R.string.message_email_not_found);
                            view.hideLoading();
                        } else {
                            Log.i(TAG, "fetchSignInMethodsForEmail: getSignInMethods: email is found");
                            isDoctorAccount();
                        }
                    }
                });
    }

    void checkFacebookEmail(final String facebookEmail, final String facebookToken, final JSONObject facebookAccount) {
        Log.i(TAG, "checkFacebookEmail: " + facebookEmail);
        mAuth.fetchSignInMethodsForEmail(facebookEmail).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                        Log.i(TAG, "fetchSignInMethodsForEmail: ");
                        if (signInMethodQueryResult.getSignInMethods().isEmpty()) {
                            Log.i(TAG, "fetchSignInMethodsForEmail: getSignInMethods: email is not found");
                            registerFacebookEmail(facebookAccount, facebookToken);
                        } else {
                            Log.i(TAG, "fetchSignInMethodsForEmail: getSignInMethods: email is found");
                            isFacebookDoctorAccount(facebookEmail, facebookToken);
                        }
                    }
                });
    }

    void checkGoogleEmail(final GoogleSignInAccount googleAccount) {
        Log.i(TAG, "checkGoogleEmail: " + googleAccount.getEmail());
        mAuth.fetchSignInMethodsForEmail(googleAccount.getEmail()).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                        Log.i(TAG, "fetchSignInMethodsForEmail: ");
                        if (signInMethodQueryResult.getSignInMethods().isEmpty()) {
                            Log.i(TAG, "fetchSignInMethodsForEmail: getSignInMethods: email is not found");
                            registerGoogleEmail(googleAccount);
                        } else {
                            Log.i(TAG, "fetchSignInMethodsForEmail: getSignInMethods: email is found");
                            isGoogleDoctorAccount(googleAccount);
                        }
                    }
                });
    }

    private void registerGoogleEmail(GoogleSignInAccount googleAccount) {
        view.hideLoading();
        Log.i(TAG, "registerGoogleEmail: " + googleAccount.getEmail());
        Intent intent = new Intent(activity, SignupActivity.class);
        intent.putExtra(Constants.Intents.ACCOUNT_REGEITERATION, Constants.Intents.GOOGLE_REGEITERATION);
        intent.putExtra(Constants.Intents.Google_TOKEN, googleAccount.getIdToken());
        intent.putExtra(Constants.Intents.Google_ID, googleAccount.getId());
        intent.putExtra(Constants.Intents.ACCOUNT_NAME, googleAccount.getDisplayName());
        intent.putExtra(Constants.Intents.ACCOUNT_EMAIL, googleAccount.getEmail());
        intent.putExtra(Constants.Intents.ACCOUNT_PICTURE, googleAccount.getPhotoUrl().toString());
        activity.startActivity(intent);
    }

    private void registerFacebookEmail(JSONObject facebookAccount, String facebookToken) {
        Log.i(TAG, "registerFacebookEmail: ");
        view.hideLoading();
        Intent intent = new Intent(activity, SignupActivity.class);
        intent.putExtra(Constants.Intents.ACCOUNT_REGEITERATION, Constants.Intents.FACEBOOK_REGEITERATION);
        intent.putExtra(Constants.Intents.Facebook_TOKEN, facebookToken);
        try {
            if (facebookAccount.has("id"))
                intent.putExtra(Constants.Intents.Facebook_ID, facebookAccount.getString("id"));
            if (facebookAccount.has("name"))
                intent.putExtra(Constants.Intents.ACCOUNT_NAME, facebookAccount.getString("name"));
            if (facebookAccount.has("email"))
                intent.putExtra(Constants.Intents.ACCOUNT_EMAIL, facebookAccount.getString("email"));
            if (facebookAccount.has("picture"))
                intent.putExtra(Constants.Intents.ACCOUNT_PICTURE, facebookAccount.getJSONObject("picture").getJSONObject("data").getString("url"));
            if (facebookAccount.has("birthday")) {
                String birthdayString = facebookAccount.getString("birthday");
                Log.i(TAG, "registerFacebookBirthday: " + birthdayString);
                DateFormat FacebookDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Log.i(TAG, "registerFacebookBirthDate: " + FacebookDateFormat.parse(birthdayString));
                DateFormat ClinicaDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String birthDate = ClinicaDateFormat.format(FacebookDateFormat.parse(birthdayString));
                Log.i(TAG, "registerClinicaBirthday: " + birthDate);
                intent.putExtra(Constants.Intents.ACCOUNT_BIRTH_DATE, birthDate);
            }
            if (facebookAccount.has("gender"))
                intent.putExtra(Constants.Intents.ACCOUNT_GENDER, facebookAccount.getString("gender"));

            activity.startActivity(intent);
        } catch (JSONException | ParseException e) {
            Log.i(TAG, "registerFacebookEmail: JSONException | ParseException : " + e.getLocalizedMessage());
            onFailure(e);
        }
    }

    private void isDoctorAccount() {
        Log.i(TAG, "isDoctorAccount: ");
        Query query = mReference.child(Constants.DataBase.USERS_NODE)
                .orderByChild(Constants.DataBase.USER_Email_NODE).equalTo(getLoginEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    view.showEmailError(R.string.message_email_registered_as_user);
                    view.hideLoading();
                    Log.i(TAG, "isDoctorAccount: onDataChange: email registered as patient");
                } else {
                    loginWithEmail();
                    Log.i(TAG, "isDoctorAccount: onDataChange: email registered as doctor");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "isDoctorAccount : onCancelled: " + databaseError.toException().getLocalizedMessage());
                onFailure(databaseError.toException());
            }
        });

    }

    private void isFacebookDoctorAccount(String email, final String facebookToken) {
        Log.i(TAG, "isFacebookDoctorAccount: ");
        Query query = mReference.child(Constants.DataBase.USERS_NODE)
                .orderByChild(Constants.DataBase.USER_Email_NODE).equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "isFacebookDoctorAccount: onDataChange: email registered as patient");
                    ToastTool.with(activity, R.string.message_email_registered_as_user).show();
                    view.hideLoading();
                } else {
                    Log.i(TAG, "isFacebookDoctorAccount: onDataChange: email registered as doctor");
                    AuthCredential credential = FacebookAuthProvider.getCredential(facebookToken);
                    signInWithCredential(credential);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "isFacebookDoctorAccount : onCancelled: " + databaseError.toException().getLocalizedMessage());
                onFailure(databaseError.toException());
            }
        });

    }

    private void isGoogleDoctorAccount(final GoogleSignInAccount googleAccount) {
        Log.i(TAG, "isGoogleDoctorAccount: ");
        Query query = mReference.child(Constants.DataBase.USERS_NODE)
                .orderByChild(Constants.DataBase.USER_Email_NODE).equalTo(googleAccount.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "isGoogleDoctorAccount: onDataChange: email registered as patient");
                    ToastTool.with(activity, R.string.message_email_registered_as_user).show();
                    view.hideLoading();
                } else {
                    Log.i(TAG, "isGoogleDoctorAccount: onDataChange: email registered as doctor");
                    AuthCredential credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
                    signInWithCredential(credential);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "isGoogleDoctorAccount : onCancelled: " + databaseError.toException().getLocalizedMessage());
                onFailure(databaseError.toException());
            }
        });
    }

    private void signInWithCredential(AuthCredential credential) {
        Log.i(TAG, "signInWithCredential: ");
        mAuth.signInWithCredential(credential).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.i(TAG, "signInWithCredential: onSuccess: ");
                        checkDoctorProfile();
                    }
                });
    }

    private void loginWithEmail() {
        Log.i(TAG, "loginWithEmail: ");
        String email = getLoginEmail();
        String password = getLoginPassword();
        mAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.i(TAG, "loginWithEmail: onSuccess: ");
                        checkDoctorProfile();
                    }
                });
    }

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.i(TAG, "onActivityResult: try to get google account");
                GoogleSignInAccount acct = task.getResult(ApiException.class);
                if (acct != null) {
                    Log.i(TAG, "onActivityResult: acct != null");
                    checkGoogleEmail(acct);
                } else {
                    Log.i(TAG, "onActivityResult: acct == null");
                    onFailure(task.getException());
                }
            } catch (ApiException e) {
                Log.i(TAG, "onActivityResult: catch ApiException : " + e.getLocalizedMessage());
                onFailure(e);
            }

        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.i(TAG, "onFailure: " + e);
        view.hideLoading();
        ToastTool.with(activity, R.string.error_happened).show();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    private void checkDoctorProfile() {
        mReference.child(Constants.DataBase.Doctors_NODE).child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        view.hideLoading();
                        if (dataSnapshot.exists()) {
                            Doctor doctor = dataSnapshot.getValue(Doctor.class);
                            if (doctor != null) {
                                if (TextUtils.isEmpty(doctor.getPracticeLicenseIdPhotoURL())) {
                                    view.openLicenseActivity();
                                } else {
                                    if (checkProfile(doctor)) {
                                        if (checkClinic(doctor)) {
                                            if (!TextUtils.isEmpty(doctor.getAccountStatus())) {
                                                switch (doctor.getAccountStatus()) {
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
                                    } else {
                                        view.openProfileActivity();
                                    }
                                }
                            } else view.showError(R.string.data_not_found);
                        } else view.showError(R.string.data_not_found);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.showError(R.string.data_not_found);
                    }
                });
    }

    private void setAccountStatus() {
        mReference.child(Constants.DataBase.Doctors_NODE).child(mAuth.getUid())
                .child(Constants.DataBase.Doctors_Statues_NODE).setValue(Doctor.UNDER_REVIEW_STATUS)
                .addOnFailureListener(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                view.hideLoading();
                view.openMessageActivity(R.string.profile_under_review);
            }
        });
    }
}
