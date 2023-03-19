package com.kurnivan_ny.foodit.ui.main.odinput

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.databinding.ActivityInputOdBinding
import com.kurnivan_ny.foodit.ui.adapter.ImageHomeAdapter
import com.kurnivan_ny.foodit.ui.main.HomeActivity
import com.kurnivan_ny.foodit.viewmodel.InputODViewModel
import com.kurnivan_ny.foodit.viewmodel.preferences.SharedPreferences

class InputODActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputOdBinding

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var storage: FirebaseStorage
    lateinit var db: FirebaseFirestore

    private lateinit var filePath: Uri

    private lateinit var viewModel: InputODViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputOdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = SharedPreferences(this)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        viewModel = ViewModelProvider(this).get(InputODViewModel::class.java)

        progressBar(true)

        viewModel.imageFileURL.value = intent.getStringExtra("imageFile")

        viewModel.imageFileURL.observe(this, androidx.lifecycle.Observer{
            storage.reference.child("image_makanan/$it").downloadUrl
                .addOnSuccessListener { Uri ->
                    Glide.with(this)
                        .load(Uri)
                        .apply(RequestOptions.centerCropTransform())
                        .into(binding.ivMakanan)
                    progressBar(false)
            }
                .addOnFailureListener { e ->
                    progressBar(false)
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        })

        binding.ivSearch.setOnClickListener {
            val intent = Intent(this@InputODActivity, EditODActivity::class.java)
            startActivity(intent)
        }

        binding.ivUlang.setOnClickListener {
            getPicture()
        }

        binding.btnKirim.setOnClickListener {
            val username = sharedPreferences.getValuesString("username")
            val tanggal_makan = sharedPreferences.getValuesString("tanggal_makan")
            val bulan_makan = sharedPreferences.getValuesString("bulan_makan")

            db.collection("users").document(username!!)
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

            val intent = Intent(this@InputODActivity, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

    }

    private fun getPicture() {
        ImagePicker.with(this)
            .crop()
            .createIntent {  intent ->
                ImageResult.launch(intent)
            }
    }

    private val ImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result->

        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val selectedImg: Uri = result.data?.data as Uri
            filePath = selectedImg

            addImagetoCloudStorage(filePath)

        } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addImagetoCloudStorage(filePath: Uri) {
        val imageFile = intent.getStringExtra("imageFile")

        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        storage.reference.child("image_makanan/$imageFile")
            .putFile(filePath)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()

                val loading = ProgressDialog(this)
                loading.setMessage("Menunggu...")
                loading.show()
                val handler = Handler()
                handler.postDelayed(object: Runnable{
                    override fun run() {
                        loading.dismiss()
                        val intent = Intent(this@InputODActivity, InputODActivity::class.java)
                        intent.putExtra("imageFile", "$imageFile")
                        startActivity(intent)
                    }
                }, 20000)

                Toast.makeText(this,"Berhasil", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Gagal" + e.message, Toast.LENGTH_SHORT).show()
            }.addOnProgressListener {taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressDialog.setMessage("Mengunggah " + progress.toInt() + "%")
            }

//        db.collection("users").document(sUsername)
//            .get().addOnSuccessListener {
//                val urls = it.get("url").toString()
//                if (urls.equals("default.png")) {
//                    db.collection("users").document(sUsername)
//                        .update(
//                            "url", imageFile,
//                        )
//                } else {
//                    storage.reference.child("image_profile/$urls")
//                        .delete()
//                    db.collection("users").document(sUsername)
//                        .update(
//                            "url", imageFile,
//                        )
//                }
//            }
    }

    private fun progressBar(isLoading: Boolean) = with(binding){
        if (isLoading) {
            this.progressBar.visibility = View.VISIBLE
        } else {
            this.progressBar.visibility = View.GONE
        }
    }

}