package com.tactyl.www.autosphere

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_webview.*
import org.jetbrains.annotations.Nullable
import org.threeten.bp.Instant
import java.net.Inet4Address
import java.net.NetworkInterface




@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    //private var mSnackBar: Snackbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        registerReceiver(ConnectivityReceiver(),
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))


    }


    private fun showMessage(isConnected: Boolean) {
        var msgTXT="\n"
        if (!isConnected) {
            if (webView.visibility!=View.GONE){
                //imageView.visibility=View.VISIBLE
                textViewInfo.visibility=View.VISIBLE
                webView.visibility=View.GONE
            }

            msgTXT = msgTXT + Instant.now() +" : No Internet connection\n"
            Log.d("AMISWEB : ", textViewInfo.text.toString())
            textViewInfo.text = textViewInfo.text.toString() + msgTXT
        } else {

            msgTXT = msgTXT + Instant.now() +" : Internet connection ON\n"
            Log.d("AMISWEB : ", "Internet connection ON")

            val netInfo = getDeviceIpAddress()
            Log.d("AMISWEB : ", "IP info : " + netInfo)
            msgTXT = msgTXT + "IP info : " + netInfo //+"\n"

            imageView.visibility=View.GONE
            textViewInfo.visibility=View.GONE
            webView.visibility=View.VISIBLE
            textViewInfo.text = textViewInfo.text.toString() + msgTXT
            goHome()
        }


    }

    override fun onResume() {
        super.onResume()

        ConnectivityReceiver.connectivityReceiverListener = this
    }

    fun goHome()   {
        if (returnCheckBuildProp)
        //webView.loadUrl(getString(R.string.website_url))
        webView.loadUrl(webURL)
    }

    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        showMessage(isConnected)
    }


    private fun getDeviceIpAddress(): String? {
        var actualConnectedToNetwork: String? = null
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connManager != null) {
            val infoAllNetworks = connManager.allNetworks //.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val networkInfo: NetworkInfo? = connManager.activeNetworkInfo
            if (networkInfo?.isConnected?:true) { //    mWifi.isConnected) {

//                for (i in infoAllNetworks.indices ){
//                    //val txtInfoNetworkInterface=
//                    infoAllNetworks[i].writeToParcel(NetWorkInfoParcelData,0)
//                    Log.d("AMISWEB : ","txtInfoNetworkInterface : " + txtInfoNetworkInterface)
//                }
                //val typeConInternet = mWifi.
                actualConnectedToNetwork = getWifiIp()

            }
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = getNetworkInterfaceIpAddress()

        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = "127.0.0.1"
        }
        return actualConnectedToNetwork
    }

    @Nullable
    private fun getWifiIp(): String? {
        val mWifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Log.d("AMISWEB : ","MAC Add Wifi : " + "")

        if (mWifiManager != null && mWifiManager.isWifiEnabled) {
            val mac=mWifiManager.connectionInfo.macAddress
            Log.d("AMISWEB : ","MacAddress : " + mac)
            textViewInfo.text = textViewInfo.text.toString() + "MacAddress Wifi: " + mac +"\n"
            val ip = mWifiManager.connectionInfo.ipAddress
            return ((ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF) + "."
                    + (ip shr 24 and 0xFF))
        }
        return null
    }


    @Nullable
    fun getNetworkInterfaceIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface = en.nextElement()
                val enumIpAddr = networkInterface.getInetAddresses()
                    val macNet = networkInterface.hardwareAddress
                    val res1 = StringBuilder()
                        for (b in macNet) {
                        res1.append(String.format("%02X:", b))
                        }
                        if (res1.isNotEmpty())  res1.deleteCharAt(res1.length - 1)

                Log.d("AMISWEB : ","MacAddress : " + res1)
                textViewInfo.text = textViewInfo.text.toString() + "MacAddress Eth0 : " + res1 +"\n"

                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress() && inetAddress is Inet4Address) {
                        val host = inetAddress.getHostAddress()
                        //val hostnameTXT = inetAddress.hostName

                        //Todo affichje host name

                        if (!TextUtils.isEmpty(host)) {
                            return host
                        }
                    }
                }

            }
        } catch (ex: Exception) {
            Log.e("IP Address", "getLocalIpAddress", ex)
        }

        return null
    }

}