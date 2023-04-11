package com.kurnivan_ny.foodit.data.model.auth

import com.google.gson.annotations.SerializedName

data class ODResponse(

	@field:SerializedName("tanggal_makan")
	val tanggalMakan: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("bulan_makan")
	val bulanMakan: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("waktu_makan")
	val waktuMakan: String? = null
)

