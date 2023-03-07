package com.kurnivan_ny.foodit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kurnivan_ny.foodit.data.model.manualinput.SearchData
import com.kurnivan_ny.foodit.databinding.SearchItemBinding

class SearchAdapter(var searchList: List<SearchData>): RecyclerView.Adapter<SearchAdapter.SearchListViewHolder>() {

    var onItemClick: ((SearchData) -> Unit)? = null

    class SearchListViewHolder(private val itemBinding: SearchItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(searchModel: SearchData){
            itemBinding.singleItemFood.text = searchModel.nama_makanan
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchListViewHolder {
        val view = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        val food = searchList[position]
        holder.bind(food)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(food)
        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

}