package com.kurnivan_ny.foodit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kurnivan_ny.foodit.data.model.onboarding.ImageSlideModel
import com.kurnivan_ny.foodit.databinding.ImageSlideOnboardingBinding

class ImageSlideAdapter(private val items: List<ImageSlideModel>): RecyclerView.Adapter<ImageSlideAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(itemView: ImageSlideOnboardingBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val binding = itemView
        fun bind(data: ImageSlideModel) {
            with(binding){
                Glide.with(itemView)
                    .load(data.image)
                    .into(ivImage)
                tvImage.setText(data.text)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        return ImageViewHolder(
            ImageSlideOnboardingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}