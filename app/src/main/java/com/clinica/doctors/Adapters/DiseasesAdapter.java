package com.clinica.doctors.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.clinica.doctors.Models.Disease;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiseasesAdapter extends RecyclerView.Adapter<DiseasesAdapter.DiseasesViewHolder> {

    private Context context;
    private List<Disease> dataList;
    private LayoutInflater inflater;
    private OnDiseaseSelected onDiseaseSelected;

    public DiseasesAdapter(Context context) {
        this.context = context;
        this.dataList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnDiseaseSelected(OnDiseaseSelected onDiseaseSelected) {
        this.onDiseaseSelected = onDiseaseSelected;
    }

    public void appendData(List<Disease> dataList) {
        int rangeStart = this.dataList.size();
        this.dataList.addAll(dataList);
        int rangeEnd = this.dataList.size();
        notifyItemRangeInserted(rangeStart, rangeEnd);
    }

    public void addData(Disease disease) {
        dataList.add(disease);
        notifyItemInserted(dataList.size());
    }

    public void updateData(Disease disease) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId().equals(disease.getId())) {
                dataList.set(i, disease);
                notifyItemChanged(i);
            }
        }
    }

    public void removeData(Disease disease) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId().equals(disease.getId())) {
                dataList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, getItemCount());
            }
        }
    }

    public boolean filterData(List<Disease> fullList, String q) {
        q = q.toLowerCase();
        dataList.clear();
        if (TextUtils.isEmpty(q)) {
            dataList.addAll(fullList);
        } else {
            for (int i = 0; i < fullList.size(); i++) {
                if (fullList.get(i).getTitle().toLowerCase().contains(q)) {
                    dataList.add(fullList.get(i));
                }
            }
        }
        notifyDataSetChanged();
        return dataList.size() > 0;
    }

    @NonNull
    @Override
    public DiseasesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View itemView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, viewGroup, false);
        return new DiseasesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseasesViewHolder diseasesViewHolder, int position) {
        final Disease disease = dataList.get(position);
        diseasesViewHolder.text.setText(disease.getTitle());
        diseasesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDiseaseSelected != null) {
                    onDiseaseSelected.onSelect(disease);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class DiseasesViewHolder extends RecyclerView.ViewHolder {

        @BindView(android.R.id.text1)
        CheckedTextView text;

        public DiseasesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            text.setPadding(text.getPaddingLeft() + 50, text.getPaddingTop(), text.getPaddingRight() + 50, text.getPaddingBottom());
        }
    }

    public interface OnDiseaseSelected {
        void onSelect(Disease disease);
    }
}
