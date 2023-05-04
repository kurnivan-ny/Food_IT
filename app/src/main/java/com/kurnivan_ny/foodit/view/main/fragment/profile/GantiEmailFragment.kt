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
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences
import com.kurnivan_ny.foodit.databinding.FragmentGantiEmailBinding


class GantiEmailFragment : Fragment() {

    // BINDING
    private var _binding: FragmentGantiEmailBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    private lateinit var sEmailLama: String
    private lateinit var sEmailBaru: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGantiEmailBinding.inflate(inflater, container, false)
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
            toProfileFragment()
        }

        binding.btnSimpan.setOnClickListener {

            sEmailLama = binding.edtEmailLama.text.toString()
            sEmailBaru = binding.edtEmailBaru.text.toString()

            val UserUID = sharedPreferences.getValuesString("user_uid")

            if (sEmailLama.equals("")){
                binding.edtEmailLama.error = "Silakan Isi Email Lama"
                binding.edtEmailLama.requestFocus()
            } else if (sEmailBaru.equals("")){
                binding.edtEmailBaru.error = "Silakan Isi Email Baru"
                binding.edtEmailBaru.requestFocus()
            } else {

                val password = sharedPreferences.getValuesString("password").toString()

                val user = auth.currentUser
                val credential = EmailAuthProvider.getCredential(sEmailLama, password)

                if (user != null) {
                    user.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                user.updateEmail(sEmailBaru)
                                    .addOnCompleteListener{
                                        if (it.isSuccessful){

                                            db.collection("users").document(UserUID!!)
                                                .update(
                                                    "email", sEmailBaru
                                                )

                                            sharedPreferences.setValuesString("email", sEmailBaru)

                                            Toast.makeText(requireContext(), "Email Berhasil Diubah", Toast.LENGTH_LONG).show()

                                            toProfileFragment()
                                        } else {
                                            Toast.makeText(requireContext(), "Email Gagal Diubah", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            } else {
                                binding.edtEmailLama.error = "Email Lama Salah"
                                binding.edtEmailLama.requestFocus()
                            }
                    }
                }
            }
        }

    }

    private fun toProfileFragment() {
        val toProfileFragment = GantiEmailFragmentDirections.actionGantiEmailFragmentToProfileFragment()
        binding.root.findNavController().navigate(toProfileFragment)
    }

}