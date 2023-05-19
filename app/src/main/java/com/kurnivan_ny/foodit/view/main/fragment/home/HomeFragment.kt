package com.kurnivan_ny.foodit.view.main.fragment.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.*
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.data.api.RetrofitInstance
import com.kurnivan_ny.foodit.view.adapter.ImageHomeAdapter
import com.kurnivan_ny.foodit.data.model.modelui.home.ImageHomeModel
import com.kurnivan_ny.foodit.data.model.modelfirestore.Konsumsi
import com.kurnivan_ny.foodit.databinding.FragmentHomeBinding
import com.kurnivan_ny.foodit.view.main.manualinput.ManualInputActivity
import com.kurnivan_ny.foodit.view.main.odinput.InputODActivity
import com.kurnivan_ny.foodit.viewmodel.HomeViewModel
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    //BINDING
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var storage: FirebaseStorage
    lateinit var db: FirebaseFirestore

    lateinit var calendar: Calendar

    private lateinit var dots: ArrayList<TextView>

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var sTanggalMakan:String
    private lateinit var sBulanMakan: String

    private lateinit var sUsername:String
    private lateinit var sUserUID: String
    private lateinit var sUrl: String

    private lateinit var filePath: Uri

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = SharedPreferences(requireContext())
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        calendar = Calendar.getInstance()
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        sUsername = sharedPreferences.getValuesString("username").toString()
        sUserUID = sharedPreferences.getValuesString("user_uid").toString()

        binding.tvNama.text = "Hallo, "+ sUsername

        sUrl = sharedPreferences.getValuesString("url").toString()
        storage.reference.child("image_profile/$sUrl").downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(this)
                .load(Uri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)
        }

        if (binding.dateEditText.text.toString().equals("") or binding.dateEditText.text.toString().equals("Pilih Tanggal")){

            viewModel.tanggal_makan.value = "Pilih Tanggal"

            val docs = arrayListOf<ImageHomeModel>()
            docs.add(
                ImageHomeModel("","","",
                "","",0.0f,0.0f,
                0.0f, 0.0f)
            )
            docs.add(
                ImageHomeModel("","","",
                "","",0.0f,0.0f,
                0.0f, 0.0f)
            )
            docs.add(
                ImageHomeModel("","","",
                "","",0.0f,0.0f,
                0.0f, 0.0f)
            )
            viewModel.homedata.value = docs
        }

        viewModel.homedata.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            binding.vpImage.adapter = ImageHomeAdapter(it)
            progressBar(false)

        })

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable{
            var index = 0
            override fun run() {
                if (index == 3)
                    index = 0
                Log.e("Runnable","$index")
                binding.vpImage.setCurrentItem(index)
                index++
                handler.postDelayed(this, 8000)
            }
        }

        handler.post(runnable)

        binding.vpImage.offscreenPageLimit = 3
        binding.vpImage.clipToPadding = false
        binding.vpImage.clipChildren = false
        binding.vpImage.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        dots = ArrayList()
        setIndicator()

        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer{ page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        binding.vpImage.setPageTransformer(transformer)

        binding.vpImage.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                selectedDot(position)
                super.onPageSelected(position)
            }
        })

        getTanggal()

        updateUI()

        binding.btnPagi.setOnClickListener {
            if (binding.dateEditText.text.toString().equals("") or binding.dateEditText.text.toString().equals("Pilih Tanggal")){
                Toast.makeText(requireContext(), "Silakan Pilih Tanggal Makan", Toast.LENGTH_LONG).show()
                binding.dateEditText.requestFocus()
            } else {
                sharedPreferences.setValuesString("waktu_makan", "makan pagi")
                searchMakanan()
            }
        }
        binding.btnSiang.setOnClickListener {
            if (binding.dateEditText.text.toString().equals("") or binding.dateEditText.text.toString().equals("Pilih Tanggal")){
                Toast.makeText(requireContext(), "Silakan Pilih Tanggal Makan", Toast.LENGTH_LONG).show()
                binding.dateEditText.requestFocus()
            } else {
                sharedPreferences.setValuesString("waktu_makan", "makan siang")
                searchMakanan()
            }
        }
        binding.btnMalam.setOnClickListener {
            if (binding.dateEditText.text.toString().equals("") or binding.dateEditText.text.toString().equals("Pilih Tanggal")){
                Toast.makeText(requireContext(), "Silakan Pilih Tanggal Makan", Toast.LENGTH_LONG).show()
                binding.dateEditText.requestFocus()
            } else {
                sharedPreferences.setValuesString("waktu_makan", "makan malam")
                searchMakanan()
            }
        }
    }

    private fun setIndicator() {
        for (i in 0 until 3) {
            dots.add(TextView(requireContext()))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                dots[i].text = Html.fromHtml("&#9679")
            }
            dots[i].textSize = 18f
            binding.dotsIndicator.addView(dots[i])
        }
    }

    private fun selectedDot(position: Int) {
        for(i in 0 until 3){
            if (i == position){
                dots[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.green_apple))
            } else {
                dots[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.hint))
            }
        }
    }

    private fun getTanggal() {

        binding.dateEditText.isFocusable = false

        binding.dateEditText.setOnClickListener {

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->

                    val arrayMonth = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni",
                        "Juli", "Agustus", "September", "Oktober", "November", "Desember")

                    sTanggalMakan = if (dayOfMonth<10){
                        "0" + (dayOfMonth.toString() + " " + (arrayMonth[monthOfYear]) + " " + year)
                    } else {
                        (dayOfMonth.toString() + " " + (arrayMonth[monthOfYear]) + " " + year)
                    }

                    sBulanMakan = arrayMonth[monthOfYear]

                    viewModel.tanggal_makan.value = sTanggalMakan

                    val konsumsi = updateKonsumsi(sTanggalMakan, sBulanMakan)
                    checkMakan(sUserUID, konsumsi)

                }, year, month, day
            )
            datePickerDialog.show()

            progressBar(true)
        }
    }

    private fun updateKonsumsi(sTanggalMakan: String, sBulanMakan: String): Konsumsi {
        val konsumsi = Konsumsi()

        konsumsi.tanggal_makan = sTanggalMakan
        konsumsi.bulan_makan = sBulanMakan

        konsumsi.total_konsumsi_karbohidrat = 0.00F
        konsumsi.total_konsumsi_protein = 0.00F
        konsumsi.total_konsumsi_lemak = 0.00F

        konsumsi.status_konsumsi_karbohidrat = "Kurang"
        konsumsi.status_konsumsi_protein = "Kurang"
        konsumsi.status_konsumsi_lemak = "Kurang"
        konsumsi.totalenergikal = sharedPreferences.getValuesFloat("totalenergikal")

        return konsumsi
    }

    private fun checkMakan(sUserUID: String, data: Konsumsi) {
        db.collection("users").document(sUserUID)
            .collection(data.bulan_makan!!).document(data.tanggal_makan!!).get()
            .addOnSuccessListener { document ->
                if (document.get("tanggal_makan") == null) {
                    savetoFirestore(sUserUID, data)
                }
                else {
                    // Get Values From Firestore
                    sharedPreferences.setValuesString("tanggal_makan", document.get("tanggal_makan").toString())
                    sharedPreferences.setValuesString("bulan_makan", document.get("bulan_makan").toString())


                    sharedPreferences.setValuesFloat("total_konsumsi_karbohidrat", (document.get("total_konsumsi_karbohidrat")
                        .toString().replace(",",".")+"F").toFloat())
                    sharedPreferences.setValuesFloat("total_konsumsi_protein", (document.get("total_konsumsi_protein")
                        .toString().replace(",",".")+"F").toFloat())
                    sharedPreferences.setValuesFloat("total_konsumsi_lemak", (document.get("total_konsumsi_lemak")
                        .toString().replace(",",".")+"F").toFloat())

                    sharedPreferences.setValuesString("status_konsumsi_karbohidrat", document.get("status_konsumsi_karbohidrat").toString())
                    sharedPreferences.setValuesString("status_konsumsi_protein", document.get("status_konsumsi_protein").toString())
                    sharedPreferences.setValuesString("status_konsumsi_lemak", document.get("status_konsumsi_lemak").toString())

                    sharedPreferences.setValuesFloat("totalenergikal", (document.get("totalenergikal")
                        .toString().replace(",",".")+"F").toFloat())

                    // viewModel
                    val doc = arrayListOf<ImageHomeModel>()
                    doc.add(document.toObject(ImageHomeModel::class.java)!!)
                    doc.add(document.toObject(ImageHomeModel::class.java)!!)
                    doc.add(document.toObject(ImageHomeModel::class.java)!!)
                    viewModel.homedata.value = doc

                    // viewModel
                    viewModel.total_karbohidrat_konsumsi.value = (document.get("total_konsumsi_karbohidrat")
                        .toString().replace(",",".")+"F").toFloat()
                    viewModel.total_protein_konsumsi.value = (document.get("total_konsumsi_protein")
                        .toString().replace(",",".")+"F").toFloat()
                    viewModel.total_lemak_konsumsi.value = (document.get("total_konsumsi_lemak")
                        .toString().replace(",",".")+"F").toFloat()

                }
            }
    }

    private fun savetoFirestore(sUserUID: String, data: Konsumsi) {
        db.collection("users").document(sUserUID)
            .collection(data.bulan_makan!!).document(data.tanggal_makan!!)
            .set(data)

        sharedPreferences.setValuesString("tanggal_makan", data.tanggal_makan.toString())
        sharedPreferences.setValuesString("bulan_makan", data.bulan_makan.toString())

        sharedPreferences.setValuesFloat("total_konsumsi_karbohidrat", (data.total_konsumsi_karbohidrat
            .toString().replace(",",".")+"F").toFloat())
        sharedPreferences.setValuesFloat("total_konsumsi_protein", (data.total_konsumsi_protein
            .toString().replace(",",".")+"F").toFloat())
        sharedPreferences.setValuesFloat("total_konsumsi_lemak", (data.total_konsumsi_lemak
            .toString().replace(",",".")+"F").toFloat())

        sharedPreferences.setValuesString("status_konsumsi_karbohidrat", data.status_konsumsi_karbohidrat.toString())
        sharedPreferences.setValuesString("status_konsumsi_protein", data.status_konsumsi_protein.toString())
        sharedPreferences.setValuesString("status_konsumsi_lemak", data.status_konsumsi_lemak.toString())

        sharedPreferences.setValuesFloat("total_konsumsi_lemak", (data.totalenergikal
            .toString().replace(",",".")+"F").toFloat())

//      viewmodel

        db.collection("users").document(sUserUID)
            .collection(data.bulan_makan!!).document(data.tanggal_makan!!)
            .get().addOnSuccessListener { document ->
                // viewModel
                val doc = arrayListOf<ImageHomeModel>()
                doc.add(document.toObject(ImageHomeModel::class.java)!!)
                doc.add(document.toObject(ImageHomeModel::class.java)!!)
                doc.add(document.toObject(ImageHomeModel::class.java)!!)
                viewModel.homedata.value = doc
            }

        viewModel.total_karbohidrat_konsumsi.value = (data.total_konsumsi_karbohidrat
            .toString().replace(",",".")+"F").toFloat()
        viewModel.total_protein_konsumsi.value = (data.total_konsumsi_protein
            .toString().replace(",",".")+"F").toFloat()
        viewModel.total_lemak_konsumsi.value = (data.total_konsumsi_lemak
            .toString().replace(",",".")+"F").toFloat()
    }

    private fun updateUI() {

        val totalenergikal:Float = sharedPreferences.getValuesFloat("totalenergikal")

        viewModel.tanggal_makan.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            binding.dateEditText.setText(it)
        })

        karbohidrat(totalenergikal)
        protein(totalenergikal)
        lemak(totalenergikal)
    }

    private fun karbohidrat(totalenergikal: Float) {

        // karbohidrat = energi/4 gram
        val batas_bawah_karbohidrat = totalenergikal/4 *0.55
        val batas_atas_karbohidrat = totalenergikal/4 *0.75

        var status_konsumsi_karbohidrat: String

        viewModel.total_karbohidrat_konsumsi.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it < batas_bawah_karbohidrat) {
                status_konsumsi_karbohidrat = "Kurang"
                updateKarbohidrattoFirestore(status_konsumsi_karbohidrat)
            } else if  ((it > batas_bawah_karbohidrat)&&(it < batas_atas_karbohidrat)){
                status_konsumsi_karbohidrat = "Normal"
                updateKarbohidrattoFirestore(status_konsumsi_karbohidrat)
            } else if (it > batas_atas_karbohidrat) {
                status_konsumsi_karbohidrat = "Lebih"
                updateKarbohidrattoFirestore(status_konsumsi_karbohidrat)
//                alertLebihKarbohidrat()
            }
        })
    }

    private fun alertLebihKarbohidrat() {
        val view = View.inflate(requireContext(), R.layout.alert_over_dialog, null)
        val tv_desc = view.findViewById<TextView>(R.id.tv_desc)

        tv_desc.setText("Konsumsi gizi karbohidrat\ntelah melebihi batas!")

        AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
            .setView(view)
            .show()

    }

    private fun updateKarbohidrattoFirestore(status_konsumsi_karbohidrat: String) {
        db.collection("users").document(sUserUID)
            .collection(sharedPreferences.getValuesString("bulan_makan")!!)
            .document(sharedPreferences.getValuesString("tanggal_makan")!!)
            .update(
                "status_konsumsi_karbohidrat", status_konsumsi_karbohidrat,
            )

        sharedPreferences.setValuesString("status_konsumsi_karbohidrat", status_konsumsi_karbohidrat)
    }


    private fun protein(totalenergikal: Float) {

        // protein = energi/4 gram
        val batas_bawah_protein = totalenergikal/4 *0.07
        val batas_atas_protein = totalenergikal/4 *0.2

        var status_konsumsi_protein: String

        viewModel.total_protein_konsumsi.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it < batas_bawah_protein){
                status_konsumsi_protein = "Kurang"
                updateProteintoFirestore(status_konsumsi_protein)
            } else if  ((it > batas_bawah_protein)&&(it < batas_atas_protein)){
                status_konsumsi_protein = "Normal"
                updateProteintoFirestore(status_konsumsi_protein)
            } else if (it > batas_atas_protein) {
                status_konsumsi_protein = "Lebih"
                updateProteintoFirestore(status_konsumsi_protein)
//                alertLebihProtein()
            }
        })
    }

    private fun alertLebihProtein() {
        val view = View.inflate(requireContext(), R.layout.alert_over_dialog, null)
        val tv_desc = view.findViewById<TextView>(R.id.tv_desc)

        tv_desc.setText("Konsumsi gizi protein\ntelah melebihi batas!")

        AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
            .setView(view)
            .show()
    }

    private fun updateProteintoFirestore(status_konsumsi_protein: String) {
        db.collection("users").document(sUserUID)
            .collection(sharedPreferences.getValuesString("bulan_makan")!!)
            .document(sharedPreferences.getValuesString("tanggal_makan")!!)
            .update(
                "status_konsumsi_protein", status_konsumsi_protein
            )

        sharedPreferences.setValuesString("status_konsumsi_protein", status_konsumsi_protein)
    }

    private fun lemak(totalenergikal: Float) {

        // lemak = energi/9 gram
        val batas_bawah_lemak = totalenergikal/9 *0.15
        val batas_atas_lemak = totalenergikal/9 *0.25

        var status_konsumsi_lemak: String

        viewModel.total_lemak_konsumsi.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it < batas_bawah_lemak){
                status_konsumsi_lemak = "Kurang"
                updateLemaktoFirestore(status_konsumsi_lemak)
            } else if  ((it > batas_bawah_lemak)&&(it < batas_atas_lemak)){
                status_konsumsi_lemak = "Normal"
                updateLemaktoFirestore(status_konsumsi_lemak)
            } else if (it > batas_atas_lemak) {
                status_konsumsi_lemak = "Lebih"
                updateLemaktoFirestore(status_konsumsi_lemak)
//                alertLebihLemak()
            }
        })
    }

    private fun alertLebihLemak() {
        val view = View.inflate(requireContext(), R.layout.alert_over_dialog, null)
        val tv_desc = view.findViewById<TextView>(R.id.tv_desc)

        tv_desc.setText("Konsumsi gizi lemak\ntelah melebihi batas!")

        AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
            .setView(view)
            .show()
    }

    private fun updateLemaktoFirestore(status_konsumsi_lemak: String) {
        db.collection("users").document(sUserUID)
            .collection(sharedPreferences.getValuesString("bulan_makan")!!)
            .document(sharedPreferences.getValuesString("tanggal_makan")!!)
            .update(
                "status_konsumsi_lemak", status_konsumsi_lemak
            )

        sharedPreferences.setValuesString("status_konsumsi_lemak", status_konsumsi_lemak)

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
                Toast.makeText(activity, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Gagal", Toast.LENGTH_SHORT).show()
            }
        }

    private fun addImagetoCloudStorage(filePath: Uri) {
        val waktu_makan = sharedPreferences.getValuesString("waktu_makan").toString()
        val UserUID = sharedPreferences.getValuesString("user_uid").toString()
        val imageFile = "$UserUID-$sTanggalMakan-$waktu_makan.jpeg"

        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false)
        progressDialog.show()

        storage.reference.child("image_makanan/$imageFile")
            .putFile(filePath)
            .addOnSuccessListener {
                progressDialog.dismiss()

                Toast.makeText(activity,"Berhasil", Toast.LENGTH_LONG).show()

                progressBar(true)

                val dataToBeSendToAPI = hashMapOf(
                    "image_url" to imageFile,
                    "useruid" to sUserUID, 
                    "bulan_makan" to sBulanMakan,
                    "tanggal_makan" to sTanggalMakan,
                    "waktu_makan" to waktu_makan
                )

                observerUpdateRetrievedDatatoDatabase(dataToBeSendToAPI)


            }.addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(activity, "Gagal", Toast.LENGTH_SHORT).show()
            }.addOnProgressListener {taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressDialog.setMessage("Mengunggah " + progress.toInt() + "%")
            }

    }

    private fun observerUpdateRetrievedDatatoDatabase(dataToBeSendToAPI: HashMap<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.API_OBJECT.postODResult(dataToBeSendToAPI)
            if (response.isSuccessful){
                val response_body = response.body()

                if (response_body != null){
                    if (response_body.status == "predict"){

                        val intent = Intent(activity, InputODActivity::class.java)
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

    private fun searchMakanan() {

        val view =View.inflate(requireContext(), R.layout.home_food_dialog, null)

        val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
            .setView(view)
            .setNegativeButton("TIDAK, MENGISI SECARA MANUAL"){_,_ ->
                val intent = Intent(activity, ManualInputActivity::class.java)
                startActivity(intent)
            }
            .setPositiveButton("Ya"){_,_ ->
                getPicture()
            }

        val dialog = builder.create()

        dialog.setOnShowListener {
            val button_negative = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            button_negative.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_apple
                ))

            val button_positive = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button_positive.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_apple
                ))
        }

        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }

    private fun progressBar(isLoading: Boolean) = with(binding){
        if (isLoading) {
            this.progressBar.visibility = View.VISIBLE
        } else {
            this.progressBar.visibility = View.GONE
        }
    }

}