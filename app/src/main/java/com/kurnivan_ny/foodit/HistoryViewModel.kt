package com.kurnivan_ny.foodit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HistoryViewModel: ViewModel() {
    val newhistory = MutableLiveData<ArrayList<ListHistoryModel>>(arrayListOf())
}