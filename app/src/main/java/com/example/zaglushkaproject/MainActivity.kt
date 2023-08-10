package com.example.zaglushkaproject


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.zaglushkaproject.data.SharedPreferencesHelper
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import stroke.app.pixfit.FragmentWebView
import java.util.Locale


class MainActivity : AppCompatActivity() {
//    private lateinit var webView: WebView
    private lateinit var remoteConfig: FirebaseRemoteConfig

    @SuppressLint("SetJavaScriptEnabled", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получаем менеджер фрагментов
        val fragmentManager: FragmentManager = supportFragmentManager

        // Начинаем транзакцию для добавления фрагмента
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Создаем экземпляр вашего фрагмента
        val fragmentWebView = FragmentWebView()

        // Заменяем контейнер текущим фрагментом
        fragmentTransaction.replace(R.id.fragment_container, fragmentWebView)

        // Подтверждаем транзакцию
        fragmentTransaction.commit()

//        webView = findViewById(R.id.webView)
//        webView.webViewClient = WebViewClient()

//        setupUIWebViewSetting()
        setupRemoteConfigSetting()

        val savedLink: String? = SharedPreferencesHelper.getSharedPreferencesLink(this)
        if (savedLink != null) {
            if (isInternetAvailable()) {
                if (savedInstanceState == null) {
                    val fragment = FragmentWebView()

                    val bundle = Bundle()
                    bundle.putString("arg", savedLink)
                    fragment.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit()
                }


//                webView.loadUrl(savedLink)
            } else {
                showNetworkErrorScreen()
            }
        } else {
            remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
                try {
                    if (task.isSuccessful) {
                        val linkURL = remoteConfig.getString("url")
                        if(linkURL == "" || checkIsEmu()){
                            openSportGameActivity()
                        } else {
                            SharedPreferencesHelper.saveSharedPreferencesLink(linkURL, this)
//                            webView.loadUrl(linkURL)
                            if (savedInstanceState == null) {
                                val fragment = FragmentWebView()

                                val bundle = Bundle()
                                bundle.putString("arg", linkURL)
                                fragment.arguments = bundle

                                supportFragmentManager.beginTransaction()
                                    .add(R.id.fragment_container, fragment)
                                    .commit()
                            }
                        }
                    } else {
                        showNetworkErrorScreen()
                    }
                } catch (e: Exception) {
                    showNetworkErrorScreen()
                }
            }
        }
    }

    private fun setupRemoteConfigSetting() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

//    @SuppressLint("SetJavaScriptEnabled")
//    private fun setupUIWebViewSetting() {
//        val webSettings: WebSettings = webView.settings
//        webSettings.javaScriptEnabled = true
//        webSettings.domStorageEnabled = true
//        webSettings.databaseEnabled = true
//        webSettings.setSupportZoom(false)
//        webSettings.allowFileAccess = true
//        webSettings.allowContentAccess = true
//        webSettings.loadWithOverviewMode = true
//        webSettings.useWideViewPort = true
//
//        val cookieManager: CookieManager = CookieManager.getInstance()
//        cookieManager.setAcceptCookie(true)
//    }

    private fun showNetworkErrorScreen() {
        findViewById<TextView>(R.id.textViewError).text = getString(R.string.error_internet)
    }

//    override fun onBackPressed() {
//        if (webView.canGoBack()) {
//            webView.goBack()
//        } else {
//            super.onBackPressed()
//        }
//    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    private fun openSportGameActivity() {
        val intent = Intent(this, QuizGameActivity::class.java)
        startActivity(intent)
    }

    private fun checkIsEmu(): Boolean {
        if (BuildConfig.DEBUG) return false // when developer use this build on emulator
        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        val brand = Build.BRAND
        var result = (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware == "goldfish"
                || Build.BRAND.contains("google")
                || buildHardware == "vbox86"
                || buildProduct == "sdk"
                || buildProduct == "google_sdk"
                || buildProduct == "sdk_x86"
                || buildProduct == "vbox86p"
                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
                || buildHardware.lowercase(Locale.getDefault()).contains("nox")
                || buildProduct.lowercase(Locale.getDefault()).contains("nox"))
        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") &&
                Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == buildProduct)
        return result
    }


}
