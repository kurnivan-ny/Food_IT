package com.kurnivan_ny.foodit.data.api

import com.kurnivan_ny.foodit.data.model.modeldataor.ORResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Endpoint
const val OR_ENDPOINT = "predict"

interface ApiService {
    // POST OR RESULT
    @POST(OR_ENDPOINT)
    suspend fun postORResult(
        @Body data: HashMap<String, String>
    ): Response<ORResponse>
}