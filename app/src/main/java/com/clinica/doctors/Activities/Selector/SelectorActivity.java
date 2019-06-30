package com.clinica.doctors.Activities.Selector;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Adapters.CitiesAdapter;
import com.clinica.doctors.Adapters.DiseasesAdapter;
import com.clinica.doctors.Adapters.SpecializationAdapter;
import com.clinica.doctors.Models.City;
import com.clinica.doctors.Models.Disease;
import com.clinica.doctors.Models.Doctor.Specialization;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.RecyclerDividerItemDecoration;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectorActivity extends BaseActivity implements SelectorView {

    SelectorPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_btn)
    AppCompatImageView searchBtn;
    @BindView(R.id.search_input_layout)
    TextInputLayout inputSearch;
    @BindView(R.id.search_input)
    AppCompatEditText searchET;
    @BindView(R.id.close_btn)
    AppCompatImageView closeBtn;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.empty)
    AppCompatTextView emptyText;

    private List<City> cities;
    private CitiesAdapter citiesAdapter;

    private List<Specialization> specializationList;
    private SpecializationAdapter specializationAdapter;

    private List<Disease> diseaseList;
    private DiseasesAdapter diseasesAdapter;

    @Override
    protected int setLayoutView() {
        return R.layout.recycler_search_layout;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        presenter = new SelectorPresenter(this, this);
        switch (getIntent().getStringExtra(Constants.Intents.SELECTION_KEY)) {
            case Constants.Intents.SELECT_CITY:
                toolbar.setTitle(getString(R.string.city_selector_title));
                searchET.setHint(getString(R.string.city_selector_search_hint));
                break;
            case Constants.Intents.SELECT_SPECIALIZATION:
                toolbar.setTitle(getString(R.string.specialization_selector_title));
                searchET.setHint(getString(R.string.specialization_selector_search_hint));
                break;
            case Constants.Intents.SELECT_DISEASES:
                toolbar.setTitle(getString(R.string.diseases_selector_title));
                searchET.setHint(getString(R.string.diseases_selector_search_hint));
                break;
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initActions() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    inputSearch.setError(null);
                    inputSearch.setErrorEnabled(false);
                }
                switch (getIntent().getStringExtra(Constants.Intents.SELECTION_KEY)) {
                    case Constants.Intents.SELECT_CITY:
                        citiesAdapter.filterData(cities, s.toString());
                        break;
                    case Constants.Intents.SELECT_SPECIALIZATION:
                        specializationAdapter.filterData(specializationList, s.toString());
                        break;
                    case Constants.Intents.SELECT_DISEASES:
                        diseasesAdapter.filterData(diseaseList, s.toString());
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchClicked();
                    return true;
                }
                return false;
            }
        });

        cities = new ArrayList<>();
        specializationList = new ArrayList<>();
        diseaseList = new ArrayList<>();
        citiesAdapter = new CitiesAdapter(this);
        specializationAdapter = new SpecializationAdapter(this);
        diseasesAdapter = new DiseasesAdapter(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 20));
        switch (getIntent().getStringExtra(Constants.Intents.SELECTION_KEY)) {
            case Constants.Intents.SELECT_CITY:
                recyclerView.setAdapter(citiesAdapter);
                presenter.getAllData(Constants.DataBase.CITIES_NODE);
                citiesAdapter.setOnCitySelected(new CitiesAdapter.OnCitySelected() {
                    @Override
                    public void onSelect(City city) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.Intents.CITY_DATA, city);
                        setResultOk(intent);
                    }
                });
                break;
            case Constants.Intents.SELECT_SPECIALIZATION:
                recyclerView.setAdapter(specializationAdapter);
                presenter.getAllData(Constants.DataBase.Specialization_NODE);
                specializationAdapter.setOnSpecializationSelected(new SpecializationAdapter.OnSpecializationSelected() {
                    @Override
                    public void onSelect(Specialization specialization) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.Intents.SPECIALIZATION_DATA, specialization);
                        setResultOk(intent);
                    }
                });
                break;
            case Constants.Intents.SELECT_DISEASES:
                recyclerView.setAdapter(diseasesAdapter);
                presenter.getAllData(Constants.DataBase.Diseases_NODE);
                diseasesAdapter.setOnDiseaseSelected(new DiseasesAdapter.OnDiseaseSelected() {
                    @Override
                    public void onSelect(Disease disease) {
//                        Intent intent = new Intent(SelectorActivity.this, CategoryDetailsActivity.class);
//                        intent.putExtra(Constants.Intents.SELECTION_KEY, Constants.Intents.SELECT_DISEASES);
//                        intent.putExtra(Constants.Intents.DISEASES_ID, disease.getId());
//                        startActivity(intent);
                    }
                });
                break;
        }
    }

    @OnClick(R.id.search_btn)
    void onSearchClicked() {
        String searchTxt = searchET.getText().toString().trim();
        switch (getIntent().getStringExtra(Constants.Intents.SELECTION_KEY)) {
            case Constants.Intents.SELECT_CITY:
                citiesAdapter.filterData(cities, searchTxt);
                break;
            case Constants.Intents.SELECT_SPECIALIZATION:
                specializationAdapter.filterData(specializationList, searchTxt);
                break;
            case Constants.Intents.SELECT_DISEASES:
                diseasesAdapter.filterData(diseaseList, searchTxt);
                break;
        }
        presenter.hideKeypad();
        recyclerView.requestFocus();
    }

    @OnClick(R.id.close_btn)
    void onCloseClicked() {
        presenter.hideKeypad();
        searchET.setText("");
        recyclerView.requestFocus();
    }

    @Override
    public void setSearchError(String message) {
        inputSearch.setError(message);
    }

    @Override
    public void showEmptyMessage(String message) {
        emptyText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyText.setText(message);
    }

    @Override
    public void onItemAdded(@NonNull DataSnapshot dataSnapshot) {
        switch (getIntent().getStringExtra(Constants.Intents.SELECTION_KEY)) {
            case Constants.Intents.SELECT_CITY:
                City city = dataSnapshot.getValue(City.class);
                cities.add(city);
                citiesAdapter.addData(city);
                break;
            case Constants.Intents.SELECT_SPECIALIZATION:
                Specialization specialization = dataSnapshot.getValue(Specialization.class);
                specialization.setId(dataSnapshot.getKey());
                specializationList.add(specialization);
                specializationAdapter.addData(specialization);
                break;
            case Constants.Intents.SELECT_DISEASES:
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setId(dataSnapshot.getKey());
                diseaseList.add(disease);
                diseasesAdapter.addData(disease);
                break;
        }
        emptyText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemUpdated(@NonNull DataSnapshot dataSnapshot) {
        switch (getIntent().getStringExtra(Constants.Intents.SELECTION_KEY)) {
            case Constants.Intents.SELECT_CITY:
                City city = dataSnapshot.getValue(City.class);
                citiesAdapter.updateData(city);
                for (int i = 0; i < cities.size(); i++) {
                    if (cities.get(i).getId() == city.getId()) {
                        cities.set(i, city);
                    }
                }
                break;
            case Constants.Intents.SELECT_SPECIALIZATION:
                Specialization specialization = dataSnapshot.getValue(Specialization.class);
                specialization.setId(dataSnapshot.getKey());
                specializationAdapter.updateData(specialization);
                for (int i = 0; i < specializationList.size(); i++) {
                    if (specializationList.get(i).getId().equals(specialization.getId())) {
                        specializationList.set(i, specialization);
                    }
                }
                break;
            case Constants.Intents.SELECT_DISEASES:
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setId(dataSnapshot.getKey());
                diseasesAdapter.updateData(disease);
                for (int i = 0; i < diseaseList.size(); i++) {
                    if (diseaseList.get(i).getId().equals(disease.getId())) {
                        diseaseList.set(i, disease);
                    }
                }
                break;
        }
    }

    @Override
    public void onItemRemoved(@NonNull DataSnapshot dataSnapshot) {
        switch (getIntent().getStringExtra(Constants.Intents.SELECTION_KEY)) {
            case Constants.Intents.SELECT_CITY:
                City city = dataSnapshot.getValue(City.class);
                citiesAdapter.removeData(city);
                for (int i = 0; i < cities.size(); i++) {
                    if (cities.get(i).getId() == city.getId()) {
                        cities.remove(i);
                    }
                }
                if (cities.size() == 0) {
                    showEmptyMessage(getString(R.string.empty_list));
                }
                break;
            case Constants.Intents.SELECT_SPECIALIZATION: {
                Specialization specialization = dataSnapshot.getValue(Specialization.class);
                specialization.setId(dataSnapshot.getKey());
                specializationAdapter.removeData(specialization);
                for (int i = 0; i < specializationList.size(); i++) {
                    if (specializationList.get(i).getId().equals(specialization.getId())) {
                        specializationList.remove(i);
                    }
                }
                if (specializationList.size() == 0) {
                    showEmptyMessage(getString(R.string.empty_list));
                }
                break;
            }
            case Constants.Intents.SELECT_DISEASES: {
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setId(dataSnapshot.getKey());
                diseasesAdapter.removeData(disease);
                for (int i = 0; i < diseaseList.size(); i++) {
                    if (diseaseList.get(i).getId().equals(disease.getId())) {
                        diseaseList.remove(i);
                    }
                }
                if (diseaseList.size() == 0) {
                    showEmptyMessage(getString(R.string.empty_list));
                }
                break;
            }
        }
    }

    @Override
    public void setResultOk(Intent data) {
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void setResultCancelled() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultCancelled();
    }
}
