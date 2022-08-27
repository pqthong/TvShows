package com.example.tvshows.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvshows.R;
import com.example.tvshows.databinding.ItemContainnerSliderImageBinding;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {

    private String[] sliderImage;
    private LayoutInflater layoutInflater;

    public ImageSliderAdapter(String[] sliderImage) {
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainnerSliderImageBinding sliderImageBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_containner_slider_image, parent, false);

        return new ImageSliderViewHolder(sliderImageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        holder.bindSliderImage(sliderImage[position]);
    }

    @Override
    public int getItemCount() {
        return sliderImage.length;
    }

    static class ImageSliderViewHolder extends RecyclerView.ViewHolder {

        private ItemContainnerSliderImageBinding itemContainnerSliderImageBinding;

        public ImageSliderViewHolder(ItemContainnerSliderImageBinding itemContainnerSliderImageBinding) {
            super(itemContainnerSliderImageBinding.getRoot());
            this.itemContainnerSliderImageBinding = itemContainnerSliderImageBinding;
        }

        public void bindSliderImage(String imageUrl) {
            itemContainnerSliderImageBinding.setImageUrl(imageUrl);
        }

    }

}
