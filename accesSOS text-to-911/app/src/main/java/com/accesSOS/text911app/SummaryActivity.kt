package com.accesSOS.text911app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.viewpager.widget.ViewPager

class SummaryActivity : AppCompatActivity() {
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, OptionalEmergencyDetails::class.java)
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
        setContentView(R.layout.activity_summary)
        supportActionBar?.hide()
        if(!checkForInternet(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                showPopup()
            }, 1000)
        }

        Data.Companion.updateSummaryStrings()

        val locationLabel : TextView = findViewById(R.id.locationDetailsString)
        val emergencyServicesRequestedLabel : TextView = findViewById(R.id.emergencyServicesRequestedString)
        val weaponsLabel : TextView = findViewById(R.id.weaponDetailsString)
        val locationDetailsLabel : TextView = findViewById(R.id.locationIndoorOutdoorDetailsString)
        val emergencyDetailsLabel : TextView = findViewById(R.id.emergencyDetailsString)


        locationLabel.text = Data.Companion.summaryStrings.locationString
        emergencyServicesRequestedLabel.text = Data.Companion.summaryStrings.emergencyServicesRequestedString
        weaponsLabel.text = Data.Companion.summaryStrings.weaponsString
        locationDetailsLabel.text = Data.Companion.summaryStrings.locationDetailsString
        emergencyDetailsLabel.text = Data.Companion.summaryStrings.emergencyDetailsString


        val button: Button = findViewById(R.id.button_text911)

        val locationEditBtn : Button = findViewById(R.id.editLocationBtn)

        locationEditBtn.setOnClickListener{
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
            finish()
        }

        val emergencyServicesEditBtn : Button = findViewById(R.id.editEmergencyServicesBtn)

        emergencyServicesEditBtn.setOnClickListener{
            val intent = Intent(this, EmergencyDetailsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
            finish()
        }

        val weaponsEditBtn : Button = findViewById(R.id.editWeaponsDetailsBtn)

        weaponsEditBtn.setOnClickListener{
            val intent = Intent(this, WeaponsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
            finish()
        }

        val editLocationDetailsBtn : Button = findViewById(R.id.editLocationDetailsBtn)

        editLocationDetailsBtn.setOnClickListener{
            val intent = Intent(this, LocationDetailsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
            finish()
        }

        val emergencyDetailsEditBtn : Button = findViewById(R.id.editEmergencyDetailsBtn)

        emergencyDetailsEditBtn.setOnClickListener{
            val intent = Intent(this, OptionalEmergencyDetails::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
            finish()
        }



        button.setOnClickListener {

            Data.Companion.generateSMSString()
            val uri = Uri.parse("smsto:1234567890")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", Data.Companion.smsString)
            startActivity(intent)
            //finish()
            // Do something in response to button click
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