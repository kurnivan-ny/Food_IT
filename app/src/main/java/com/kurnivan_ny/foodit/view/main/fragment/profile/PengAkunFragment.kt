package com.kurnivan_ny.foodit.view.main.fragment.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.kurnivan_ny.foodit.data.model.modelfirestore.User
import com.kurnivan_ny.foodit.databinding.FragmentPengAkunBinding
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences

class PengAkunFragment : Fragment() {

    //BINDING
    private var _binding: FragmentPengAkunBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var db: FirebaseFirestore

    private lateinit var sEmail: String
    private lateinit var sUsername: String
    private lateinit var sPasswordLama: String
    private lateinit var sPasswordBaru: String
    private lateinit var sKonfirmasiPassBaru: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPengAkunBinding.inflate(inflater, container, false)
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

        setUpForm()

        itemOnClickListener()
    }

    private fun setUpForm() {
        binding.edtEmail.setText(sharedPreferences.getValuesString("email"))
        binding.edtUsername.setText(sharedPreferences.getValuesString("username"))
    }

    private fun itemOnClickListener() {
        binding.ivBackAkun.isClickable = true
        binding.ivBackAkun.setOnClickListener {
            val toProfileFragment = PengAkunFragmentDirections.actionPengAkunFragmentToProfileFragment()
            binding.root.findNavController().navigate(toProfileFragment)
        }

        binding.btnSimpan.setOnClickListener {

            edtForm()
            if (sEmail.equals("")){
                binding.edtEmail.error = "Silakan isi Email"
                binding.edtEmail.requestFocus()
            }
            else if (sUsername.equals("")){
                binding.edtUsername.error = "Silakan isi Username"
                binding.edtUsername.requestFocus()
            }
            else if (sPasswordLama.equals("")){
                binding.edtPasswordLama.error = "Silakan isi Password Lama"
                binding.edtPasswordLama.requestFocus()
            }
            else if (sPasswordBaru.equals("")){
                binding.edtPasswordBaru.error = "Silakan isi Password Baru"
                binding.edtPasswordBaru.requestFocus()
            }
            else if (sKonfirmasiPassBaru.equals("")){
                binding.edtKonfirmasiPass.error = "Silakan isi Konfirmasi Password Baru"
            }
            else {
                saveUser(sEmail,sUsername,sPasswordLama,sPasswordBaru,sKonfirmasiPassBaru)

                val toProfileFragment = PengAkunFragmentDirections.actionPengAkunFragmentToProfileFragment()
                binding.root.findNavController().navigate(toProfileFragment)
            }
        }
    }

    private fun saveUser(
        sEmail: String, sUsername: String,
        sPasswordLama: String, sPasswordBaru: String,
        sKonfirmasiPassBaru: String) {

        val user = User()

        user.username = sUsername
        user.email = sEmail
        user.password = sPasswordBaru

        if (sPasswordBaru.equals(sKonfirmasiPassBaru)){
            checkingPasswordLama( sPasswordLama, user)
        }
        else {
            Toast.makeText(requireContext(), "Password Baru Tidak Sama Dengan Konfirmasi Password", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkingPasswordLama( sPasswordLama: String, data: User) {

        if (sPasswordLama.equals(sharedPreferences.getValuesString("password"))) {
            savetoFirestore(data)
        }
        else {
            Toast.makeText(requireContext(), "Password Lama Anda Salah", Toast.LENGTH_LONG).show()
        }
    }

    private fun savetoFirestore(data: User) {

        db.collection("users").document(data.username!!)
            .update(
                "email", data.email.toString(),
                "username", data.username.toString(),
                "password", data.password.toString()
            ).addOnSuccessListener {
                Toast.makeText(requireContext(), "Berhasil", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal", Toast.LENGTH_LONG).show()
            }
        sharedPreferences.setValuesString("email", data.email.toString())
        sharedPreferences.setValuesString("username", data.username.toString())
        sharedPreferences.setValuesString("password", data.password.toString())
    }

    private fun edtForm() {
        sEmail = binding.edtEmail.text.toString()
        sUsername = binding.edtUsername.text.toString()
        sPasswordLama = binding.edtPasswordLama.text.toString()
        sPasswordBaru = binding.edtPasswordBaru.text.toString()
        sKonfirmasiPassBaru = binding.edtKonfirmasiPass.text.toString()
    }
}