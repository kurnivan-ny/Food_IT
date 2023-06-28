package com.kurnivan_ny.foodit.view.adapter.manualinput

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kurnivan_ny.foodit.data.model.modelui.manualinput.ListManualModel
import com.kurnivan_ny.foodit.databinding.ListItemManualBinding
import com.kurnivan_ny.foodit.view.adapter.history.OnItemClickListener

class ListManualAdapter(var manualList: ArrayList<ListManualModel>):
    RecyclerView.Adapter<ListManualAdapter.ListManualViewHolder>() {

    var useruid: String = ""
    var tanggal_makan: String = ""
    var bulan_makan: String = ""

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ListManualViewHolder(private val itemBinding: ListItemManualBinding):
        RecyclerView.ViewHolder(itemBinding.root){
        fun bind(listManualModel: ListManualModel, position: Int){

            itemBinding.tvTitle.text = listManualModel.nama_makanan
            itemBinding.tvDescription.text = listManualModel.berat_makanan.toString()+ " " + listManualModel.satuan_makanan

            itemBinding.btnCancel.setOnClickListener {
                deleteItem(position)
                deleteFirestore(listManualModel)
            }
        }
        init {
            itemBinding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position, manualList[position].nama_makanan)
                    notifyItemChanged(position)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListManualViewHolder {
        val view = ListItemManualBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListManualViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListManualViewHolder, position: Int) {
        val makanan:ListManualModel = manualList[position]
        holder.bind(makanan, position)
    }

    override fun getItemCount(): Int {
        return manualList.size
    }

    fun deleteItem(position: Int){
        manualList.removeAt(position)
        notifyItemChanged(position)
        notifyItemRangeChanged(position,manualList.size)
        notifyDataSetChanged()
    }

    fun deleteFirestore(listManualModel: ListManualModel) {

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        db.collection("users").document(useruid)
            .collection(bulan_makan).document(tanggal_makan)
            .collection(listManualModel.waktu_makan).document(listManualModel.nama_makanan)
            .delete().addOnSuccessListener {

                db.collection("users").document(useruid)
                    .collection(bulan_makan).document(tanggal_makan)
                    .get().addOnSuccessListener {
                        val total_konsumsi_karbohidrat:Float = (it.get("total_konsumsi_karbohidrat").toString()+"F").toFloat()
                        var total_karbohidrat = total_konsumsi_karbohidrat - listManualModel.karbohidrat

                        val total_konsumsi_protein:Float = (it.get("total_konsumsi_protein").toString()+"F").toFloat()
                        var total_protein = total_konsumsi_protein - listManualModel.protein

                        val total_konsumsi_lemak:Float = (it.get("total_konsumsi_lemak").toString()+"F").toFloat()
                        var total_lemak = total_konsumsi_lemak - listManualModel.lemak

                        if (total_karbohidrat<0.00F || total_protein<0.00F || total_lemak<0.00F){
                            total_karbohidrat = 0.00F
                            total_protein = 0.00F
                            total_lemak = 0.00F
                        }

                        db.collection("users").document(useruid)
                            .collection(bulan_makan).document(tanggal_makan)
                            .update("total_konsumsi_karbohidrat",total_karbohidrat,
                                "total_konsumsi_protein", total_protein,
                                "total_konsumsi_lemak", total_lemak)
                    }

            }
    }

}