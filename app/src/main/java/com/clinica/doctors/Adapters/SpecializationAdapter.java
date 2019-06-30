package com.clinica.doctors.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinica.doctors.Models.Doctor.Specialization;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecializationAdapter extends RecyclerView.Adapter<SpecializationAdapter.SpecializationsViewHolder> {

    private Context context;
    private List<Specialization> dataList;
    private LayoutInflater inflater;
    private OnSpecializationSelected onSpecializationSelected;

    public SpecializationAdapter(Context context) {
        this.context = context;
        this.dataList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnSpecializationSelected(OnSpecializationSelected onSpecializationSelected) {
        this.onSpecializationSelected = onSpecializationSelected;
    }

    public void addData(Specialization specialization) {
        dataList.add(specialization);
        sortList();
    }

    public void updateData(Specialization specialization) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId().equals(specialization.getId())) {
                dataList.set(i, specialization);
                sortList();
            }
        }
    }

    public void removeData(Specialization specialization) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId().equals(specialization.getId())) {
                dataList.remove(i);
                sortList();
            }
        }
    }

    private void sortList() {
        Collections.sort(dataList, new Comparator<Specialization>() {
            @Override
            public int compare(Specialization specialization1, Specialization specialization2) {
                if (UserData.getLocalization(context) == Localization.ARABIC_VALUE)
                    return specialization1.getTitleAr().compareTo(specialization2.getTitleAr());
                else return specialization1.getTitleEn().compareTo(specialization2.getTitleEn());
            }
        });
        notifyDataSetChanged();
    }

    public boolean filterData(List<Specialization> fullList, String q) {
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
    public SpecializationsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View itemView = inflater.inflate(R.layout.row_specialization_item, viewGroup, false);
        return new SpecializationsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecializationsViewHolder specializationsViewHolder, int position) {
        Specialization specialization = dataList.get(position);
        specializationsViewHolder.bindData(specialization);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class SpecializationsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.text)
        TextView text;

        SpecializationsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(final Specialization specialization) {
            Picasso.get()
                    .load(specialization.getImgURL())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(img);
            if (UserData.getLocalization(context) == Localization.ARABIC_VALUE)
                text.setText(specialization.getTitleAr());
            else text.setText(specialization.getTitleEn());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onSpecializationSelected != null) {
                        onSpecializationSelected.onSelect(specialization);
                    }
                }
            });
        }
    }

    public interface OnSpecializationSelected {
        void onSelect(Specialization specialization);
    }
}
