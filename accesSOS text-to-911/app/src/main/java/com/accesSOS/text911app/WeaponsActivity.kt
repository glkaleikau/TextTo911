package com.accesSOS.text911app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import android.widget.RadioButton
import androidx.viewpager.widget.ViewPager

class WeaponsActivity : AppCompatActivity() {
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000

    private fun showPopup() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val handler = Handler(Looper.getMainLooper())
        val pw = PopupWindow(inflater.inflate(R.layout.activity_internet_popup, null, false), ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT, false)
        pw.elevation = 20F
        pw.showAtLocation(findViewById(R.id.container), Gravity.CENTER, 0, 0)
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            if(checkForInternet(this)){
                pw.dismiss()
            }
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, EmergencyDetailsActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weapons)
        supportActionBar?.hide()
        if(!checkForInternet(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                showPopup()
            }, 1000)
        }
        val continueBtn: Button = findViewById(R.id.button_continue3)

        continueBtn.setOnClickListener {
            if (continueBtn.isEnabled) {
                val intent = Intent(this, LocationDetailsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
                finish()

            }
        }

        val backBtn : Button = findViewById(R.id.backButton2)
        backBtn.setOnClickListener{
            onBackPressed()
        }

        val radioBtn1 : RadioButton = findViewById(R.id.yesBtn)
        val radioBtn2 : RadioButton = findViewById(R.id.noBtn)
        val radioBtn3 : RadioButton = findViewById(R.id.maybeBtn)

        radioBtn1.isChecked = Data.Companion.weaponsInfo.weaponsYes
        radioBtn2.isChecked = Data.Companion.weaponsInfo.weaponsNo
        radioBtn3.isChecked = Data.Companion.weaponsInfo.weaponsNotSure

        Log.i("weaponsYes: ", Data.Companion.weaponsInfo.weaponsYes.toString())
        Log.i("weaponsNo: ", Data.Companion.weaponsInfo.weaponsNo.toString())
        Log.i("weaponsMaybe: ", Data.Companion.weaponsInfo.weaponsNotSure.toString())

        updateButtonUsability(continueBtn)

        radioBtn1.setOnClickListener {
            if (radioBtn1.isChecked) {
                Data.Companion.weaponsInfo.weaponsYes = true
                Data.Companion.weaponsInfo.weaponsNo = false
                Data.Companion.weaponsInfo.weaponsNotSure = false
                updateButtonUsability(continueBtn)
            }
        }

        radioBtn2.setOnClickListener {
            if (radioBtn2.isChecked) {
                Data.Companion.weaponsInfo.weaponsYes = false
                Data.Companion.weaponsInfo.weaponsNo = true
                Data.Companion.weaponsInfo.weaponsNotSure = false
                updateButtonUsability(continueBtn)
            }
        }

        radioBtn3.setOnClickListener {
            if (radioBtn3.isChecked) {
                Data.Companion.weaponsInfo.weaponsYes = false
                Data.Companion.weaponsInfo.weaponsNo = false
                Data.Companion.weaponsInfo.weaponsNotSure = true
                updateButtonUsability(continueBtn)
            }
        }
    }

    fun updateButtonUsability(continueBtn: Button)
    {
        if (Data.weaponSelected())
        {
            continueBtn.isEnabled = true
            continueBtn.setBackgroundResource(R.drawable.rounded_corner)
        }
        else
        {
            continueBtn.isEnabled = false
            continueBtn.setBackgroundResource(R.drawable.unselected_btn)
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}