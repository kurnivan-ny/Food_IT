package com.kurnivan_ny.foodit

class User {
    var email:String?= null
    var username:String?= null
    var password:String?= null

    var nama:String?= null
    var url:String?= null

    var jenis_kelamin:String?= null
    var umur:Int?= null
    var tinggi:Int?= null
    var berat:Int?= null
    var totalenergikal:Float?= null
}

class Konsumsi {
    var bulan_makan:String? = null
    var tanggal_makan:String? = null

    var total_konsumsi_karbohidrat:Float?= null
    var total_konsumsi_protein:Float?= null
    var total_konsumsi_lemak:Float?= null

    var status_konsumsi_karbohidrat:String? = null
    var status_konsumsi_protein:String? = null
    var status_konsumsi_lemak:String? = null
}

class Makan {
    var waktu_makan:String? = null
    var nama_makanan:String? = null
    var satuan_makanan:String? = null
    var berat_makanan:Int? = null

    var karbohidrat:Float? = null
    var protein:Float? = null
    var lemak:Float? = null
}
