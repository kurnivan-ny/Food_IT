package com.kurnivan_ny.foodit.view.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kurnivan_ny.foodit.databinding.ActivityLupaPasswordBinding

class LupaPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLupaPasswordBinding

    private lateinit var sEmail: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLupaPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnKembali.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnKirim.setOnClickListener {

            sEmail = binding.etEmail.text.toString()

            if (sEmail.equals("")){
                binding.etEmail.error = "Silakan Isi Email Lupa Password"
                binding.etEmail.requestFocus()
            } else {
                auth.sendPasswordResetEmail(sEmail)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Email Reset Password Dikirim", Toast.LENGTH_LONG)
                                .show()

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)

                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Email Reset Password Gagal Dikirim",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
}