package com.kurnivan_ny.foodit.view.ui.main.manualinput

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.data.api.RetrofitInstance
import com.kurnivan_ny.foodit.data.model.modelui.manualinput.ListManualModel
import com.kurnivan_ny.foodit.databinding.ActivityManualInputBinding
import com.kurnivan_ny.foodit.view.adapter.manualinput.ListManualAdapter
import com.kurnivan_ny.foodit.view.adapter.history.OnItemClickListener
import com.kurnivan_ny.foodit.view.ui.main.activity.HomeActivity
import com.kurnivan_ny.foodit.viewmodel.ManualViewModel
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences
import com.kurnivan_ny.foodit.view.ui.main.orinput.InputORActivity
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.HashMap

class ManualInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualInputBinding

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore

    private var manualList: ArrayList<ListManualModel> = arrayListOf()
    private var manualListAdapter: ListManualAdapter = ListManualAdapter(manualList)

    private lateinit var filePath: Uri

    private lateinit var viewModel: ManualViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = SharedPreferences(this)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

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

        binding.ivUlang.setOnClickListener {
            getPicture()
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

            val intent = Intent(this@ManualInputActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        manualListAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int, nama_makanan: String) {
                val intent = Intent(this@ManualInputActivity, EditDetailMakananActivity::class.java)
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

    private fun getPicture() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
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
        val imageFile = intent.getStringExtra("imageFile").toString()
        val UserUID = intent.getStringExtra("useruid").toString()
        val BulanMakan = intent.getStringExtra("tanggal_makan").toString()
        val TanggalMakan = intent.getStringExtra("bulan_makan").toString()
        val waktu_makan = intent.getStringExtra("waktu_makan").toString()


        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.show()
        storage.reference.child("image_makanan/$imageFile")
            .putFile(filePath)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()

                Toast.makeText(this,"Berhasil Mengunggah", Toast.LENGTH_LONG).show()

                progressBar(true)

                val dataToBeSendToAPI = hashMapOf(
                    "image_url" to imageFile,
                    "useruid" to UserUID, // CEK
                    "bulan_makan" to BulanMakan,
                    "tanggal_makan" to TanggalMakan,
                    "waktu_makan" to waktu_makan
                )

                observerUpdateRetrievedDatatoDatabase(dataToBeSendToAPI)

            }.addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Gagal" + e.message, Toast.LENGTH_SHORT).show()
            }.addOnProgressListener {taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressDialog.setMessage("Mengunggah " + progress.toInt() + "%")
            }
    }

    private fun observerUpdateRetrievedDatatoDatabase(dataToBeSendToAPI: HashMap<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.API_OBJECT.postORResult(dataToBeSendToAPI)
            if (response.isSuccessful){
                val response_body = response.body()

                if (response_body != null){
                    if (response_body.status == "predict"){

                        val intent = Intent(this@ManualInputActivity, InputORActivity::class.java)
                        intent.putExtra("imageFile", dataToBeSendToAPI["image_url"])
                        intent.putExtra("useruid", dataToBeSendToAPI["useruid"])
                        intent.putExtra("tanggal_makan", dataToBeSendToAPI["tanggal_makan"])
                        intent.putExtra("bulan_makan", dataToBeSendToAPI["bulan_makan"])
                        intent.putExtra("waktu_makan", dataToBeSendToAPI["waktu_makan"])
                        startActivity(intent)

                        withContext(Dispatchers.Main){
                            Toast.makeText(this@ManualInputActivity, "Berhasil Prediksi", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManualInputActivity, "Gagal Prediksi", Toast.LENGTH_LONG)
                        .show()
                    progressBar(false)
                }
            }
        }

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

    private fun progressBar(isLoading: Boolean) = with(binding){
        if (isLoading) {
            this.progressBar.visibility = View.VISIBLE
        } else {
            this.progressBar.visibility = View.GONE
        }
    }

}