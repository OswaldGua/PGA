package com.tactyl.www.autosphere

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.tactyl.www.autosphere.Network.API.RetrofitClient
import com.tactyl.www.autosphere.Network.responses.DataURL
import kotlinx.android.synthetic.main.activity_webview.*
import org.jetbrains.anko.toast
import org.jetbrains.annotations.Nullable
import org.threeten.bp.Instant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface




@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    //private var mSnackBar: Snackbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        registerReceiver(ConnectivityReceiver(),
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        //showMessage(false)
    }

    private fun showMessage(isConnected: Boolean) {
        var msgTXT="\n"
        if (!isConnected or !returnCheckBuildProp) {
            if (webView.visibility!=View.GONE){
                //imageView.visibility=View.VISIBLE
                textViewInfo.visibility=View.VISIBLE
                webView.visibility=View.GONE
            }

            if (!isConnected)
                msgTXT = "$msgTXT " +Instant.now() +" : No Internet connection\n"

            Log.d("AMISWEB : ", textViewInfo.text.toString())
            textViewInfo.text = "${textViewInfo.text} $msgTXT"
        }

        else {

            msgTXT = "$msgTXT " + Instant.now() +" : Internet connection ON\n"
            Log.d("AMISWEB : ", "Internet connection ON")

            val netInfo = getDeviceIpAddress()
            Log.d("AMISWEB : ", "IP info : " + netInfo)
            msgTXT = "$msgTXT IP info : $netInfo  \n"

            imageView.visibility=View.GONE
            textViewInfo.visibility=View.GONE
            webView.visibility=View.VISIBLE
            textViewInfo.text = "${textViewInfo.text} $msgTXT"
            goHome()
        }


    }

    override fun onResume() {
        super.onResume()

        ConnectivityReceiver.connectivityReceiverListener = this
    }

    fun goHome()   {
        //TODO check si ProductSerial dif "", ne pas lancer returnCheckBuildProp (redondant)
        if (returnCheckBuildProp)
        //webView.loadUrl(getString(R.string.website_url))
            if(ProductSerial.isEmpty()){
                textViewInfo.text = "${textViewInfo.text}\n\n ***** NO SERIAL ID DETECTED, PLEASE CONTACT SUPPORT ***** \n"
            }
            else{
                webURLFromAPI = getURLFromAPI()
                textViewInfo.text = "${textViewInfo.text}\n\n ***** URL FROM API : $webURLFromAPI ***** \n"

                if (webURLFromAPI.isNotEmpty())  {
                    //TODO Sauve URL to File
                    //textViewInfo.text = "${textViewInfo.text}\n\n ***** URL FROM API ***** \n"
                    //webView.destroy()
                    //saveURLToFile(webURLFromAPI)
                    //webView.loadUrl(webURLFromAPI)
                }
                else {
                    textViewInfo.text = "${textViewInfo.text}\n\n ***** URL FROM FILE ***** \n"
                    webURL = getURLFromFile()
                    webView.loadUrl(webURL)
                }
            }

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

        if (mWifiManager!= null && mWifiManager.isWifiEnabled) {
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

    private fun getURLFromAPI():String{
        var tempwebURLFromAPI=""
        val destinationService = RetrofitClient.instance
        val requestCall = destinationService.getJSONFileAutosphere()

        requestCall.enqueue(object : Callback<List<DataURL>> {
            override fun onFailure(call: Call<List<DataURL>>, t: Throwable) {
                toast("Faillure Backoffice connection !")
                textViewInfo.text = "${textViewInfo.text} \n\n ***** Download URL from API Failed ***** \n"
            }

            override fun onResponse(call: Call<List<DataURL>>, response: Response<List<DataURL>>) {
                if(response.isSuccessful){
                    val urlListFromAPI=response.body()!!
                    textViewInfo.text = "${textViewInfo.text}\n DataFromAPI  OK : ${urlListFromAPI.size} \n"

                    val indexListU = urlListFromAPI.size-1
                   for (i in 0 until indexListU) {
                       if (urlListFromAPI[i].serial== ProductSerial){
                           tempwebURLFromAPI=urlListFromAPI[i].uRL
                           textViewInfo.text = "${textViewInfo.text} \n ***** URL FROM API : $tempwebURLFromAPI \n"

                           webView.loadUrl(tempwebURLFromAPI)
                           saveURLToFile(tempwebURLFromAPI)
                       }
                   }
                }
            }
        })
        return tempwebURLFromAPI
    }

    private fun getURLFromFile():String {                                                           //recuperation de l'url

        val sdcard = Environment.getExternalStorageDirectory()
        val home = File("$sdcard/AmisBox")
        var tempURL = getString(R.string.website_url)

        if (!home.exists()) {                                                                         //1er utilisation apres instalation, fir n'existe pas
            home.mkdirs()
//            var countDownTimer = object : CountDownTimer(2000, 100) {
//                override fun onFinish() {}
//
//                override fun onTick(p0: Long) {
//                    //Log.d("AmisBox : ","Timer : "+p0)
//                }
//            }
//                // override object functions here, do it quicker by setting cursor on object, then type alt + enter ; implement members
//            countDownTimer.start()
        }

        val fileAmisBoxSetting = File("$sdcard/AmisBox/setting.txt")

        if (!fileAmisBoxSetting.exists()) {                                                         //Si fichier n'existe pas, creation et ecriture d'url
            var fileWriter: FileWriter? = null

            try {
                fileWriter = FileWriter("$sdcard/AmisBox/setting.txt")
                //val tempURL = getString(R.string.website_url)
                fileWriter.append("url = $tempURL")
                fileWriter.append('\n')
                //return tempURL

            } catch (e: Exception) {
                println("Writing file error!")
                e.printStackTrace()
            } finally {
                try {
                    fileWriter!!.flush()
                    fileWriter.close()
                } catch (e: IOException) {
                    println("Flushing/closing error!")
                    e.printStackTrace()
                }
            }
        }
        else {                                                                                  //Sinon Lecture
            val lineList = fileAmisBoxSetting.readLines()
            lineList.forEach {
                //Log.d("AMISWEB : ", "Build.Prop :  $it")
                if (it.contains("url = ", true)) {
                    tempURL = it.substringAfterLast("= ")
                    //return tempURL
                }
            }

        }
        return tempURL
    }


    private fun saveURLToFile(urlToSave:String) {                                                           //recuperation de l'url

        val sdcard = Environment.getExternalStorageDirectory()
        val home = File("$sdcard/AmisBox")
        //var tempURL = getString(R.string.website_url)

        if (!home.exists())   home.mkdirs()                                                                       //1er utilisation apres instalation, fir n'existe pas

        val fileAmisBoxSetting = File("$sdcard/AmisBox/setting.txt")

        if (fileAmisBoxSetting.exists())
        {
            val tempVerif = fileAmisBoxSetting.delete()
            if (!tempVerif) Log.d("AmisBox","ERROR DELETE FILE")
        }

            var fileWriter: FileWriter? = null

            try {
                fileWriter = FileWriter("$sdcard/AmisBox/setting.txt")
                //val tempURL = getString(R.string.website_url)
                fileWriter.append("url = $urlToSave")
                fileWriter.append('\n')
                //return tempURL

            } catch (e: Exception) {
                println("Writing file error!")
                e.printStackTrace()
            } finally {
                try {
                    fileWriter!!.flush()
                    fileWriter.close()
                } catch (e: IOException) {
                    println("Flushing/closing error!")
                    e.printStackTrace()
                }
            }
    }

}