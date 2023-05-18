package com.kurnivan_ny.foodit.view.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kurnivan_ny.foodit.databinding.ActivityLoginBinding
import com.kurnivan_ny.foodit.view.main.activity.HomeActivity
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var iEmail: String
    private lateinit var iPassword: String

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = SharedPreferences(this)

//        sharedPreferences.setValuesString("onboarding", "1")

        binding.btnDaftar.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

//        if (sharedPreferences.getValuesString("status").equals("1")){
//            finishAffinity()
//
//            val goHome = Intent(this@LoginActivity, HomeActivity::class.java)
//            startActivity(goHome)
//        }

        binding.tvLupaPass.setOnClickListener {
            val intent = Intent(this@LoginActivity, LupaPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnMasuk.setOnClickListener {
            iEmail = binding.etEmail.text.toString()
            iPassword = binding.etPassword.text.toString()

            if (iEmail.equals("")){
                binding.etEmail.error = "Silakan Isi Email Anda"
                binding.etEmail.requestFocus()
            } else if (iPassword.equals("")){
                binding.etPassword.error = "Silakan Isi Password Anda"
                binding.etPassword.requestFocus()
            } else {
                pushLogin(iEmail, iPassword)
            }
        }
    }

    private fun pushLogin(iEmail: String, iPassword: String) {
       auth.signInWithEmailAndPassword(iEmail, iPassword)
           .addOnCompleteListener {
               if (it.isSuccessful){

                   val user_uid = auth.currentUser?.uid

                   if (user_uid != null) {

                       db.collection("users").document(user_uid).get()
                           .addOnSuccessListener { document ->

                               if (iPassword.equals(document.get("password"))){
                                   sharedPreferences.setValuesString("email", document.get("email").toString())
                                   sharedPreferences.setValuesString("username", document.get("username").toString())
                                   sharedPreferences.setValuesString("password", document.get("password").toString())
                                   sharedPreferences.setValuesString("url", document.get("url").toString())
                                   sharedPreferences.setValuesString("nama", document.get("nama").toString())
                                   sharedPreferences.setValuesString("jenis_kelamin", document.get("jenis_kelamin").toString())

                                   sharedPreferences.setValuesInt("umur", document.get("umur").toString().toInt())
                                   sharedPreferences.setValuesInt("tinggi", document.get("tinggi").toString().toInt())
                                   sharedPreferences.setValuesInt("berat", document.get("berat").toString().toInt())

                                   sharedPreferences.setValuesFloat("totalenergikal", (document.get("totalenergikal")
                                       .toString().replace(",",".") +"F").toFloat())

                                   sharedPreferences.setValuesString("user_uid", user_uid)

//                                   sharedPreferences.setValuesString("status", "1")

                                   val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                   startActivity(intent)

                               } else {
                                   db.collection("users").document(user_uid)
                                       .update(
                                           "password", iPassword
                                       ).addOnSuccessListener {
                                           db.collection("users").document(user_uid).get()
                                               .addOnSuccessListener { document ->
                                                   sharedPreferences.setValuesString("email", document.get("email").toString())
                                                   sharedPreferences.setValuesString("username", document.get("username").toString())
                                                   sharedPreferences.setValuesString("password", document.get("password").toString())
                                                   sharedPreferences.setValuesString("url", document.get("url").toString())
                                                   sharedPreferences.setValuesString("nama", document.get("nama").toString())
                                                   sharedPreferences.setValuesString("jenis_kelamin", document.get("jenis_kelamin").toString())

                                                   sharedPreferences.setValuesInt("umur", document.get("umur").toString().toInt())
                                                   sharedPreferences.setValuesInt("tinggi", document.get("tinggi").toString().toInt())
                                                   sharedPreferences.setValuesInt("berat", document.get("berat").toString().toInt())

                                                   sharedPreferences.setValuesFloat("totalenergikal", (document.get("totalenergikal")
                                                       .toString().replace(",",".") +"F").toFloat())

                                                   sharedPreferences.setValuesString("user_uid", user_uid)

//                                                   sharedPreferences.setValuesString("status", "1")

                                                   finishAffinity()

                                                   val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                                   startActivity(intent)
                                               }
                                       }
                               }
                           }
                   }

                   finish()

               } else {
                   Toast.makeText(this@LoginActivity, "Login Gagal Dilakukan", Toast.LENGTH_LONG).show()
               }
           }

    }
}