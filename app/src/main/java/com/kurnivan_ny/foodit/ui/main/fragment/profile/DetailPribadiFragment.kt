package com.kurnivan_ny.foodit.ui.main.fragment.profile

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.data.modelfirestore.User
import com.kurnivan_ny.foodit.databinding.FragmentDetailPribadiBinding
import com.kurnivan_ny.foodit.viewmodel.preferences.SharedPreferences
import java.nio.FloatBuffer
import java.util.*

class DetailPribadiFragment : Fragment() {

    //BINDING
    private var _binding: FragmentDetailPribadiBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage

    private lateinit var sUsername:String
    private lateinit var sNama:String
    private lateinit var sJenisKelamin:String

    private lateinit var sUmur:String
    private lateinit var sTinggi:String
    private lateinit var sBerat:String

    val imagefile = UUID.randomUUID().toString()

    lateinit var filePath: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailPribadiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = SharedPreferences(requireContext())

        storage = FirebaseStorage.getInstance()

        db = FirebaseFirestore.getInstance()

        setUpFrom()

        edtFotoProfile()

        itemOnClickListener()
    }

    private fun edtFotoProfile() {
        binding.ivProfile.setOnClickListener {
//            ImagePicker.with(this)
//                .cameraOnly()
//                .start()
            startGallery()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            val selectedImg: Uri = result.data?.data as Uri
            filePath = selectedImg

            Glide.with(this)
                .load(filePath)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)

        }
    }

    private fun itemOnClickListener() {
        binding.ivBack.setOnClickListener {
            val toProfileFragment =
                DetailPribadiFragmentDirections.actionDetailPribadiFragmentToProfileFragment()
            binding.root.findNavController().navigate(toProfileFragment)
        }

        binding.btnSimpan.setOnClickListener {
            edtForm()
            if (sNama.equals("")) {
                binding.edtNama.error = "Silakan isi Nama Lengkap"
                binding.edtNama.requestFocus()
            } else if (sJenisKelamin.equals("Masukan Jenis Kelamin")) {
                binding.edtJenisKelamin.error = "Silakan pilih Jenis Kelamin"
                binding.edtJenisKelamin.requestFocus()
            } else if (sUmur.equals("")) {
                binding.edtUmur.error = "Silakan isi Umur"
                binding.edtUmur.requestFocus()
            } else if (sTinggi.equals("")) {
                binding.edtTinggi.error = "Silakan isi TInggi Badan (cm)"
                binding.edtTinggi.requestFocus()
            } else if (sBerat.equals("")) {
                binding.edtBeratBadan.error = "Silakan isi Berat Badan (kg)"
                binding.edtBeratBadan.requestFocus()
            } else {
//                uploadImage()
                if (::filePath.isInitialized) {
                    addImagetoCloudStorage(filePath, imagefile, sUsername)
                }
                val TotalEnergi:Float = predictRegressi(sJenisKelamin, sUmur, sTinggi, sBerat)
                saveUser(sNama, sJenisKelamin, sUmur, sTinggi, sBerat, sUsername, TotalEnergi)
                val toProfileFragment =
                    DetailPribadiFragmentDirections.actionDetailPribadiFragmentToProfileFragment()
                binding.root.findNavController().navigate(toProfileFragment)
            }
        }
    }


    private fun addImagetoCloudStorage(filePath: Uri, imagefile:String, sUsername: String) {
        storage.reference.child("image_profile/$imagefile")
            .putFile(filePath)

        db.collection("users").document(sUsername)
            .get().addOnSuccessListener {
                val urls = it.get("url").toString()
                if (urls.equals("default.png")){
                    db.collection("users").document(sUsername)
                        .update(
                            "url", imagefile,
                        )
                } else {
                    storage.reference.child("image_profile/$urls")
                        .delete()
                    db.collection("users").document(sUsername)
                        .update(
                            "url", imagefile,
                        )
                }
            }

//        db.collection("users").document(sUsername!!)
//            .update(
//                "url", imagefile,
//            )

        sharedPreferences.setValuesString("url", imagefile)
    }
//                .addOnSuccessListener {
//                    Toast.makeText(activity, "Uploaded", Toast.LENGTH_LONG).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(activity, "Failed" + e.message, Toast.LENGTH_LONG).show()
//                }


//    private fun uploadImage() {
//        if (filePath != null){
//            val ref = storageReference.child("image_profile/" + imagefile)
//            ref.putFile(filePath)
//                .addOnSuccessListener {
//                    Toast.makeText(activity, "Uploaded", Toast.LENGTH_LONG).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(activity, "Failed" + e.message, Toast.LENGTH_LONG).show()
//                }
////                .addOnProgressListener { taskSnapshot ->
////                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
////
////                }
//        }
//    }

//    private fun saveUrl(user: User) {
//        if (filePath != null){
//            val progressDialog = ProgressDialog(activity)
//            progressDialog.setTitle("Uploading...")
//            progressDialog.show()
//
//            val ref = storageReference.child("images_profile/" + UUID.randomUUID().toString())
//            ref.putFile(filePath)
//                .addOnSuccessListener {
//                    progressDialog.dismiss()
//                    Toast.makeText(activity,"Uploaded", Toast.LENGTH_LONG).show()
//                    ref.downloadUrl.addOnSuccessListener {
//                        saveToFirebase(it.toString(), user)
//                    }
//                }
//                .addOnFailureListener { e ->
//                    progressDialog.dismiss()
//                    Toast.makeText(activity, "Failed " + e.message, Toast.LENGTH_SHORT).show()
//                }
//                .addOnProgressListener { taskSnapshot ->
//                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
//                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
//                }
//        }
//    }

    private fun setUpFrom() {
        val jeniskelamin = resources.getStringArray(R.array.jenis_kelamin)
        val arrayAdapterJenisKelamin = ArrayAdapter(requireContext(),
            R.layout.dropdown_item, jeniskelamin)

        val sUrl = sharedPreferences.getValuesString("url").toString()

        storage.reference.child("image_profile/$sUrl").downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(this)
                .load(Uri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)
        }

        binding.edtNama.setText(sharedPreferences.getValuesString("nama"))
        binding.edtJenisKelamin.setText(sharedPreferences.getValuesString("jenis_kelamin"))
        binding.edtJenisKelamin.setAdapter(arrayAdapterJenisKelamin)

        binding.edtUmur.setText(sharedPreferences.getValuesInt("umur").toString())
        binding.edtTinggi.setText(sharedPreferences.getValuesInt("tinggi").toString())
        binding.edtBeratBadan.setText(sharedPreferences.getValuesInt("berat").toString())
    }

    private fun edtForm() {
        sUsername = sharedPreferences.getValuesString("username").toString()

        sNama = binding.edtNama.text.toString()
        sJenisKelamin = binding.edtJenisKelamin.text.toString()
        sUmur = binding.edtUmur.text.toString()
        sTinggi = binding.edtTinggi.text.toString()
        sBerat = binding.edtBeratBadan.text.toString()
    }

    private fun saveUser(
        sNama: String, sJenisKelamin: String,
        sUmur: String, sTinggi: String,
        sBerat: String,
        sUsername: String, TotalEnergi:Float) {

        val user = User()

        user.username = sUsername

        user.nama = sNama
        user.jenis_kelamin = sJenisKelamin
        user.umur = sUmur.toInt()
        user.tinggi = sTinggi.toInt()
        user.berat = sBerat.toInt()


        user.totalenergikal = TotalEnergi

        saveToFirestore(user)
    }

    private fun saveToFirestore(data: User) {
        db.collection("users").document(data.username!!)
            .update(
                "nama", data.nama.toString(),
                "jenis_kelamin", data.jenis_kelamin.toString(),

                "umur", data.umur.toString().toInt(),
                "tinggi", data.tinggi.toString().toInt(),
                "berat", data.berat.toString().toInt(),

                "totalenergikal", (data.totalenergikal.toString()+"F").toFloat()
            )
        sharedPreferences.setValuesString("nama", data.nama.toString())
        sharedPreferences.setValuesString("jenis_kelamin", data.jenis_kelamin.toString())

        sharedPreferences.setValuesInt("umur", data.umur.toString().toInt())
        sharedPreferences.setValuesInt("tinggi", data.tinggi.toString().toInt())
        sharedPreferences.setValuesInt("berat", data.berat.toString().toInt())

        sharedPreferences.setValuesFloat("totalenergikal", (data.totalenergikal
            .toString().replace(",",".") +"F").toFloat())

    }

    private fun createORTSession(ortEnvironment: OrtEnvironment): OrtSession {

        val modelBytes = resources.openRawResource(R.raw.regressi_model).readBytes()
        return ortEnvironment.createSession(modelBytes)
    }

    private fun runPrediction(
        floatBufferInputs: FloatBuffer?,
        ortSession: OrtSession,
        ortEnvironment: OrtEnvironment?
    ): Any {
        val inputName = ortSession.inputNames?.iterator()?.next()
        val inputTensor =
            OnnxTensor.createTensor(ortEnvironment, floatBufferInputs, longArrayOf(1, 5))
        val results = ortSession.run(mapOf(inputName to inputTensor))
        val output = results[0].value as Array<FloatArray>
        return output[0][0]
    }

    private fun predictRegressi(sJenisKelamin: String, sUmur: String, sTinggi: String, sBerat: String): Float {

        val TotalEnergi:Float = if (sJenisKelamin.equals("Laki-laki")){
            val floatBufferInputs = FloatBuffer.wrap(
                floatArrayOf(
                    sUmur.toFloat(),
                    sBerat.toFloat(),
                    sTinggi.toFloat(),
                    1.0f,
                    0.0f
                )
            )
            outputPrediction(floatBufferInputs)
        } else {
            val floatBufferInputs = FloatBuffer.wrap(
                floatArrayOf(
                    sUmur.toFloat(),
                    sBerat.toFloat(),
                    sTinggi.toFloat(),
                    0.0f,
                    1.0f
                )
            )
            outputPrediction(floatBufferInputs)
        }
        return TotalEnergi
    }

    private fun outputPrediction(floatBufferInputs: FloatBuffer?): Float {
        val ortEnvironment = OrtEnvironment.getEnvironment()
        val ortSession = createORTSession(ortEnvironment)
        val output = runPrediction(floatBufferInputs, ortSession, ortEnvironment)

//        val TotalEnergi:Float = (String.format("%.2f", output).replace(",",".") + "F").toFloat()
        val TotalEnergi:Float = (output.toString() + "F").replace(",",".").toFloat()
        return TotalEnergi

    }
}