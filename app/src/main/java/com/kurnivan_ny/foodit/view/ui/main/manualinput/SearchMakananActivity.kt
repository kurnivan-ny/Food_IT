package com.kurnivan_ny.foodit.view.ui.main.manualinput

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.kurnivan_ny.foodit.data.model.modelui.manualinput.SearchModel
import com.kurnivan_ny.foodit.databinding.ActivitySearchMakananBinding
import com.kurnivan_ny.foodit.view.adapter.manualinput.SearchAdapter
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class SearchMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchMakananBinding

    private lateinit var sharedPreferences: SharedPreferences

    private var searchList: List<SearchModel> = ArrayList()
    private val searchListAdapter = SearchAdapter(searchList)

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = SharedPreferences(this)
        db = FirebaseFirestore.getInstance()

        val UserUID = sharedPreferences.getValuesString("user_uid")

        val tanggal_makan = sharedPreferences.getValuesString("tanggal_makan")
        val waktu_makan = sharedPreferences.getValuesString("waktu_makan")
        val bulan_makan = sharedPreferences.getValuesString("bulan_makan")

        // Set up recycler view
        binding.rvSearchlist.hasFixedSize()
        binding.rvSearchlist.layoutManager = LinearLayoutManager(this)
        binding.rvSearchlist.adapter = searchListAdapter

        binding.rvSearchlist.itemAnimator  = SlideInUpAnimator()

        binding.ivBack.setOnClickListener {
            val intent = Intent(this@SearchMakananActivity, ManualInputActivity::class.java)
            startActivity(intent)
        }

        db.collection("food")
            .get().addOnCompleteListener {
                if (it.isSuccessful)
                {
                    searchList = it.result!!.toObjects(SearchModel::class.java)
                    searchListAdapter.searchList = searchList
                    searchListAdapter.notifyDataSetChanged()
                }
            }

        binding.svMakanan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                // Get Value of field
                binding.svMakanan.hint
                binding.svMakanan.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                val searchText = binding.svMakanan.text.toString()

                // Search in Firestore
                searchInFirestore(searchText.toLowerCase())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        searchListAdapter.onItemClick = {food ->

            db.collection("users").document(UserUID!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection(waktu_makan!!).document(food.nama_makanan)
                .get().addOnSuccessListener {
                    if (it.get("nama_makanan") == null){
                        // To Detail Activity
                        val intent = Intent(
                            this@SearchMakananActivity,
                            DetailMakananActivity::class.java
                        )
                        intent.putExtra("nama_makanan", food.nama_makanan)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this, "Makanan sudah diinput", Toast.LENGTH_LONG).show()
                        binding.svMakanan.requestFocus()
                    }
                }
        }
    }

    private fun searchInFirestore(searchText: String) {

        if (searchText.equals("")){
            db.collection("food")
                .get().addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        searchList = it.result!!.toObjects(SearchModel::class.java)
                        searchListAdapter.searchList = searchList
                        searchListAdapter.notifyDataSetChanged()
                    }
                }
        } else {
            db.collection("food")
                .whereArrayContains("kata_kunci", searchText)
                .get().addOnCompleteListener() {
                    if (it.isSuccessful) {
                        searchList = it.result!!.toObjects(SearchModel::class.java)
                        searchListAdapter.searchList = searchList
                        searchListAdapter.notifyDataSetChanged()
                    }
                }
        }
    }

}