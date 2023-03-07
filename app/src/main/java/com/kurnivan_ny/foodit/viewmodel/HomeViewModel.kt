package com.kurnivan_ny.foodit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kurnivan_ny.foodit.data.model.home.ImageHomeData

class HomeViewModel: ViewModel(){
    val tanggal_makan: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

//    val status_karbohidrat_konsumsi: MutableLiveData<String> by lazy {
//        MutableLiveData<String>()
//    }
//
//    val status_protein_konsumsi: MutableLiveData<String> by lazy {
//        MutableLiveData<String>()
//    }
//
//    val status_lemak_konsumsi: MutableLiveData<String> by lazy {
//        MutableLiveData<String>()
//    }

    val total_karbohidrat_konsumsi: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }

    val total_protein_konsumsi: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }

    val total_lemak_konsumsi: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }

    val homedata = MutableLiveData<ArrayList<ImageHomeData>>(arrayListOf())
}