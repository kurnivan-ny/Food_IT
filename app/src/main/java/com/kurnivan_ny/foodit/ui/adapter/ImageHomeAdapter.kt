package com.kurnivan_ny.foodit.ui.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kurnivan_ny.foodit.data.model.home.ImageHomeModel
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.databinding.ImageContainerBinding

class ImageHomeAdapter(private val items: List<ImageHomeModel>)
    :RecyclerView.Adapter<ImageHomeAdapter.ImageViewHolder>(){

    inner class ImageViewHolder(itemView: ImageContainerBinding)
        :RecyclerView.ViewHolder(itemView.root){

        private val binding = itemView
        fun bind(data: ImageHomeModel, position: Int) {

            if (position.equals(0)) {
                karbohidrat(data.status_konsumsi_karbohidrat, data.total_konsumsi_karbohidrat)
            }
            if (position.equals(1)) {
                protein(data.status_konsumsi_protein, data.total_konsumsi_protein)
            }
            if (position.equals(2)) {
                lemak(data.status_konsumsi_lemak, data.total_konsumsi_lemak)
            }

            binding.root.setOnClickListener {
                val view = View.inflate(itemView.context, R.layout.alert_home_dialog, null)
                val background = view.findViewById<ConstraintLayout>(R.id.cl_background)
                val tv_title = view.findViewById<TextView>(R.id.tv_title)
                val tv_dikonsumsi = view.findViewById<TextView>(R.id.tv_berat)
                val tv_batas_bawah = view.findViewById<TextView>(R.id.tv_batas_bawah)
                val tv_batas_atas = view.findViewById<TextView>(R.id.tv_batas_atas)
                val tv_penyakit = view.findViewById<TextView>(R.id.tv_penyakit)

                if (position.equals(0)) {
                    if (data.status_konsumsi_karbohidrat.equals("")) {
                        Toast.makeText(
                            itemView.context,
                            "Silakan Pilih Tanggal Makan",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (data.status_konsumsi_karbohidrat.equals("Kurang")) {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.kurang
                                )
                            )
                            tv_title.setText("Karbohidrat")
                            tv_dikonsumsi.setText(
                                "${String.format("%.2f", data.total_konsumsi_karbohidrat)}gr"
                            )
                            tv_batas_bawah.setText(
                                "${String.format("%.2f", data.totalenergikal / 4 * 0.55)} gr"
                            )
                            tv_batas_atas.setText(
                                "${ String.format("%.2f", data.totalenergikal / 4 * 0.75)} gr"
                            )

                            tv_penyakit.setText("Penyakit Akibat Kekurangan\nKarbohidrat:\n" +
                                    "1. Hiploglikemia\n" +
                                    "2. Gangguan Pencernaan\n" +
                                    "3. Kelelahan dan Lemas")
                        } else if (data.status_konsumsi_karbohidrat.equals("Normal")) {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.normal
                                )
                            )
                            tv_title.setText("Karbohidrat")
                            tv_dikonsumsi.setText(
                                "${String.format("%.2f", data.total_konsumsi_karbohidrat)}gr"
                            )
                            tv_batas_bawah.setText(
                                "${String.format("%.2f", data.totalenergikal / 4 * 0.55)} gr"
                            )
                            tv_batas_atas.setText(
                                "${ String.format("%.2f", data.totalenergikal / 4 * 0.75)} gr"
                            )
                            tv_penyakit.setText("Kandungan Karbohidrat Anda Normal")

                        } else if (data.status_konsumsi_karbohidrat.equals("Lebih")) {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.lebih
                                )
                            )
                            tv_title.setText("Karbohidrat")
                            tv_dikonsumsi.setText(
                                "${String.format("%.2f", data.total_konsumsi_karbohidrat)}gr"
                            )
                            tv_batas_bawah.setText(
                                "${String.format("%.2f", data.totalenergikal / 4 * 0.55)} gr"
                            )
                            tv_batas_atas.setText(
                                "${ String.format("%.2f", data.totalenergikal / 4 * 0.75)} gr"
                            )

                            tv_penyakit.setText("Penyakit Akibat Kelebihan\nKarbohidrat:\n" +
                                    "1. Diabetes Melitus Tipe 2\n" +
                                    "2. Obesitas\n" +
                                    "3. Penyakit jantung")
                        }

                        AlertDialog.Builder(itemView.context, R.style.MyAlertDialogTheme)
                            .setView(view)
                            .show()
                    }
                }

                if (position.equals(1)) {
                    if (data.status_konsumsi_protein.equals("")) {
                        Toast.makeText(
                            itemView.context,
                            "Silakan Pilih Tanggal Makan",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (data.status_konsumsi_protein.equals("Kurang")) {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.kurang
                                )
                            )
                            tv_title.setText("Protein")
                            tv_dikonsumsi.setText(
                                "${String.format("%.2f", data.total_konsumsi_karbohidrat)}gr"
                            )
                            tv_batas_bawah.setText(
                                "${String.format("%.2f", data.totalenergikal / 4 * 0.55)} gr"
                            )
                            tv_batas_atas.setText(
                                "${ String.format("%.2f", data.totalenergikal / 4 * 0.75)} gr"
                            )

                            tv_penyakit.setText("Penyakit akibat kekurangan\nProtein:\n" +
                                    "1. Kwashiorkor\n" +
                                    "2. Kerontokan rambut\n" +
                                    "3. Infeksi")
                        } else if (data.status_konsumsi_protein.equals("Normal")) {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.normal
                                )
                            )
                            tv_title.setText("Protein")
                            tv_dikonsumsi.setText(
                                "${String.format("%.2f", data.total_konsumsi_karbohidrat)}gr"
                            )
                            tv_batas_bawah.setText(
                                "${String.format("%.2f", data.totalenergikal / 4 * 0.55)} gr"
                            )
                            tv_batas_atas.setText(
                                "${ String.format("%.2f", data.totalenergikal / 4 * 0.75)} gr"
                            )
                            tv_penyakit.setText("Kandungan Protein Anda Normal")

                        } else if (data.status_konsumsi_protein.equals("Lebih")) {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.lebih
                                )
                            )
                            tv_title.setText("Protein")
                            tv_dikonsumsi.setText(
                                "${String.format("%.2f", data.total_konsumsi_karbohidrat)}gr"
                            )
                            tv_batas_bawah.setText(
                                "${String.format("%.2f", data.totalenergikal / 4 * 0.55)} gr"
                            )
                            tv_batas_atas.setText(
                                "${ String.format("%.2f", data.totalenergikal / 4 * 0.75)} gr"
                            )

                            tv_penyakit.setText("Penyakit Akibat Kelebihan\nProtein:\n" +
                                    "1. Gangguan Fungsi Ginjal\n" +
                                    "2. Osteoporosis\n" +
                                    "3. Penyakit Hati")
                        }
                        AlertDialog.Builder(itemView.context, R.style.MyAlertDialogTheme)
                            .setView(view)
                            .show()
                    }
                }

                if (position.equals(2)) {
                    if (data.status_konsumsi_lemak.equals("")) {
                        Toast.makeText(
                            itemView.context,
                            "Silakan Pilih Tanggal Makan",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (data.status_konsumsi_lemak.equals("Kurang")) {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.kurang
                                )
                            )
                            tv_title.setText("Lemak")
                            tv_dikonsumsi.setText(
                                "${String.format("%.2f", data.total_konsumsi_karbohidrat)}gr"
                            )
                            tv_batas_bawah.setText(
                                "${String.format("%.2f", data.totalenergikal / 4 * 0.55)} gr"
                            )
                            tv_batas_atas.setText(
                                "${ String.format("%.2f", data.totalenergikal / 4 * 0.75)} gr"
                            )

                            tv_penyakit.setText("Penyakit Akibat Kekurangan\nLemak:\n" +
                                    "1. Gangguan Sistem Saraf\n" +
                                    "2. Gangguan Kulit\n" +
                                    "3. Masalah Reproduksi")

                        } else if (data.status_konsumsi_lemak.equals("Normal")) {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.normal
                                )
                            )
                            tv_title.setText("Lemak")
                            tv_dikonsumsi.setText(
                                "${String.format("%.2f", data.total_konsumsi_karbohidrat)}gr"
                            )
                            tv_batas_bawah.setText(
                                "${String.format("%.2f", data.totalenergikal / 4 * 0.55)} gr"
                            )
                            tv_batas_atas.setText(
                                "${ String.format("%.2f", data.totalenergikal / 4 * 0.75)} gr"
                            )
                            tv_penyakit.setText("Kandungan Lemak Anda Normal")

                        } else if (data.status_konsumsi_lemak.equals("Lebih")) {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.lebih
                                )
                            )
                            tv_title.setText("Lemak")
                            tv_dikonsumsi.setText(
                                "${String.format("%.2f", data.total_konsumsi_karbohidrat)}gr"
                            )
                            tv_batas_bawah.setText(
                                "${String.format("%.2f", data.totalenergikal / 4 * 0.55)} gr"
                            )
                            tv_batas_atas.setText(
                                "${ String.format("%.2f", data.totalenergikal / 4 * 0.75)} gr"
                            )

                            tv_penyakit.setText("Penyakit Akibat Kelebihan\nLemak:\n" +
                                    "1. Obesitas\n" +
                                    "2. Penyakit Jantung\n" +
                                    "3. Kanker")
                        }

                        AlertDialog.Builder(itemView.context, R.style.MyAlertDialogTheme)
                            .setView(view)
                            .show()
                    }
                }
            }
        }

        private fun karbohidrat(statusKonsumsiKarbohidrat: String, totalKonsumsiKarbohidrat: Float) {
            if (statusKonsumsiKarbohidrat.equals("")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.transparent
                ))
                binding.ivTitle.setImageResource(R.drawable.karbohidrat)
                binding.tvTittle.setText("Karbohidrat")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiKarbohidrat)} gr")
            } else if (statusKonsumsiKarbohidrat.equals("Kurang")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.kurang
                ))
                binding.ivTitle.setImageResource(R.drawable.karbohidrat)
                binding.tvTittle.setText("Karbohidrat")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiKarbohidrat)} gr")
            } else if (statusKonsumsiKarbohidrat.equals("Normal")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.normal
                ))
                binding.ivTitle.setImageResource(R.drawable.karbohidrat)
                binding.tvTittle.setText("Karbohidrat")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiKarbohidrat)} gr")
            } else if (statusKonsumsiKarbohidrat.equals("Lebih")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.lebih
                ))
                binding.ivTitle.setImageResource(R.drawable.karbohidrat)
                binding.tvTittle.setText("Karbohidrat")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiKarbohidrat)} gr")
            }
        }

        private fun protein(statusKonsumsiProtein: String, totalKonsumsiProtein: Float) {
            if (statusKonsumsiProtein.equals("")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.transparent
                ))
                binding.ivTitle.setImageResource(R.drawable.protein)
                binding.tvTittle.setText("Protein")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiProtein)} gr")
            } else if (statusKonsumsiProtein.equals("Kurang")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.kurang
                ))
                binding.ivTitle.setImageResource(R.drawable.protein)
                binding.tvTittle.setText("Protein")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiProtein)} gr")
            } else if (statusKonsumsiProtein.equals("Normal")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.normal
                ))
                binding.ivTitle.setImageResource(R.drawable.protein)
                binding.tvTittle.setText("Protein")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiProtein)} gr")
            } else if (statusKonsumsiProtein.equals("Lebih")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.lebih
                ))
                binding.ivTitle.setImageResource(R.drawable.protein)
                binding.tvTittle.setText("Protein")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiProtein)} gr")
            }
        }

        private fun lemak(statusKonsumsiLemak: String, totalKonsumsiLemak: Float) {
            if (statusKonsumsiLemak.equals("")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.transparent
                ))
                binding.ivTitle.setImageResource(R.drawable.lemak)
                binding.tvTittle.setText("Lemak")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiLemak)} gr")
            } else if (statusKonsumsiLemak.equals("Kurang")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.kurang
                ))
                binding.ivTitle.setImageResource(R.drawable.lemak)
                binding.tvTittle.setText("Lemak")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiLemak)} gr")
            } else if (statusKonsumsiLemak.equals("Normal")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.normal
                ))
                binding.ivTitle.setImageResource(R.drawable.lemak)
                binding.tvTittle.setText("Lemak")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiLemak)} gr")
            } else if (statusKonsumsiLemak.equals("Lebih")){
                binding.cvMakro.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                    R.color.lebih
                ))
                binding.ivTitle.setImageResource(R.drawable.lemak)
                binding.tvTittle.setText("Lemak")
                binding.tvOutput.setText("${String.format("%.2f",totalKonsumsiLemak)} gr")
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {
        return ImageViewHolder(
            ImageContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}