package com.kurnivan_ny.foodit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kurnivan_ny.foodit.databinding.ImageContainerBinding

class ImageAdapterHome(private val items: List<ImageHomeData>, private val viewPager2: ViewPager2)
    :RecyclerView.Adapter<ImageAdapterHome.ImageViewHolder>(){

    inner class ImageViewHolder(itemView: ImageContainerBinding)
        :RecyclerView.ViewHolder(itemView.root){

        private val binding = itemView
        fun bind(data: ImageHomeData, position: Int){

            if (position.equals(0)){
                karbohidrat(data.status_konsumsi_karbohidrat, data.total_konsumsi_karbohidrat)
            }
            if (position.equals(1)){
                protein(data.status_konsumsi_protein, data.total_konsumsi_protein)
            }
            if (position.equals(2)){
                lemak(data.status_konsumsi_lemak, data.total_konsumsi_lemak)
            }
        }

        private fun karbohidrat(statusKonsumsiKarbohidrat: String, totalKonsumsiKarbohidrat: Float) {
            if (statusKonsumsiKarbohidrat.equals("")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.transparent))
                binding.ivTitle.setImageResource(R.drawable.karbohidrat)
                binding.tvTittle.setText("Karbohidrat")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiKarbohidrat)} gr")
            } else if (statusKonsumsiKarbohidrat.equals("Kurang")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.kurang))
                binding.ivTitle.setImageResource(R.drawable.karbohidrat)
                binding.tvTittle.setText("Karbohidrat")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiKarbohidrat)} gr")
            } else if (statusKonsumsiKarbohidrat.equals("Normal")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.normal))
                binding.ivTitle.setImageResource(R.drawable.karbohidrat)
                binding.tvTittle.setText("Karbohidrat")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiKarbohidrat)} gr")
            } else if (statusKonsumsiKarbohidrat.equals("Lebih")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.lebih))
                binding.ivTitle.setImageResource(R.drawable.karbohidrat)
                binding.tvTittle.setText("Karbohidrat")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiKarbohidrat)} gr")
            }
        }

        private fun protein(statusKonsumsiProtein: String, totalKonsumsiProtein: Float) {
            if (statusKonsumsiProtein.equals("")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.transparent))
                binding.ivTitle.setImageResource(R.drawable.protein)
                binding.tvTittle.setText("Protein")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiProtein)} gr")
            } else if (statusKonsumsiProtein.equals("Kurang")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.kurang))
                binding.ivTitle.setImageResource(R.drawable.protein)
                binding.tvTittle.setText("Protein")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiProtein)} gr")
            } else if (statusKonsumsiProtein.equals("Normal")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.normal))
                binding.ivTitle.setImageResource(R.drawable.protein)
                binding.tvTittle.setText("Protein")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiProtein)} gr")
            } else if (statusKonsumsiProtein.equals("Lebih")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.lebih))
                binding.ivTitle.setImageResource(R.drawable.protein)
                binding.tvTittle.setText("Protein")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiProtein)} gr")
            }
        }

        private fun lemak(statusKonsumsiLemak: String, totalKonsumsiLemak: Float) {
            if (statusKonsumsiLemak.equals("")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.transparent))
                binding.ivTitle.setImageResource(R.drawable.lemak)
                binding.tvTittle.setText("Lemak")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiLemak)} gr")
            } else if (statusKonsumsiLemak.equals("Kurang")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.kurang))
                binding.ivTitle.setImageResource(R.drawable.lemak)
                binding.tvTittle.setText("Lemak")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiLemak)} gr")
            } else if (statusKonsumsiLemak.equals("Normal")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.normal))
                binding.ivTitle.setImageResource(R.drawable.lemak)
                binding.tvTittle.setText("Lemak")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiLemak)} gr")
            } else if (statusKonsumsiLemak.equals("Lebih")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.lebih))
                binding.ivTitle.setImageResource(R.drawable.lemak)
                binding.tvTittle.setText("Lemak")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiLemak)} gr")
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageAdapterHome.ImageViewHolder {
        return ImageViewHolder(
            ImageContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageAdapterHome.ImageViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}