package com.kurnivan_ny.foodit.view.main.fragment.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.data.model.modelfirestore.User
import com.kurnivan_ny.foodit.databinding.FragmentPengAkunBinding
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences
import com.kurnivan_ny.foodit.view.sign.LoginActivity

class PengAkunFragment : Fragment() {

    //BINDING
    private var _binding: FragmentPengAkunBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth : FirebaseAuth

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
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        setUpForm()

        itemOnClickListener()
    }

    private fun setUpForm() {

        binding.tvNama.setText(sharedPreferences.getValuesString("nama").toString())

        binding.tvUsername.setText(sharedPreferences.getValuesString("username").toString()
        + " / " + String.format("%.2f",sharedPreferences.getValuesFloat("totalenergikal"))
                + " kal")

        val sUrl = sharedPreferences.getValuesString("url").toString()

        storage.reference.child("image_profile/$sUrl").downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(this)
                .load(Uri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)
        }
    }

    private fun itemOnClickListener() {

        val UserUID = sharedPreferences.getValuesString("user_uid").toString()

        binding.ivBackAkun.isClickable = true
        binding.ivBackAkun.setOnClickListener {
            val toProfileFragment =
                PengAkunFragmentDirections.actionPengAkunFragmentToProfileFragment()
            binding.root.findNavController().navigate(toProfileFragment)
        }
        binding.btnDetailPribadi.setOnClickListener {
            val toDetailPribadiFragment =
                PengAkunFragmentDirections.actionPengAkunFragmentToDetailPribadiFragment()
            binding.root.findNavController().navigate(toDetailPribadiFragment)
        }
        binding.btnGantiEmail.setOnClickListener {
            val toGantiEmailFragment =
                PengAkunFragmentDirections.actionPengAkunFragmentToGantiEmailFragment()
            binding.root.findNavController().navigate(toGantiEmailFragment)
        }
        binding.btnGantiPassword.setOnClickListener {
            val toGantiPasswordFragment =
                PengAkunFragmentDirections.actionPengAkunFragmentToGantiPasswordFragment()
            binding.root.findNavController().navigate(toGantiPasswordFragment)
        }
        binding.btnHapus.setOnClickListener {
            sharedPreferences.clear()
            val user = auth.currentUser!!

            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        db.collection("users").document(UserUID)
                            .delete()

                        Toast.makeText(
                            requireContext(),
                            "User Akun Sudah Terhapus",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            killActivity()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    private fun killActivity() {
        activity?.finish()
    }
}