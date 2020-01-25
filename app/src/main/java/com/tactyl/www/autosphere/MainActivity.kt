package com.tactyl.www.autosphere

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.webkit.*
import kotlinx.android.synthetic.main.activity_webview.*
import org.jetbrains.anko.progressDialog
import org.threeten.bp.Instant
import java.io.File


var returnCheckBuildProp=false
var webURLFromAPI=""
var webURL=""
var ProductModel=""
var ProductSerial=""

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : BaseActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)



//        registerReceiver(ConnectivityReceiver(),
//                IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY   .CONNECTIVITY_ACTION))

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE                                        //Config plein ecran + desactive barres
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        supportActionBar?.hide()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        setContentView(R.layout.activity_webview)



//        Thread.setDefaultUncaughtExceptionHandler(New MyExceptionHandler(this));
//        if (getIntent().getBooleanExtra("crash", false)) {
//            Toast.makeText(this, "App restarted after crash", Toast.LENGTH_SHORT).show();
//        }

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webView.webViewClient=mywebViewClient()

        webView.visibility= View.GONE
        //imageView.visibility= View.VISIBLE
        textViewInfo.visibility= View.VISIBLE

        returnCheckBuildProp=checkBuildProp()
        if (!returnCheckBuildProp)                                                                      // Verification utilisation sur AMISBOX + SN
            {
                textViewInfo.text = "\nVeuillez utiliser ce programme sur une AmisBox\n"
            }
        else
            {
                textViewInfo.text = "AmisBox - Infos : \n ${getString(R.string.title_activity_fullscreen)} \nSN : $ProductSerial \n\nLog info :\n"
            }

        }

    private fun checkBuildProp():Boolean {                                                          //Verification si build.prop contient firefly...
        val lineList = File("/system/build.prop").readLines()
        var FlagReturn:Boolean=false
        lineList.forEach {
            //Log.d("AMISWEB : ", "Build.Prop :  $it")
            if (it.contains("ro.product.model=firefly-rk3288",true)){
                ProductModel = it.substring(18)
                FlagReturn=true}

            if (it.contains("Serial=",true)){
                ProductSerial = it.substring(7)
            FlagReturn=true}
        }

        return FlagReturn
    }



    private inner class mywebViewClient : WebViewClient() {

        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            super.onReceivedHttpError(view, request, errorResponse)

            textViewInfo.text = textViewInfo.text.toString() + Instant.now() +" : HTTP Error :\n Request : " + request.toString() + "\n errorResponse : " + errorResponse + "\n"

            Log.d("AMISWEB", " : HTTP Error :\n Request : " + request.toString() + "\n errorResponse : " + errorResponse + "\n")

            onErrorWebView("mywebViewClient-onReceivedHttpError")

        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            textViewInfo.text = textViewInfo.text.toString() + Instant.now() +" : HTTP Error :\n Request : " + request.toString() + "\n errorResponse : " + error + "\n"
            Log.d("AMISWEB", " : HTTP Error :\n Request : " + request.toString() + "\n errorResponse : " + error + "\n")

            onErrorWebView("mywebViewClient-onReceivedError")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            Log.d("AMISWEB : ", "onPageFinished : "+ url + " - " + webView.title)

            if (webView.title == "Page Web non disponible" || webView.title ==""){
                onErrorWebView("mywebViewClient-onPageFinished-title"+webView.title)
            }
            super.onPageFinished(view, url)
        }

        fun onErrorWebView(msg:String){
            val dialog = progressDialog(message = "La page web sera recharchée dans quelques instants …", title = "AmisBox - Erreur Page Web : " + msg)
            dialog.create()
            var countDownTimer = object : CountDownTimer(10000, 100) {
                override fun onFinish() {
                    dialog.dismiss()
                    goHome()
                }

                override fun onTick(p0: Long) {
                    //Log.d("AmisBox : ","Timer : "+p0)
                    dialog.incrementProgressBy(1)
                    if (dialog.progress>90){
                        dialog.setMessage("Chargement de la page ...")
                    }
                }
                // override object functions here, do it quicker by setting cursor on object, then type alt + enter ; implement members
            }

            countDownTimer.start()
        }

    }







}

