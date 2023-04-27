package com.kurnivan_ny.foodit.view.main.fragment.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences
import com.kurnivan_ny.foodit.databinding.FragmentGantiEmailBinding
import com.kurnivan_ny.foodit.databinding.FragmentGantiPasswordBinding

class GantiPasswordFragment : Fragment() {

    // BINDING
    private var _binding: FragmentGantiPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    private lateinit var sPasswordLama: String
    private lateinit var sPasswordBaru: String
    private lateinit var sKonfirmasiPassword: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGantiPasswordBinding.inflate(inflater, container, false)
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
        auth = FirebaseAuth.getInstance()

        binding.ivBackAkun.setOnClickListener {
            toPengAkun()
        }

        binding.tvLupaPassword.setOnClickListener {
            val toLupaPasswordFragment = GantiPasswordFragmentDirections.actionGantiPasswordFragmentToProfileLupaPasswordFragment()
            binding.root.findNavController().navigate(toLupaPasswordFragment)
        }

        binding.btnSimpan.setOnClickListener {

            sPasswordLama = binding.edtPasswordLama.text.toString()
            sPasswordBaru = binding.edtPasswordBaru.text.toString()
            sKonfirmasiPassword = binding.edtKonfirmasiPass.text.toString()

            val UserUID = sharedPreferences.getValuesString("user_uid")

            if (sPasswordLama.equals("")){
                binding.edtPasswordLama.error = "Silakan Isi Password Lama"
                binding.edtPasswordLama.requestFocus()
            } else if (sPasswordBaru.equals("")){
                binding.edtPasswordBaru.error = "Silakan Isi Password Baru"
                binding.edtPasswordBaru.requestFocus()
            } else if (sPasswordBaru.length < 6){
                binding.edtPasswordBaru.error = "Password Baru Kurang Dari 6 Karakter"
                binding.edtPasswordBaru.requestFocus()
            } else if (sKonfirmasiPassword.equals("")){
                binding.edtKonfirmasiPass.error = "Silakan Isi Konfirmasi Password"
                binding.edtKonfirmasiPass.requestFocus()
            } else {

                val email = sharedPreferences.getValuesString("email")
                val user = auth.currentUser

                val credential = EmailAuthProvider.getCredential(email!!, sPasswordLama)

                if (user != null) {
                    user.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                if (sKonfirmasiPassword.equals(sPasswordBaru)){
                                    user.updatePassword(sKonfirmasiPassword)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful){

                                                db.collection("users").document(UserUID!!)
                                                    .update(
                                                        "password", sKonfirmasiPassword
                                                    )

                                                sharedPreferences.setValuesString("password", sKonfirmasiPassword)

                                                Toast.makeText(requireContext(), "Password Berhasil Diubah", Toast.LENGTH_LONG).show()

                                                toPengAkun()
                                            } else {
                                                Toast.makeText(requireContext(), "Password Tidak Berhasil Diubah", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                } else {
                                    binding.edtKonfirmasiPass.error = "Konfirmasi Password Tidak Sama Dengan Password Baru"
                                    binding.edtKonfirmasiPass.requestFocus()
                                }
                            } else {
                                binding.edtPasswordLama.error = "Password Lama Salah"
                                binding.edtPasswordLama.requestFocus()
                            }
                        }
                }
            }

        }

    }

    private fun toPengAkun() {
        val toPengAkunFragment = GantiPasswordFragmentDirections.actionGantiPasswordFragmentToPengAkunFragment()
        binding.root.findNavController().navigate(toPengAkunFragment)
    }

}