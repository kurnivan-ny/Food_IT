package com.kurnivan_ny.foodit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kurnivan_ny.foodit.data.model.history.ListDetailHistoryModel
import com.kurnivan_ny.foodit.databinding.ListItemHistoryBinding

class ListDetailLemakHistoryAdapter(var detailhistoryList: ArrayList<ListDetailHistoryModel>)
    :RecyclerView.Adapter<ListDetailLemakHistoryAdapter.DetailListHistoryViewModel>(){

    inner class DetailListHistoryViewModel(private val itemBinding: ListItemHistoryBinding)
        :RecyclerView.ViewHolder(itemBinding.root){
            fun bind(data: ListDetailHistoryModel){
                itemBinding.tvTitle.text = data.nama_makanan
                itemBinding.tvDescription.text = "${String.format("%.2f", (data.lemak.toString() + "F").toFloat())} gr"
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailListHistoryViewModel {
        val view = ListItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailListHistoryViewModel(view)
    }

    override fun onBindViewHolder(holder: DetailListHistoryViewModel, position: Int) {
        val detailhistory: ListDetailHistoryModel = detailhistoryList[position]
        holder.bind(detailhistory)
    }

    override fun getItemCount(): Int {
        return detailhistoryList.size
    }
}