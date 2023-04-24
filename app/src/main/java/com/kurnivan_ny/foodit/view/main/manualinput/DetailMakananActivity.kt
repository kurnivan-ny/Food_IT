package com.kurnivan_ny.foodit.view.main.manualinput

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.data.model.modelfirestore.Makan
import com.kurnivan_ny.foodit.databinding.ActivityDetailMakananBinding
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences

class DetailMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMakananBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore

    private lateinit var satuan: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar(true)

        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                progressBar(false)
            }
        }, 2000)

        binding.tvTitle.setText("")

        sharedPreferences = SharedPreferences(this)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

//        val loading = ProgressDialog(this)
//        loading.setMessage("Menunggu...")
//        loading.setCancelable(false)
//        loading.show()
//        val handler = Handler()
//        handler.postDelayed(object: Runnable{
//            override fun run() {
//                loading.dismiss()
//            }
//        }, 4000)

        val nama_makanan = intent.getStringExtra("nama_makanan").toString()

        binding.ivBack.setOnClickListener {
            val intent = Intent(this@DetailMakananActivity, SearchMakananActivity::class.java)
            startActivity(intent)
        }

        getDataFirestore(nama_makanan)
    }

    private fun getDataFirestore(namaMakanan: String) {
        db.collection("food").document(namaMakanan).get().addOnSuccessListener { document ->

            val nama_makanan = document.get("nama_makanan").toString()

            // setting image
            val img_url = document.get("url_foto").toString()

            val beratPorsi = (document.get("berat_porsi_gr").toString() + "F").toFloat()

            val karbohidrat = (document.get("karbohidrat_100gr").toString() + "F").toFloat()
            val protein = (document.get("protein_100gr").toString() + "F").toFloat()
            val lemak = (document.get("lemak_100gr").toString() + "F").toFloat()

            setUpForm(nama_makanan, img_url, beratPorsi, karbohidrat, protein, lemak)
        }
    }

    private fun setUpForm(namaMakanan: String, imgUrl: String, beratPorsi: Float, karbohidrat: Float, protein: Float, lemak: Float) {
        binding.tvTitle.text = namaMakanan
        storage.reference.child("$imgUrl").downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(this)
                .load(Uri)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.ivMakanan)
        }

        val satuanMakanan = resources.getStringArray(R.array.satuan)
        val arrayAdapterSatuan = ArrayAdapter(this, R.layout.dropdown_item, satuanMakanan)
        binding.edtSatuanMakanan.setText("porsi")
        binding.edtSatuanMakanan.setAdapter(arrayAdapterSatuan)

        binding.btnTambah.isEnabled = false
        binding.btnTambah.visibility = View.INVISIBLE

        binding.edtBeratMakanan.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                satuan = binding.edtSatuanMakanan.text.toString()

                val berat_makanan = binding.edtBeratMakanan.text.toString()

                if (berat_makanan.equals("") or berat_makanan.equals("0")){

                    binding.tvKarbohidrat.text = "Karbohidrat:\t 0.00 gr"
                    binding.tvProtein.text = "Protein:\t 0.00 gr"
                    binding.tvLemak.text = "Lemak:\t 0.00 gr"

                    binding.btnTambah.isEnabled = false
                    binding.btnTambah.visibility = View.INVISIBLE
                } else {
                    val beratMakanan = berat_makanan.toInt()
                    val arrayTotal = hitungKebutuhan(beratMakanan, beratPorsi, karbohidrat, protein, lemak)

                    binding.btnTambah.isEnabled = true
                    binding.btnTambah.visibility = View.VISIBLE

                    binding.btnTambah.setOnClickListener {

                        saveMakanantoFirestore(namaMakanan, satuan, beratMakanan, arrayTotal)

                        val intent = Intent(this@DetailMakananActivity, ManualInputActivity::class.java)
                        startActivity(intent)
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun saveMakanantoFirestore(namaMakanan: String, satuan: String, beratMakanan: Int, arrayTotal: FloatArray) {
        val username = sharedPreferences.getValuesString("username")
        val tanggal_makan = sharedPreferences.getValuesString("tanggal_makan")
        val waktu_makan = sharedPreferences.getValuesString("waktu_makan")
        val bulan_makan = sharedPreferences.getValuesString("bulan_makan")

        val makan = Makan()
        makan.waktu_makan = waktu_makan

        makan.nama_makanan = namaMakanan
        makan.satuan_makanan = satuan
        makan. berat_makanan = beratMakanan

        makan.karbohidrat = arrayTotal[0]
        makan.protein = arrayTotal[1]
        makan.lemak = arrayTotal[2]

        db.collection("users").document(username!!)
            .collection(bulan_makan!!).document(tanggal_makan!!)
            .collection(waktu_makan!!).document(namaMakanan).set(makan)

        // update value username

        db.collection("users").document(username)
            .collection(bulan_makan).document(tanggal_makan)
            .get().addOnSuccessListener {
                var total_karbohidrat:Float = (it.get("total_konsumsi_karbohidrat").toString()+"F").toFloat()
                var total_protein:Float = (it.get("total_konsumsi_protein").toString()+"F").toFloat()
                var total_lemak:Float = (it.get("total_konsumsi_lemak").toString()+"F").toFloat()

                total_karbohidrat += arrayTotal[0]
                total_protein += arrayTotal[1]
                total_lemak += arrayTotal[2]

                db.collection("users").document(username)
                    .collection(bulan_makan).document(tanggal_makan)
                    .update("total_konsumsi_karbohidrat",total_karbohidrat,
                        "total_konsumsi_protein", total_protein,
                        "total_konsumsi_lemak", total_lemak)
            }

//        var total_karbohidrat = sharedPreferences.getValuesFloat("total_konsumsi_karbohidrat")
//        var total_protein = sharedPreferences.getValuesFloat("total_konsumsi_protein")
//        var total_lemak = sharedPreferences.getValuesFloat("total_konsumsi_lemak")
//
//        total_karbohidrat += arrayTotal[0]
//        total_protein += arrayTotal[1]
//        total_lemak += arrayTotal[2]
//
//        db.collection("users").document(username!!)
//            .collection("makan").document(tanggal_makan!!)
//            .update("total_konsumsi_karbohidrat",total_karbohidrat,
//            "total_konsumsi_protein", total_protein,
//            "total_konsumsi_lemak", total_lemak)
//
//        sharedPreferences.setValuesFloat("total_konsumsi_karbohidrat", total_karbohidrat)
//        sharedPreferences.setValuesFloat("total_konsumsi_protein", total_protein)
//        sharedPreferences.setValuesFloat("total_konsumsi_lemak",total_lemak)

    }

    private fun hitungKebutuhan(beratMakanan: Int, beratPorsi: Float, karbohidrat: Float, protein: Float, lemak: Float): FloatArray {

        var total_karbohidrat:Float? = null
        var total_protein:Float? = null
        var total_lemak: Float? = null

        // berat karbohidrat, protein, dan lemak dalam 100 gram

        if (satuan.equals("porsi")){
//            val berat_porsi_gr = 100 // edit

            total_karbohidrat = karbohidrat/100 * beratPorsi * beratMakanan //porsi
            total_protein = protein/100 * beratPorsi * beratMakanan
            total_lemak = lemak/100 * beratPorsi * beratMakanan

            binding.tvKarbohidrat.text = "Karbohidrat:\t ${String.format("%.2f",total_karbohidrat)} gr"
            binding.tvProtein.text = "Protein:\t ${String.format("%.2f",total_protein)} gr"
            binding.tvLemak.text = "Lemak:\t ${String.format("%.2f",total_lemak)} gr"

            binding.tvKeteranganPorsi.text = "* 1 porsi = ${String.format("%.2f",beratPorsi)} gram"
        }
        else if (satuan.equals("gram")){

            total_karbohidrat = karbohidrat/100 * beratMakanan //gram
            total_protein = protein/100 * beratMakanan
            total_lemak = lemak/100 * beratMakanan

            binding.tvKarbohidrat.text = "Karbohidrat:\t ${String.format("%.2f",total_karbohidrat)} gr"
            binding.tvProtein.text = "Protein:\t ${String.format("%.2f",total_protein)} gr"
            binding.tvLemak.text = "Lemak:\t ${String.format("%.2f",total_lemak)} gr"

            binding.tvKeteranganPorsi.text = "* 1 porsi = ${String.format("%.2f",beratPorsi)} gram"
        }

        val arrayTotal = floatArrayOf(total_karbohidrat!!,total_protein!!,total_lemak!!)

        return arrayTotal
    }

    private fun progressBar(isLoading: Boolean) = with(binding){
        if (isLoading) {
            this.progressBar.visibility = View.VISIBLE
        } else {
            this.progressBar.visibility = View.GONE
        }
    }
}