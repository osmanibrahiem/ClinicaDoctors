package com.clinica.doctors.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.clinica.doctors.Models.Ads;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.AdsViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Ads> adsList;

    public AdsAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.adsList = new ArrayList<>();
    }

    public void addAds(Ads ads) {
        if (ads.getStatus().equals(Constants.ACTIVE_STATUS)) {
            long startDate = ads.getPublicationDate();
            long endDate = ads.getExpiryDate();
            long timeNow = new Date().getTime();

            if (timeNow >= startDate && timeNow <= endDate) {
                adsList.add(ads);
                sortList();
            }
        }
    }

    public void removeAds(Ads ads) {
        for (int i = 0; i < adsList.size(); i++) {
            if (adsList.get(i).getId().equals(ads.getId())) {
                adsList.remove(i);
                sortList();
            }
        }
    }

    public void updateAds(Ads ads) {
        if (ads.getStatus().equals(Constants.ACTIVE_STATUS)) {
            boolean isInList = false;
            for (int i = 0; i < adsList.size(); i++) {
                if (adsList.get(i).getId().equals(ads.getId())) {
                    isInList = true;
                    long startDate = ads.getPublicationDate();
                    long endDate = ads.getExpiryDate();
                    long timeNow = new Date().getTime();

                    if (timeNow >= startDate && timeNow <= endDate) {
                        adsList.set(i, ads);
                        sortList();
                    } else removeAds(ads);
                }
            }
            if (!isInList) {
                addAds(ads);
            }
        } else {
            removeAds(ads);
        }
    }

    private void sortList() {
        Collections.sort(adsList, new Comparator<Ads>() {
            @Override
            public int compare(Ads ads1, Ads ads2) {
                return Long.compare(ads2.getPublicationDate(), ads1.getPublicationDate());
            }
        });
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View convertView = inflater.inflate(R.layout.row_image_home_slider, viewGroup, false);
        return new AdsViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdsViewHolder holder, int position) {
        Log.i("mDataBase", "onBindViewHolder: position : " + position);
        final Ads ads = adsList.get(position);
        Picasso.get()
                .load(ads.getImageURL())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_placeholder)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return adsList.size();
    }

    class AdsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_view)
        ImageView imageView;

        AdsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
