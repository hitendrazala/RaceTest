package com.example.composecomponenet

import com.example.composecomponenet.model.RaceData
import com.example.composecomponenet.model.RaceSummary
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RaceApi {
    @Headers(
        "Accept: application/json"
    )
    @GET("racing/")
    abstract fun getNextRaces(@Query("method") method: String,@Query("count")count:Int): Call<RaceData>

}