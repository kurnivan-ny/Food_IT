package com.kurnivan_ny.foodit.ui.sign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.kurnivan_ny.foodit.databinding.ActivityLoginBinding
import com.kurnivan_ny.foodit.ui.main.HomeActivity
import com.kurnivan_ny.foodit.viewmodel.preferences.SharedPreferences

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var iUsername: String
    private lateinit var iPassword: String

    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        sharedPreferences = SharedPreferences(this)

        sharedPreferences.setValuesString("onboarding", "1")

        binding.btnDaftar.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        if (sharedPreferences.getValuesString("status").equals("1")){
            finishAffinity()

            val goHome = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(goHome)
        }

        binding.btnMasuk.setOnClickListener {
            iUsername = binding.etUsername.text.toString()
            iPassword = binding.etPassword.text.toString()

            if (iUsername.equals("")){
                binding.etUsername.error = "Silakan Isi Username Anda"
                binding.etUsername.requestFocus()
            } else if (iPassword.equals("")){
                binding.etPassword.error = "Silakan Isi Password Anda"
                binding.etPassword.requestFocus()
            } else {
                pushLogin(iUsername, iPassword)
            }
        }
    }

    private fun pushLogin(iUsername: String, iPassword: String) {

        db.collection("users").document(iUsername).get()
            .addOnSuccessListener { document ->
                if (document.get("username") == null){
                    Toast.makeText(this@LoginActivity, "Username Tidak Ditemukan",
                        Toast.LENGTH_LONG).show()
                } else {
                    if (document.get("password")!!.equals(iPassword)){

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

                        sharedPreferences.setValuesString("status", "1")

                        finishAffinity()

                        var intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "Password Anda Salah", Toast.LENGTH_LONG).show()
                    }
                }
            }

    }
}