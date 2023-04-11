package com.kurnivan_ny.foodit.data.api

import com.kurnivan_ny.foodit.data.model.auth.ODResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Endpoint
const val OD_ENDPOINT = "predict"

interface ApiService {

    // POST OD RESULT
    @POST(OD_ENDPOINT)
    fun postODResult(
        @Body data: HashMap<String, String>
    ): Response<ODResponse>
}