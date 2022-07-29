package com.flobiz.challenge.api

import com.flobiz.challenge.models.StackExchangeResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface StackExchangeApi {

    companion object {
        val BASE_URL = "https://api.stackexchange.com/2.2/"

        var stackExchangeApi: StackExchangeApi? = null

        fun getInstance(): StackExchangeApi {

            if (stackExchangeApi == null) {
                val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                stackExchangeApi = retrofit.create(StackExchangeApi::class.java)
            }
            return stackExchangeApi!!
        }
    }

    @GET("questions")
    fun getQuestions(
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "activity",
        @Query("site") site: String = "stackoverflow",
        @Query("page") page: Int = 1,
        @Query("pagesize") pagesize: Int = 30,
        @Query("tagged") tagged: String = "",
    ): Call<StackExchangeResponse>
}