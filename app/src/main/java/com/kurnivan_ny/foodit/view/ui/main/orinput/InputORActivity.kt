package com.kurnivan_ny.foodit.view.ui.main.orinput

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.data.api.RetrofitInstance
import com.kurnivan_ny.foodit.data.model.modelui.manualinput.ListManualModel
import com.kurnivan_ny.foodit.databinding.ActivityInputOdBinding
import com.kurnivan_ny.foodit.view.adapter.manualinput.ListManualAdapter
import com.kurnivan_ny.foodit.view.adapter.history.OnItemClickListener
import com.kurnivan_ny.foodit.view.ui.main.activity.HomeActivity
import com.kurnivan_ny.foodit.view.ui.main.manualinput.EditDetailMakananActivity
import com.kurnivan_ny.foodit.viewmodel.InputORViewModel
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputORActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputOdBinding

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var storage: FirebaseStorage
    lateinit var db: FirebaseFirestore

    private var manualList: ArrayList<ListManualModel> = arrayListOf()
    private var manualListAdapter: ListManualAdapter = ListManualAdapter(manualList)

    private lateinit var filePath: Uri

    private lateinit var viewModel: InputORViewModel

    override fun onStart() {
        super.onStart()

        progressBar(true)

        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                progressBar(false)
            }
        }, 2000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputOdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = SharedPreferences(this)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.tvTitle.text = capitalize(sharedPreferences.getValuesString("waktu_makan"))

        viewModel = ViewModelProvider(this).get(InputORViewModel::class.java)

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
            val intent = Intent(this@InputORActivity, EditORActivity::class.java)
            startActivity(intent)
        }

        binding.ivUlang.setOnClickListener {
            getPicture()
        }

        binding.rvHasil.setHasFixedSize(true)
        binding.rvHasil.layoutManager = LinearLayoutManager(this)
        getDataFirestore()

        viewModel.listmakanan.observe(this, Observer {
            manualListAdapter.manualList = it
            manualListAdapter.notifyDataSetChanged()
        })

        binding.rvHasil.adapter = manualListAdapter

        binding.rvHasil.itemAnimator = SlideInUpAnimator()

        manualListAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int, nama_makanan: String) {
                val intent = Intent(this@InputORActivity, EditDetailMakananActivity::class.java)
                intent.putExtra("nama_makanan", nama_makanan)
                startActivity(intent)
                manualListAdapter.notifyDataSetChanged()
            }
        })

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

            val intent = Intent(this@InputORActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun getPicture() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .createIntent {  intent ->
                progressBar1(true)
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
        val BulanMakan = intent.getStringExtra("bulan_makan").toString()
        val TanggalMakan = intent.getStringExtra("tanggal_makan").toString()
        val waktu_makan = intent.getStringExtra("waktu_makan").toString()

        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.show()

        progressBar1(false)

        storage.reference.child("image_makanan/$imageFile")
            .putFile(filePath)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()

                Toast.makeText(this,"Berhasil", Toast.LENGTH_LONG).show()

                progressBar1(true)

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

                        val intent = Intent(this@InputORActivity, InputOR2Activity::class.java)
                        intent.putExtra("imageFile", dataToBeSendToAPI["image_url"])
                        intent.putExtra("useruid", dataToBeSendToAPI["useruid"])
                        intent.putExtra("tanggal_makan", dataToBeSendToAPI["tanggal_makan"])
                        intent.putExtra("bulan_makan", dataToBeSendToAPI["bulan_makan"])
                        intent.putExtra("waktu_makan", dataToBeSendToAPI["waktu_makan"])
                        startActivity(intent)
//                        Toast.makeText(activity,"Berhasil Memprediksi Makanan", Toast.LENGTH_LONG)
//                            .show()
                    }
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
                    viewModel.listmakanan.value = doc
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

    private fun progressBar1(isLoading: Boolean) = with(binding){
        if (isLoading) {
            this.progressBar1.visibility = View.VISIBLE
        } else {
            this.progressBar1.visibility = View.GONE
        }
    }

    private fun capitalize(str: String?): CharSequence? {
        return str?.trim()?.split("\\s+".toRegex())
            ?.map { it.capitalize() }?.joinToString(" ")
    }

}