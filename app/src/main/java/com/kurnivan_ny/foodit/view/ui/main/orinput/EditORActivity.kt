package com.kurnivan_ny.foodit.view.ui.main.orinput

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import com.kurnivan_ny.foodit.data.model.modelui.manualinput.ListManualModel
import com.kurnivan_ny.foodit.databinding.ActivityEditOdBinding
import com.kurnivan_ny.foodit.view.adapter.manualinput.ListManualAdapter
import com.kurnivan_ny.foodit.view.adapter.history.OnItemClickListener
import com.kurnivan_ny.foodit.view.ui.main.activity.HomeActivity
import com.kurnivan_ny.foodit.view.ui.main.manualinput.EditDetailMakananActivity
import com.kurnivan_ny.foodit.view.ui.main.manualinput.SearchMakananActivity
import com.kurnivan_ny.foodit.viewmodel.ManualViewModel
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class EditORActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditOdBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: FirebaseFirestore

    private var manualList: ArrayList<ListManualModel> = arrayListOf()
    private var manualListAdapter: ListManualAdapter = ListManualAdapter(manualList)

    private lateinit var viewModel: ManualViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditOdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = SharedPreferences(this)
        db = FirebaseFirestore.getInstance()

        viewModel = ViewModelProvider(this).get(ManualViewModel::class.java)

        binding.tvWaktuMakan.text = capitalize(sharedPreferences.getValuesString("waktu_makan"))

        binding.rvHasil.setHasFixedSize(true)
        binding.rvHasil.layoutManager = LinearLayoutManager(this)

        getDataFirestore()

        viewModel.newmanual.observe(this, Observer {
//            manualListAdapter.updateData(it)
            manualListAdapter.manualList = it
            manualListAdapter.notifyDataSetChanged()
        })

        binding.rvHasil.adapter = manualListAdapter

//        loadingDialog()

        binding.rvHasil.itemAnimator = SlideInUpAnimator()

        binding.svMakanan.isFocusable = false

        binding.svMakanan.setOnClickListener {
            val intent = Intent(this, SearchMakananActivity::class.java)
            startActivity(intent)
        }

        binding.btnKirim.setOnClickListener {

            val UserUID = sharedPreferences.getValuesString("user_uid")

            val username = sharedPreferences.getValuesString("username")
            val tanggal_makan = sharedPreferences.getValuesString("tanggal_makan")
            val bulan_makan = sharedPreferences.getValuesString("bulan_makan")

            db.collection("users").document(UserUID!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .get().addOnSuccessListener {
                    var total_karbohidrat: Float =
                        (it.get("total_konsumsi_karbohidrat").toString() + "F").toFloat()
                    var total_protein: Float =
                        (it.get("total_konsumsi_protein").toString() + "F").toFloat()
                    var total_lemak: Float =
                        (it.get("total_konsumsi_lemak").toString() + "F").toFloat()

                    sharedPreferences.setValuesFloat(
                        "total_konsumsi_karbohidrat",
                        total_karbohidrat
                    )
                    sharedPreferences.setValuesFloat("total_konsumsi_protein", total_protein)
                    sharedPreferences.setValuesFloat("total_konsumsi_lemak", total_lemak)
                }

            val intent = Intent(this@EditORActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        manualListAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int, nama_makanan: String) {
                val intent = Intent(this@EditORActivity, EditDetailMakananActivity::class.java)
                intent.putExtra("nama_makanan", nama_makanan)
                startActivity(intent)
                manualListAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun capitalize(str: String?): CharSequence? {
        return str?.trim()?.split("\\s+".toRegex())
            ?.map { it.capitalize() }?.joinToString(" ")
    }

    private fun getDataFirestore() {

        val UserUID = sharedPreferences.getValuesString("user_uid")

        val username = sharedPreferences.getValuesString("username")
        val tanggal_makan = sharedPreferences.getValuesString("tanggal_makan")
        val waktu_makan = sharedPreferences.getValuesString("waktu_makan")
        val bulan_makan = sharedPreferences.getValuesString("bulan_makan")

        db.collection("users").document(UserUID!!)
            .collection(bulan_makan!!).document(tanggal_makan!!)
            .collection(waktu_makan!!).orderBy("nama_makanan", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?) {
                    if (error != null){
                        Log.e("Firestore Error", error.message.toString())
                    }

                    val doc = arrayListOf<ListManualModel>()
                    if (value != null){
                        for(dc in value.documents){
                            val data = dc.toObject(ListManualModel::class.java)
                            if (data != null) {
                                doc.add(data)
                            }
                        }
                    }
                    viewModel.newmanual.value = doc
                    manualListAdapter.notifyDataSetChanged()
                }
            })

        manualListAdapter.useruid = UserUID
        manualListAdapter.tanggal_makan = tanggal_makan
        manualListAdapter.bulan_makan = bulan_makan
    }
}