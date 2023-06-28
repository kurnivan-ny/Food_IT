package com.kurnivan_ny.foodit.view.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.data.model.modelfirestore.User
import com.kurnivan_ny.foodit.databinding.ActivityRegisterAkunBinding
import com.kurnivan_ny.foodit.view.ui.main.activity.HomeActivity
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences
import com.kurnivan_ny.foodit.view.utils.konversiInput
import com.kurnivan_ny.foodit.view.utils.predictRegressi
import java.nio.FloatBuffer

class RegisterAkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterAkunBinding

    private lateinit var sNama:String
    private lateinit var sJenisKelamin:String
    private lateinit var sUmur:String
    private lateinit var sTinggi:String
    private lateinit var sBerat:String

    private lateinit var sUsername: String
    private lateinit var sEmail: String
    private lateinit var sPassword: String

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        sharedPreferences = SharedPreferences(this)

        binding.ctvPrivacypolicy.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.policy_privacy_dialog, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(dialogView)
                .setPositiveButton("Ya"){_,_ ->
                    binding.ctvPrivacypolicy.isChecked = true
                }
                .setNegativeButton("Tidak"){p0, _ ->
                    binding.ctvPrivacypolicy.isChecked = false
                    p0.dismiss()
                }

            val alertDialog = dialogBuilder.create()

            alertDialog.setOnShowListener {
                val button_negative = (alertDialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
                button_negative.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.black
                    )
                )

                val button_positive = (alertDialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                button_positive.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.black
                    )
                )
            }

            alertDialog.show()

        }

        binding.btnMasuk.setOnClickListener {
            val intent = Intent(this@RegisterAkunActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnDaftar.setOnClickListener {

            setUpData()

            signUpForm()

            if (sUsername.equals("")){
                binding.edtUsername.error = "Silakan isi Username"
                binding.edtUsername.requestFocus()
            } else if (sEmail.equals("")){
                binding.edtEmail.error = "Silakan Isi Email"
                binding.edtPassword.requestFocus()
            } else if (sPassword.equals("")){
                binding.edtPassword.error = "Silakan Isi Password"
                binding.edtPassword.requestFocus()
            } else if (sPassword.length < 6){
                binding.edtPassword.error = "Password Anda Masih Kurang Dari 6 Karakter"
                binding.edtPassword.requestFocus()
            } else if (binding.ctvPrivacypolicy.isChecked.equals(false)){
                Toast.makeText(this, "Silakan Menyetujui Kebijakan Privasi", Toast.LENGTH_LONG).show()
                binding.ctvPrivacypolicy.requestFocus()
            } else{

                val tanda_baca = arrayOf("&", "=", "_", "\'", "-", "+", ",","<", ">", ".", "/", "\"", "\\")
                val detecttandaList: MutableList<Int> = arrayListOf()
                for (tanda in tanda_baca){
                    val statusUsername = sUsername.indexOf(tanda)
                    if(statusUsername == -1){
                        continue
                    } else {
                        detecttandaList.add(statusUsername)
                        break
                    }
                }

                if (detecttandaList.isEmpty()){

                    val modelBytes = resources.openRawResource(R.raw.regressi_model).readBytes()
                    val input:FloatBuffer = konversiInput(sJenisKelamin, sUmur, sTinggi, sBerat)

                    val TotalEnergi:Float = predictRegressi(modelBytes, input)

                    saveUser(sNama, sJenisKelamin, sUmur, sTinggi, sBerat, sUsername, sEmail, sPassword, TotalEnergi)
                } else {
                    binding.edtUsername.error = "Username tidak boleh terdapat karakter &=_'-+,<>./\"\\"
                    binding.edtUsername.requestFocus()
                }
            }
        }

    }

    private fun setUpData() {
        sNama = intent.getStringExtra("nama").toString()
        sJenisKelamin = intent.getStringExtra("jenis_kelamin").toString()
        sUmur = intent.getStringExtra("umur").toString()
        sTinggi = intent.getStringExtra("tinggi").toString()
        sBerat = intent.getStringExtra("berat").toString()
    }

    private fun signUpForm() {
        sUsername = binding.edtUsername.text.toString()
        sEmail = binding.edtEmail.text.toString()
        sPassword = binding.edtPassword.text.toString()
    }

    private fun saveUser(
        sNama: String, sJenisKelamin: String, sUmur: String, sTinggi: String,
        sBerat: String, sUsername: String, sEmail: String, sPassword: String,
        TotalEnergi: Float) {

        val user = User()
        user.username = sUsername
        user.email = sEmail
        user.password = sPassword

        user.nama = sNama
        user.jenis_kelamin = sJenisKelamin

        user.umur = sUmur.toInt()
        user.tinggi = sTinggi.toInt()
        user.berat = sBerat.toInt()

        user.url = "default.png"

        user.totalenergikal = TotalEnergi

        authFirebase(sEmail, sPassword, user)

    }

    private fun authFirebase(sEmail: String, sPassword: String, data: User) {

        auth.fetchSignInMethodsForEmail(sEmail)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val signInMethods = it.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()){
                        auth.createUserWithEmailAndPassword(sEmail, sPassword)
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(this,"Akun Berhasil Terdaftar",Toast.LENGTH_LONG).show()
                                    savetoFirestore(data)
                                    finishAffinity()
                                    val intent = Intent(this@RegisterAkunActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Email Sudah Digunakan", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun savetoFirestore(data: User) {

        val userID = auth.currentUser?.uid

        if (userID != null) {
            db.collection("users")
                .document(userID)
                .set(data)
        }

        sharedPreferences.setValuesString("email", data.email.toString())
        sharedPreferences.setValuesString("username", data.username.toString())
        sharedPreferences.setValuesString("password", data.password.toString())

        sharedPreferences.setValuesString("nama", data.nama.toString())
        sharedPreferences.setValuesString("url", data.url.toString())
        sharedPreferences.setValuesString("jenis_kelamin", data.jenis_kelamin.toString())

        sharedPreferences.setValuesInt("umur", data.umur.toString().toInt())
        sharedPreferences.setValuesInt("tinggi", data.tinggi.toString().toInt())
        sharedPreferences.setValuesInt("berat", data.berat.toString().toInt())
        sharedPreferences.setValuesFloat("totalenergikal", data.totalenergikal.toString().toFloat())

        sharedPreferences.setValuesString("user_uid", userID)
    }
}