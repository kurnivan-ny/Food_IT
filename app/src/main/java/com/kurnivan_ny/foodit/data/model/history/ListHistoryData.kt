package com.kurnivan_ny.foodit.data.model.history

data class ListHistoryData (
    var bulan_makan: String = "",
    var status_konsumsi_karbohidrat: String = "",
    var status_konsumsi_protein: String = "",
    var status_konsumsi_lemak: String = "",
    var tanggal_makan: String = "",
    var total_konsumsi_karbohidrat: Float = 0.00F,
    var total_konsumsi_protein: Float = 0.00F,
    var total_konsumsi_lemak: Float = 0.00F
)