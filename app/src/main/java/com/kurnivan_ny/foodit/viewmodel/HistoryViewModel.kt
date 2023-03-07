package com.kurnivan_ny.foodit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kurnivan_ny.foodit.data.model.history.ListHistoryData


class HistoryViewModel: ViewModel() {
    val newhistory = MutableLiveData<ArrayList<ListHistoryData>>(arrayListOf())
}