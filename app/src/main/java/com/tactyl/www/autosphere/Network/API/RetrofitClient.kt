package com.tactyl.www.autosphere.Network.API

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL ="https://amisbox.tactyl.com/json/"                        // URL SERVER
    //private val AUTH = "Basic "+ Base64.encodeToString("user:password".toByteArray(), Base64.NO_WRAP)

    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                val original = it.request()
                val requestBuilder = original.newBuilder()
                        //.addHeader("Authorization", AUTH)                                         // SI besoin d'une identification

                        .method(original.method(),original.body())
                val request = requestBuilder.build()
                it.proceed(request)

            }.build()

    val instance : ApiInterface by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)

                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        retrofit.create(ApiInterface::class.java)
    }

   // fun <T> buildService(serviceType: Class<T>){
    //    return retrofit
   // }

}