package com.traversoft.gdgphotoshare.ui.fragments.speaker;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.traversoft.gdgphotoshare.R;
import com.traversoft.gdgphotoshare.data.Speaker;
import com.traversoft.gdgphotoshare.databinding.ViewholderSpeakerBinding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * Clutch for Android
 * Copyright © 2016 HFA. All rights reserved.
 */


public class SpeakerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Speaker> speakers;

    private @Getter @Setter @Nullable OnClickSpeakerListener onClickSpeakerListener;

    private class SpeakerViewHolder extends RecyclerView.ViewHolder {
        private @NonNull ViewholderSpeakerBinding binding;

        public SpeakerViewHolder(@NonNull ViewholderSpeakerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        SpeakerViewHolder(@NonNull ViewGroup parent) {
            this(ViewholderSpeakerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    interface OnClickSpeakerListener {
        void onClick(Speaker speaker);
    }

    SpeakerAdapter(Context context) {
        this.context = context;
    }

    void setSpeakers(@NonNull LinkedHashMap<Integer, Speaker> speakers) {
        if (speakers.size() > 0) {
            this.speakers = new ArrayList<>(speakers.values());
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SpeakerViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (this.speakers != null) {
            Speaker speaker = this.speakers.get(position);
            if (speaker == null) {
                return;
            }

            Context context = ((SpeakerViewHolder) holder).binding.getRoot().getContext();

            if (!speaker.getPhotoUrl().isEmpty()) {
                Picasso.with(this.context)
                        .load(context.getString(R.string.speaker_url) + speaker.getPhotoUrl())
                        .into(((SpeakerViewHolder) holder).binding.profileImage, new Callback() {
                            @Override public void onSuccess() {

                            }

                            @Override public void onError() {

                            }
                        });
            }

            ((SpeakerViewHolder) holder).binding.name.setText(speaker.getName());
            ((SpeakerViewHolder) holder).binding.country.setText(speaker.getCountry().trim());
            ((SpeakerViewHolder) holder).binding.company.setText(speaker.getCompany());
            ((SpeakerViewHolder) holder).binding.getRoot().setOnClickListener(
                    v -> {
                        if (onClickSpeakerListener != null) {
                            onClickSpeakerListener.onClick(speaker);
                        }
                    }
            );
        }
    }

    @Override
    public int getItemCount() {
        if (this.speakers != null) {
            return this.speakers.size() > 0 ? this.speakers.size() : 0;
        }
        return 0;
    }
}