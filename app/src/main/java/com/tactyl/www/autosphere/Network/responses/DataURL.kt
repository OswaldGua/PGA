package com.tactyl.www.autosphere.Network.responses


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataURL(
    val id: Int, // 17
    @SerializedName("Serial")
    val serial: String, // AB-5.11-20180911-0017
    val title: String, // Lyon
    @SerializedName("URL")
    val uRL: String // https://borne.autosphere.fr/concession/siv2,siv6


): Serializable




