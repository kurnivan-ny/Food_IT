package com.kurnivan_ny.foodit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kurnivan_ny.foodit.data.model.manualinput.ListManualData

class ManualViewModel: ViewModel() {
    val newmanual = MutableLiveData<ArrayList<ListManualData>>(arrayListOf())
}