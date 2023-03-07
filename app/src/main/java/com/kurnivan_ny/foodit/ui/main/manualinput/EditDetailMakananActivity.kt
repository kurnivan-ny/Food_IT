package com.kurnivan_ny.foodit.ui.main.manualinput

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.data.modelfirestore.Makan
import com.kurnivan_ny.foodit.databinding.ActivityDetailMakananBinding
import com.kurnivan_ny.foodit.databinding.ActivityEditDetailMakananBinding
import com.kurnivan_ny.foodit.viewmodel.preferences.SharedPreferences

class EditDetailMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditDetailMakananBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore

    private lateinit var satuan: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDetailMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = SharedPreferences(this)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val nama_makanan = intent.getStringExtra("nama_makanan").toString()

        updateDataFirestore(nama_makanan)

        binding.ivBack.setOnClickListener {
            val intent = Intent(this@EditDetailMakananActivity, ManualInputActivity::class.java)
            startActivity(intent)
        }

//        binding.ivDelete.setOnClickListener{
//            deleteFirestore(nama_makanan)
//            val intent = Intent(this@EditDetailMakananActivity, ManualInputActivity::class.java)
//            startActivity(intent)
//        }

    }

//    private fun deleteFirestore(namaMakanan: String) {
//        val username = sharedPreferences.getValuesString("username")
//        val tanggal_makan = sharedPreferences.getValuesString("tanggal_makan")
//        val waktu_makan = sharedPreferences.getValuesString("waktu_makan")
//
//        db.collection("users").document(username!!)
//            .collection("makan").document(tanggal_makan!!)
//            .collection(waktu_makan!!).document(namaMakanan)
//            .get().addOnSuccessListener { iFood ->
//
//                db.collection("users").document(username!!)
//                    .collection("makan").document(tanggal_makan!!)
//                    .collection(waktu_makan!!).document(namaMakanan)
//                    .delete().addOnSuccessListener {
//
//                        db.collection("users").document(username!!)
//                            .collection("makan").document(tanggal_makan!!)
//                            .get().addOnSuccessListener { Food ->
//                                var total_konsumsi_karbohidrat:Float = (Food.get("total_konsumsi_karbohidrat").toString()+"F").toFloat()
//                                total_konsumsi_karbohidrat = total_konsumsi_karbohidrat - (iFood.get("karbohidrat").toString()+"F").toFloat()
//
//                                var total_konsumsi_protein:Float = (Food.get("total_konsumsi_protein").toString()+"F").toFloat()
//                                total_konsumsi_protein = total_konsumsi_protein - (iFood.get("protein").toString()+"F").toFloat()
//
//                                var total_konsumsi_lemak:Float = (Food.get("total_konsumsi_lemak").toString()+"F").toFloat()
//                                total_konsumsi_lemak = total_konsumsi_lemak - (iFood.get("lemak").toString()+"F").toFloat()
//
//                                db.collection("users").document(username)
//                                    .collection("makan").document(tanggal_makan)
//                                    .update("total_konsumsi_karbohidrat",total_konsumsi_karbohidrat,
//                                        "total_konsumsi_protein", total_konsumsi_protein,
//                                        "total_konsumsi_lemak", total_konsumsi_lemak)
//
//                        }
//                }
//        }
//    }

    private fun updateDataFirestore(namaMakanan: String) {
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

    private fun setUpForm(namaMakanan: String, imgUrl: String, beratPorsi:Float, karbohidrat: Float, protein: Float, lemak: Float) {
        val username = sharedPreferences.getValuesString("username")
        val tanggal_makan = sharedPreferences.getValuesString("tanggal_makan")
        val waktu_makan = sharedPreferences.getValuesString("waktu_makan")
        val bulan_makan = sharedPreferences.getValuesString("bulan_makan")

        binding.tvTitle.text = namaMakanan

        storage.reference.child("image_profile/$imgUrl").downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(this)
                .load(Uri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivMakanan)
        }

        db.collection("users").document(username!!)
            .collection(bulan_makan!!).document(tanggal_makan!!)
            .collection(waktu_makan!!).document(namaMakanan)
            .get().addOnSuccessListener {
                val beratMakanan = it.get("berat_makanan").toString()
                binding.edtBeratMakanan.setText(beratMakanan)

                val karbohidrat_new = (it.get("karbohidrat").toString()+"F").toFloat()
                val protein_new = (it.get("protein").toString()+"F").toFloat()
                val lemak_new = ((it.get("lemak").toString()+"F").toFloat())

                binding.tvKarbohidrat.text = "Karbohidrat:\t ${String.format("%.2f",karbohidrat_new)} gr"
                binding.tvProtein.text = "Protein:\t\t\t\t\t\t ${String.format("%.2f",protein_new)} gr"
                binding.tvLemak.text = "Lemak:\t\t\t\t\t\t\t ${String.format("%.2f",lemak_new)} gr"

                binding.btnTambah.isEnabled = false
                binding.btnTambah.visibility = View.INVISIBLE

                binding.edtBeratMakanan.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        satuan = binding.edtSatuanMakanan.text.toString()

                        val berat_makanan = binding.edtBeratMakanan.text.toString()

                        if (berat_makanan.equals("") or berat_makanan.equals("0")){

                            binding.tvKarbohidrat.text = "Karbohidrat:\t 0.00 gr"
                            binding.tvProtein.text = "Protein:\t\t\t\t\t\t 0.00 gr"
                            binding.tvLemak.text = "Lemak:\t\t\t\t\t\t\t 0.00 gr"

                            binding.btnTambah.isEnabled = false
                            binding.btnTambah.visibility = View.INVISIBLE
                        } else {

                            val beratMakanan = berat_makanan.toInt()
                            val arrayTotal = hitungKebutuhan(beratMakanan, beratPorsi, karbohidrat, protein, lemak)

                            binding.btnTambah.isEnabled = true
                            binding.btnTambah.visibility = View.VISIBLE

                            binding.btnTambah.setOnClickListener {

                                saveMakanantoFirestore(namaMakanan, satuan, beratMakanan, arrayTotal)

                                val intent = Intent(this@EditDetailMakananActivity, ManualInputActivity::class.java)
                                startActivity(intent)
                            }
                        }

                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })

                val satuanBeratMakanan = it.get("satuan_makanan").toString()

                val satuanMakanan = resources.getStringArray(R.array.satuan)
                val arrayAdapterSatuan = ArrayAdapter(this, R.layout.dropdown_item, satuanMakanan)
                binding.edtSatuanMakanan.setText(satuanBeratMakanan)
                binding.edtSatuanMakanan.setAdapter(arrayAdapterSatuan)
            }
    }

    private fun saveMakanantoFirestore(namaMakanan: String, satuan: String, beratMakanan: Int, arrayTotal: FloatArray) {

        val username = sharedPreferences.getValuesString("username")
        val tanggal_makan = sharedPreferences.getValuesString("tanggal_makan")
        val waktu_makan = sharedPreferences.getValuesString("waktu_makan")
        val bulan_makan = sharedPreferences.getValuesString("bulan_makan")

//        deleteFirestore(namaMakanan)

        val makan = Makan()
        makan.waktu_makan = waktu_makan

        makan.nama_makanan = namaMakanan
        makan.satuan_makanan = satuan
        makan.berat_makanan = beratMakanan

        makan.karbohidrat = arrayTotal[0]
        makan.protein = arrayTotal[1]
        makan.lemak = arrayTotal[2]

        // update value username

        db.collection("users").document(username!!)
            .collection(bulan_makan!!).document(tanggal_makan!!)
            .collection(waktu_makan!!).document(namaMakanan)
            .get().addOnSuccessListener {
                val karbohidrat: Float = (it.get("karbohidrat").toString() + "F").toFloat()
                val protein: Float = (it.get("protein").toString() + "F").toFloat()
                val lemak: Float = (it.get("lemak").toString() + "F").toFloat()

                db.collection("users").document(username)
                    .collection(bulan_makan).document(tanggal_makan)
                    .get().addOnSuccessListener {
                        var total_karbohidrat: Float =
                            (it.get("total_konsumsi_karbohidrat").toString() + "F").toFloat()
                        var total_protein: Float =
                            (it.get("total_konsumsi_protein").toString() + "F").toFloat()
                        var total_lemak: Float =
                            (it.get("total_konsumsi_lemak").toString() + "F").toFloat()

                        total_karbohidrat = total_karbohidrat + arrayTotal[0] - karbohidrat
                        total_protein = total_protein + arrayTotal[1] - protein
                        total_lemak = total_lemak + arrayTotal[2] - lemak

                        db.collection("users").document(username)
                            .collection(bulan_makan).document(tanggal_makan)
                            .update(
                                "total_konsumsi_karbohidrat", total_karbohidrat,
                                "total_konsumsi_protein", total_protein,
                                "total_konsumsi_lemak", total_lemak
                            )

                        db.collection("users").document(username)
                            .collection(bulan_makan).document(tanggal_makan)
                            .collection(waktu_makan).document(namaMakanan).set(makan)
                    }
            }
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
//                "total_konsumsi_protein", total_protein,
//                "total_konsumsi_lemak", total_lemak)

//        sharedPreferences.setValuesFloat("total_konsumsi_karbohidrat", total_karbohidrat)
//        sharedPreferences.setValuesFloat("total_konsumsi_protein", total_protein)
//        sharedPreferences.setValuesFloat("total_konsumsi_lemak",total_lemak)


    private fun hitungKebutuhan(beratMakanan: Int, beratPorsi: Float, karbohidrat: Float, protein: Float, lemak: Float): FloatArray {

        var total_karbohidrat:Float? = 0.00F
        var total_protein:Float? = 0.00F
        var total_lemak: Float? = 0.00F


        if (satuan.equals("porsi")){
//            var berat_porsi_gr = 100 // edit

            total_karbohidrat = karbohidrat/100 * beratPorsi * beratMakanan
            total_protein = protein/100 * beratPorsi * beratMakanan
            total_lemak = lemak/100 * beratPorsi * beratMakanan

            binding.tvKarbohidrat.text = "Karbohidrat:\t ${String.format("%.2f",total_karbohidrat)} gr"
            binding.tvProtein.text = "Protein:\t\t\t\t\t\t\t ${String.format("%.2f",total_protein)} gr"
            binding.tvLemak.text = "Lemak:\t\t\t\t\t\t\t ${String.format("%.2f",total_lemak)} gr"

            binding.tvKeteranganPorsi.text = "* 1 porsi = ${String.format("%.2f",beratPorsi)} gram"
        }
        else if (satuan.equals("gram")){

            total_karbohidrat = karbohidrat/100 * beratMakanan
            total_protein = protein/100 * beratMakanan
            total_lemak = lemak/100 * beratMakanan

            binding.tvKarbohidrat.text = "Karbohidrat:\t ${String.format("%.2f",total_karbohidrat)} gr"
            binding.tvProtein.text = "Protein:\t\t\t\t\t\t\t ${String.format("%.2f",total_protein)} gr"
            binding.tvLemak.text = "Lemak:\t\t\t\t\t\t\t ${String.format("%.2f",total_lemak)} gr"

            binding.tvKeteranganPorsi.text = "* 1 porsi = ${String.format("%.2f",beratPorsi)} gram"
        }

        val arrayTotal = floatArrayOf(total_karbohidrat!!,total_protein!!,total_lemak!!)

        return arrayTotal
    }
}