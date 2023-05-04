package com.kurnivan_ny.foodit.view.main.fragment.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kurnivan_ny.foodit.databinding.FragmentProfileLupaPasswordBinding
import com.kurnivan_ny.foodit.view.sign.LoginActivity

class ProfileLupaPasswordFragment : Fragment() {

    //BINDING
    private var _binding: FragmentProfileLupaPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var sEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileLupaPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.ivBackAkun.setOnClickListener {
            val toGantiPasswordFragment = ProfileLupaPasswordFragmentDirections
                .actionProfileLupaPasswordFragmentToGantiPasswordFragment()
            binding.root.findNavController().navigate(toGantiPasswordFragment)
        }

        binding.btnKirim.setOnClickListener {

            sEmail = binding.edtEmail.text.toString()

            if (sEmail.equals("")){
                binding.edtEmail.error = "Silakan Isi Email Lupa Password"
                binding.edtEmail.requestFocus()
            } else {
                auth.sendPasswordResetEmail(sEmail)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(), "Email Reset Password Dikirim", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Email Reset Password Gagal Dikirim",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                toProfileFragment()
            }

        }

    }

    private fun toProfileFragment() {
        val toProfileFragment = ProfileLupaPasswordFragmentDirections.actionProfileLupaPasswordFragmentToProfileFragment()
        binding.root.findNavController().navigate(toProfileFragment)
    }

}