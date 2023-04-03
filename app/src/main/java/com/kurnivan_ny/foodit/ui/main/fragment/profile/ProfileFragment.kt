package com.kurnivan_ny.foodit.ui.main.fragment.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.databinding.FragmentProfileBinding
import com.kurnivan_ny.foodit.ui.sign.LoginActivity
import com.kurnivan_ny.foodit.data.preferences.SharedPreferences

class ProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var storage: FirebaseStorage
    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater)
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

        //SETUP
        showDataUser()

        itemOnClickListener()
    }

    private fun showDataUser() {

        binding.tvNama.setText(sharedPreferences.getValuesString("nama").toString())

        val sUrl = sharedPreferences.getValuesString("url").toString()

        storage.reference.child("image_profile/$sUrl").downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(this)
                .load(Uri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)
        }
    }

    private fun itemOnClickListener(){
        binding.btnDetailPribadi.setOnClickListener(this)
        binding.btnPengAkun.setOnClickListener(this)
        binding.btnKeluar.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_detail_pribadi -> {
                val toDetailPribadiFragment = ProfileFragmentDirections
                    .actionProfileFragmentToDetailPribadiFragment()
                binding.root.findNavController().navigate(toDetailPribadiFragment)
            }
            R.id.btn_peng_akun -> {
                val toPengAkunFragment = ProfileFragmentDirections
                    .actionProfileFragmentToPengAkunFragment()
                binding.root.findNavController().navigate(toPengAkunFragment)
            }
            R.id.btn_keluar -> {

                //logout, go to login activity
                alertLogout()
            }
        }
    }

    private fun alertLogout() {
        val view = View.inflate(requireContext(), R.layout.profile_logout_dialog, null)

        val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
            .setView(view)
            .setCancelable(false)
            .setNegativeButton("Tidak"){ p0, _ ->
                p0.dismiss()
            }
            .setPositiveButton("Ya"){_, _ ->
                progressBar(true)
                observerLogout()
            }

        val dialog = builder.create()

        dialog.setOnShowListener {
            val button_negative = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            button_negative.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_apple
                ))

            val button_positive = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button_positive.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_apple
                ))
        }

        dialog.show()
    }

    private fun observerLogout() {
        progressBar(false)
        sharedPreferences.clear()
        killActivity()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }

    private fun killActivity() {
        activity?.finish()
    }

    private fun progressBar(isLoading: Boolean) = with(binding){
        if (isLoading){
            this.progressBar.visibility = View.VISIBLE
        } else {
            this.progressBar.visibility = View.GONE
        }
    }
}