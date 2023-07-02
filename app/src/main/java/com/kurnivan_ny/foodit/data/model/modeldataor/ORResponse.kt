package com.kurnivan_ny.foodit.data.model.modeldataor

import com.google.gson.annotations.SerializedName

data class ORResponse(

	@field:SerializedName("status") // key
	val status: String? = null
)

