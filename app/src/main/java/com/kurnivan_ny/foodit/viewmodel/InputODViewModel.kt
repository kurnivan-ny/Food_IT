package com.kurnivan_ny.foodit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kurnivan_ny.foodit.data.model.modelui.manualinput.ListManualModel


class InputODViewModel: ViewModel() {
    val imageFileURL: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val listmakanan = MutableLiveData<ArrayList<ListManualModel>>(arrayListOf())
}