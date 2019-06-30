package com.clinica.doctors.Activities.Auth.Address;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_governorate)
    TextInputLayout inputGovernorate;
    @BindView(R.id.governorate_et)
    AppCompatEditText governorateEt;
    @BindView(R.id.input_city)
    TextInputLayout inputCity;
    @BindView(R.id.city_et)
    AppCompatEditText cityEt;
    @BindView(R.id.input_state_en)
    TextInputLayout inputStateEn;
    @BindView(R.id.state_en_et)
    AppCompatEditText stateEnEt;
    @BindView(R.id.input_state_ar)
    TextInputLayout inputStateAr;
    @BindView(R.id.state_ar_et)
    AppCompatEditText stateArEt;
    @BindView(R.id.input_build_num_en)
    TextInputLayout inputBuildNumEn;
    @BindView(R.id.build_num_en_et)
    AppCompatEditText buildNumEnEt;
    @BindView(R.id.input_build_num_ar)
    TextInputLayout inputBuildNumAr;
    @BindView(R.id.build_num_ar_et)
    AppCompatEditText buildNumArEt;
    @BindView(R.id.input_street_en)
    TextInputLayout inputStreetEn;
    @BindView(R.id.street_en_et)
    AppCompatEditText streetEnEt;
    @BindView(R.id.input_street_ar)
    TextInputLayout inputStreetAr;
    @BindView(R.id.street_ar_et)
    AppCompatEditText streetArEt;
    @BindView(R.id.input_floor_en)
    TextInputLayout inputFloorEn;
    @BindView(R.id.floor_en_et)
    AppCompatEditText floorEnEt;
    @BindView(R.id.input_floor_ar)
    TextInputLayout inputFloorAr;
    @BindView(R.id.floor_ar_et)
    AppCompatEditText floorArEt;
    @BindView(R.id.input_apartment_en)
    TextInputLayout inputApartmentEn;
    @BindView(R.id.apartment_en_et)
    AppCompatEditText apartmentEnEt;
    @BindView(R.id.input_apartment_ar)
    TextInputLayout inputApartmentAr;
    @BindView(R.id.apartment_ar_et)
    AppCompatEditText apartmentArEt;
    @BindView(R.id.input_landmark_ar)
    TextInputLayout inputLandmarkAr;
    @BindView(R.id.landmark_ar_et)
    AppCompatEditText landmarkArEt;
    @BindView(R.id.input_landmark_en)
    TextInputLayout inputLandmarkEn;
    @BindView(R.id.landmark_en_et)
    AppCompatEditText landmarkEnEt;
    @BindView(R.id.address_ar_section)
    ConstraintLayout addressArSection;
    @BindView(R.id.address_ar_title)
    TextView addressArTitle;
    @BindView(R.id.output_address_ar)
    TextView outputAddressAr;
    @BindView(R.id.address_en_section)
    ConstraintLayout addressEnSection;
    @BindView(R.id.address_en_title)
    TextView addressEnTitle;
    @BindView(R.id.output_address_en)
    TextView outputAddressEn;

    private com.clinica.doctors.Models.Doctor.Address addressAr, addressEn;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_address;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addressAr = new com.clinica.doctors.Models.Doctor.Address();
        addressEn = new com.clinica.doctors.Models.Doctor.Address();
    }

    @Override
    protected void initActions() {
        Intent intent = getIntent();
        LatLng location = intent.getParcelableExtra(Constants.Intents.ADDRESS_LOCATION);
        addressAr.setLat(location.latitude);
        addressEn.setLat(location.latitude);
        addressAr.setLng(location.longitude);
        addressEn.setLng(location.longitude);
        Geocoder geocoderAr = new Geocoder(this, new Locale("ar"));
        Geocoder geocoderEn = new Geocoder(this, new Locale("en"));
        List<Address> addressesAr, addressesEn;
        try {
            addressesAr = geocoderAr.getFromLocation(location.latitude, location.longitude, 1);
            addressesEn = geocoderEn.getFromLocation(location.latitude, location.longitude, 1);
            String streetNameAr = addressesAr.get(0).getThoroughfare();
            String streetNameEn = addressesEn.get(0).getThoroughfare();
            String stateAr = addressesAr.get(0).getLocality();
            String stateEn = addressesEn.get(0).getLocality();
            String cityAr = addressesAr.get(0).getSubAdminArea();
            String cityEn = addressesEn.get(0).getSubAdminArea();
            String governorateAr = addressesAr.get(0).getAdminArea();
            String governorateEn = addressesEn.get(0).getAdminArea();
            Log.i("mAddress", "initActions: " + addressesAr.get(0).toString());
            Log.i("mAddress", "initActions: " + addressesEn.get(0).toString());
            addressAr.setGovernorate(governorateAr);
            addressAr.setCity(cityAr);
            addressEn.setGovernorate(governorateEn);
            addressEn.setCity(cityEn);
            if (UserData.getLocalization(this) == Localization.ARABIC_VALUE) {
                governorateEt.setText(governorateAr);
                cityEt.setText(cityAr);
            } else {
                governorateEt.setText(governorateEn);
                cityEt.setText(cityEn);
            }
            streetArEt.setText(streetNameAr);
            streetEnEt.setText(streetNameEn);
            stateArEt.setText(stateAr);
            stateEnEt.setText(stateEn);
            addressAr.setStreetName(streetNameAr);
            addressAr.setState(stateAr);
            addressEn.setStreetName(streetNameEn);
            addressEn.setState(stateEn);
            inputGovernorate.setEnabled(false);
            inputCity.setEnabled(false);
            outputAddressAr.setText(addressAr.getFullAddress(this, Localization.ARABIC_VALUE));
            outputAddressEn.setText(addressEn.getFullAddress(this, Localization.ENGLISH_VALUE));
        } catch (IOException e) {
            Log.i("address", "initActions: error " + e.getMessage());
        }

        streetArEt.addTextChangedListener(watcher);
        streetEnEt.addTextChangedListener(watcher);
        stateArEt.addTextChangedListener(watcher);
        stateEnEt.addTextChangedListener(watcher);
        buildNumArEt.addTextChangedListener(watcher);
        buildNumEnEt.addTextChangedListener(watcher);
        floorArEt.addTextChangedListener(watcher);
        floorEnEt.addTextChangedListener(watcher);
        apartmentArEt.addTextChangedListener(watcher);
        apartmentEnEt.addTextChangedListener(watcher);
        landmarkArEt.addTextChangedListener(watcher);
        landmarkEnEt.addTextChangedListener(watcher);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            addressAr.setState(stateArEt.getText().toString().trim());
            addressAr.setBuildingNumber(buildNumArEt.getText().toString().trim());
            addressAr.setStreetName(streetArEt.getText().toString().trim());
            addressAr.setFloor(floorArEt.getText().toString().trim());
            addressAr.setApartment(apartmentArEt.getText().toString().trim());
            addressAr.setLandmark(landmarkArEt.getText().toString().trim());

            addressEn.setState(stateEnEt.getText().toString().trim());
            addressEn.setBuildingNumber(buildNumEnEt.getText().toString().trim());
            addressEn.setStreetName(streetEnEt.getText().toString().trim());
            addressEn.setFloor(floorEnEt.getText().toString().trim());
            addressEn.setApartment(apartmentEnEt.getText().toString().trim());
            addressEn.setLandmark(landmarkEnEt.getText().toString().trim());

            outputAddressAr.setText(addressAr.getFullAddress(AddressActivity.this, Localization.ARABIC_VALUE));
            outputAddressEn.setText(addressEn.getFullAddress(AddressActivity.this, Localization.ENGLISH_VALUE));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_profile) {
            Intent intent = new Intent();
            intent.putExtra(Constants.Intents.ADDRESS_AR, addressAr);
            intent.putExtra(Constants.Intents.ADDRESS_EN, addressEn);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
