package com.tactyl.www.autosphere

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log


class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedOrConnecting(context))
        }
    }


    //private
    fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting



//        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
//        return if (connectivityManager is ConnectivityManager) {
//            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
//            networkInfo?.isConnected ?: false
//        } else false
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean){
            Log.d("AMISWEB : ", "Connection Change: ")
        }
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }


}