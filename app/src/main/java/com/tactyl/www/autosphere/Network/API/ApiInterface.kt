package com.tactyl.www.autosphere.Network.API

import com.tactyl.www.autosphere.Network.responses.DataURL
import retrofit2.Call
import retrofit2.http.GET


interface ApiInterface {
    @GET("autosphere.json")
    fun getJSONFileAutosphere(): Call<List<DataURL>>
}
