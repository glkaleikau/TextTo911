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
import androidx.viewpager.widget.ViewPager

class EmergencyDetailsActivity : AppCompatActivity() {

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
        finish()
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_details)
        supportActionBar?.hide()
        if(!checkForInternet(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                showPopup()
            }, 1000)
        }
        val button: Button = findViewById(R.id.button_continue2)

        val medicalBtn : Button = findViewById(R.id.medicalBtn)
        //medicalBtn.setBackgroundResource(R.drawable.medical_selector)
        val fireBtn : Button = findViewById(R.id.fireBtn)
        //fireBtn.setBackgroundResource(R.drawable.fire_selector)
        val policeBtn : Button = findViewById(R.id.policeBtn)
        //policeBtn.setBackgroundResource(R.drawable.police_selector)

        val backBtn : Button = findViewById(R.id.backButton1)
        backBtn.setOnClickListener{
            onBackPressed()
        }

        medicalBtn.isSelected = Data.Companion.emergencyInfo.medical
        fireBtn.isSelected = Data.Companion.emergencyInfo.fire
        policeBtn.isSelected = Data.Companion.emergencyInfo.police

        updateButtonUsability(button)

        button.setOnClickListener {
            if (button.isEnabled) {
                val intent = Intent(this, WeaponsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
                finish()
            }
        }

        medicalBtn.setOnClickListener {
            Log.d("MEDICAL", "PRESSED")
            medicalBtn.isSelected = !medicalBtn.isSelected
            Data.Companion.emergencyInfo.medical = !Data.Companion.emergencyInfo.medical
            updateButtonUsability(button)
        }
        fireBtn.setOnClickListener {
            Log.d("FIRE", "PRESSED")
            fireBtn.isSelected = !fireBtn.isSelected
            Data.Companion.emergencyInfo.fire = !Data.Companion.emergencyInfo.fire
            updateButtonUsability(button)
        }
        policeBtn.setOnClickListener {
            Log.d("POLICE", "PRESSED")
            policeBtn.isSelected = !policeBtn.isSelected
            Data.Companion.emergencyInfo.police = !Data.Companion.emergencyInfo.police
            updateButtonUsability(button)
        }
    }

    fun updateButtonUsability(button: Button)
    {
        if (Data.emergencyServicesSelected())
        {
            button.isEnabled = true
            button.setBackgroundResource(R.drawable.rounded_corner)
        }
        else
        {
            button.isEnabled = false
            button.setBackgroundResource(R.drawable.unselected_btn)
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