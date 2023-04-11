package com.kurnivan_ny.foodit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kurnivan_ny.foodit.data.model.modelui.manualinput.ListManualModel

class ManualViewModel: ViewModel() {
    val newmanual = MutableLiveData<ArrayList<ListManualModel>>(arrayListOf())
}