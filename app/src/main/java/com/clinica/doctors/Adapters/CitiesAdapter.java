package com.clinica.doctors.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.clinica.doctors.Models.City;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CitiesViewHolder> {

    private Context context;
    private List<City> dataList;
    private LayoutInflater inflater;
    private OnCitySelected onCitySelected;

    public CitiesAdapter(Context context) {
        this.context = context;
        this.dataList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnCitySelected(OnCitySelected onCitySelected) {
        this.onCitySelected = onCitySelected;
    }

    public void addData(City city) {
        dataList.add(city);
        sortList();
    }

    public void updateData(City city) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId() == city.getId()) {
                dataList.set(i, city);
                sortList();
            }
        }
    }

    private void sortList() {
        Collections.sort(dataList, new Comparator<City>() {
            @Override
            public int compare(City ads1, City ads2) {
                if (UserData.getLocalization(context) == Localization.ARABIC_VALUE)
                    return ads1.getTitleAr().compareTo(ads2.getTitleAr());
                else return ads1.getTitleEn().compareTo(ads2.getTitleEn());
            }
        });
        notifyDataSetChanged();
    }

    public void removeData(City city) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId() == city.getId()) {
                dataList.remove(i);
                sortList();
            }
        }
    }

    public boolean filterData(List<City> fullList, String q) {
        q = q.toLowerCase();
        dataList.clear();
        if (TextUtils.isEmpty(q)) {
            dataList.addAll(fullList);
        } else {
            for (int i = 0; i < fullList.size(); i++) {
                if (fullList.get(i).getTitleAr().toLowerCase().contains(q) ||
                        fullList.get(i).getTitleEn().toLowerCase().contains(q)) {
                    dataList.add(fullList.get(i));
                }
            }
        }
        sortList();
        return dataList.size() > 0;
    }

    @NonNull
    @Override
    public CitiesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View itemView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, viewGroup, false);
        return new CitiesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CitiesViewHolder citiesViewHolder, int position) {
        final City city = dataList.get(position);
        if (UserData.getLocalization(context) == Localization.ARABIC_VALUE) {
            citiesViewHolder.text.setText(city.getTitleAr());
        } else citiesViewHolder.text.setText(city.getTitleEn());
        citiesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCitySelected != null) {
                    onCitySelected.onSelect(city);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class CitiesViewHolder extends RecyclerView.ViewHolder {

        @BindView(android.R.id.text1)
        CheckedTextView text;

        CitiesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            text.setPadding(text.getPaddingLeft() + 50, text.getPaddingTop(), text.getPaddingRight() + 50, text.getPaddingBottom());
        }
    }

    public interface OnCitySelected {
        void onSelect(City city);
    }
}
