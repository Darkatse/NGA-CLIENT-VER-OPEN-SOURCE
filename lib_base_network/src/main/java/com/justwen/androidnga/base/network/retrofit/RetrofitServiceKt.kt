package com.justwen.androidnga.base.network.retrofit

import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface RetrofitServiceKt {

    @GET("nuke.php")
    suspend fun getString(@QueryMap map: Map<String, String>): String

    @FormUrlEncoded
    @POST("nuke.php")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded",
        "Accept-Charset: GBK",
    )
    suspend fun post(
        @QueryMap queryMap: Map<String, String> = HashMap(),
        @FieldMap fieldMap: Map<String, String> = HashMap()
    ): String
}