package com.kurnivan_ny.foodit.ui.sign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.databinding.ActivityRegisterBinding
import com.kurnivan_ny.foodit.viewmodel.preferences.SharedPreferences

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var sNama:String
    private lateinit var sJenisKelamin:String
    private lateinit var sUmur:String
    private lateinit var sTinggi:String
    private lateinit var sBerat:String

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = SharedPreferences(this)
        sharedPreferences.setValuesString("onboarding", "1")

        binding.btnMasuk.setOnClickListener {
            finish()

            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        setUpForm()

        binding.btnLanjut.setOnClickListener {
            signUpForm()

            if (sNama.equals("")){
                binding.edtNama.error = "Silakan Isi Nama Lengkap"
                binding.edtNama.requestFocus()
            } else if (sJenisKelamin.equals("Masukan Jenis Kelamin")){
                binding.edtJenisKelamin.error = "Silakan Pilih Jenis Kelamin"
                binding.edtJenisKelamin.requestFocus()
            } else if (sUmur.equals("")){
                binding.edtUmur.error = "Silakan Isi Umur"
                binding.edtUmur.requestFocus()
            } else if (sTinggi.equals("")){
                binding.edtTinggi.error = "Silakan Isi TInggi Badan (cm)"
                binding.edtTinggi.requestFocus()
            } else if (sBerat.equals("")) {
                binding.edtBeratBadan.error = "Silakan Isi Berat Badan (kg)"
                binding.edtBeratBadan.requestFocus()
            } else {
                val intent = Intent(this@RegisterActivity,
                    RegisterAkunActivity::class.java)
                intent.putExtra("nama", sNama)
                intent.putExtra("jenis_kelamin", sJenisKelamin)
                intent.putExtra("umur", sUmur)
                intent.putExtra("tinggi", sTinggi)
                intent.putExtra("berat", sBerat)
                startActivity(intent)
            }
        }
    }

    private fun setUpForm() {
        val jeniskelamin = resources.getStringArray(R.array.jenis_kelamin)
        val arrayAdapterJenisKelamin = ArrayAdapter(this, R.layout.dropdown_item, jeniskelamin)
        binding.edtJenisKelamin.setAdapter(arrayAdapterJenisKelamin)
    }

    private fun signUpForm() {
        sNama = binding.edtNama.text.toString()
        sJenisKelamin = binding.edtJenisKelamin.text.toString()
        sUmur = binding.edtUmur.text.toString()
        sTinggi = binding.edtTinggi.text.toString()
        sBerat = binding.edtBeratBadan.text.toString()
    }
}