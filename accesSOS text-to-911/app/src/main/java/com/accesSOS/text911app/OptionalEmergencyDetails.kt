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
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import androidx.viewpager.widget.ViewPager


class OptionalEmergencyDetails : AppCompatActivity() {

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LocationDetailsActivity::class.java)
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
        setContentView(R.layout.activity_optional_emergency_details)
        supportActionBar?.hide()
        if(!checkForInternet(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                showPopup()
            }, 1000)
        }

        val physical : Button = findViewById(R.id.physicalBtn)
        val breathing : Button = findViewById(R.id.breathingBtn)
        val traffic : Button = findViewById(R.id.trafficBtn)
        val chest : Button = findViewById(R.id.chestBtn)
        val social : Button = findViewById(R.id.socialBtn)
        val bleeding : Button = findViewById(R.id.bleedingBtn)
        val assault : Button = findViewById(R.id.assaultBtn)
        val crime : Button = findViewById(R.id.crimeBtn)
        val none : Button = findViewById(R.id.noneBtn)
        val contBtn : Button = findViewById(R.id.button16)

        physical.isSelected = Data.Companion.optionalDetails.physical
        breathing.isSelected = Data.Companion.optionalDetails.breathing
        traffic.isSelected = Data.Companion.optionalDetails.traffic
        chest.isSelected = Data.Companion.optionalDetails.chest
        social.isSelected = Data.Companion.optionalDetails.social
        bleeding.isSelected = Data.Companion.optionalDetails.bleeding
        assault.isSelected = Data.Companion.optionalDetails.assault
        crime.isSelected = Data.Companion.optionalDetails.crime
        none.isSelected = Data.Companion.optionalDetails.noDetails

        updateButtonUsability(contBtn,none)

        val backBtn : Button = findViewById(R.id.backButton4)
        backBtn.setOnClickListener{
            onBackPressed()
        }

        physical.setOnClickListener {
            physical.isSelected = !physical.isSelected
            Data.Companion.optionalDetails.physical = !Data.Companion.optionalDetails.physical
            none.isSelected = false
            Data.Companion.optionalDetails.noDetails = false
            updateButtonUsability(contBtn, none)
        }

        breathing.setOnClickListener {
            breathing.isSelected = !breathing.isSelected
            Data.Companion.optionalDetails.breathing = !Data.Companion.optionalDetails.breathing
            none.isSelected = false
            Data.Companion.optionalDetails.noDetails = false
            updateButtonUsability(contBtn, none)
        }

        traffic.setOnClickListener {
            traffic.isSelected = !traffic.isSelected
            Data.Companion.optionalDetails.traffic = !Data.Companion.optionalDetails.traffic
            none.isSelected = false
            Data.Companion.optionalDetails.noDetails = false
            updateButtonUsability(contBtn, none)
        }

        chest.setOnClickListener {
            chest.isSelected = !chest.isSelected
            Data.Companion.optionalDetails.chest = !Data.Companion.optionalDetails.chest
            none.isSelected = false
            Data.Companion.optionalDetails.noDetails = false
            updateButtonUsability(contBtn, none)
        }

        social.setOnClickListener {
            social.isSelected = !social.isSelected
            Data.Companion.optionalDetails.social = !Data.Companion.optionalDetails.social
            none.isSelected = false
            Data.Companion.optionalDetails.noDetails = false
            updateButtonUsability(contBtn, none)
        }

        bleeding.setOnClickListener {
            bleeding.isSelected = !bleeding.isSelected
            Data.Companion.optionalDetails.bleeding = !Data.Companion.optionalDetails.bleeding
            none.isSelected = false
            Data.Companion.optionalDetails.noDetails = false
            updateButtonUsability(contBtn, none)
        }

        assault.setOnClickListener {
            assault.isSelected = !assault.isSelected
            Data.Companion.optionalDetails.assault = !Data.Companion.optionalDetails.assault
            none.isSelected = false
            Data.Companion.optionalDetails.noDetails = false
            updateButtonUsability(contBtn, none)
        }

        crime.setOnClickListener {
            crime.isSelected = !crime.isSelected
            Data.Companion.optionalDetails.crime = !Data.Companion.optionalDetails.crime
            none.isSelected = false
            Data.Companion.optionalDetails.noDetails = false
            updateButtonUsability(contBtn, none)
        }

        none.setOnClickListener {
            none.isSelected = true
            Data.Companion.resetOptionalDetails()
            Data.Companion.optionalDetails.noDetails = true
            physical.isSelected = false
            breathing.isSelected = false
            traffic.isSelected = false
            chest.isSelected = false
            social.isSelected = false
            bleeding.isSelected = false
            assault.isSelected = false
            crime.isSelected = false
            updateButtonUsability(contBtn, none)
        }

        contBtn.setOnClickListener {
            if (assault.isSelected || crime.isSelected) {
                contBtn.setBackgroundResource(R.drawable.rounded_corner)
                contBtn.isEnabled = true
            }
            if (contBtn.isEnabled) {
                val intent = Intent(this, SummaryActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
                finish()
            }
        }

        val skip : Button = findViewById(R.id.skipButton)
        skip.setOnClickListener {
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
            finish()

        }

    }
    fun updateButtonUsability(button: Button, noneButton : Button)
    {
        if (Data.optionalDetails.noDetails)
        {
            noneButton.setTextColor(getResources().getColor(R.color.accessRED))
        }
        else
        {
            noneButton.setTextColor(getResources().getColor(R.color.black))
        }
        if (Data.optionalDetailSelected())
        {
            button.isEnabled = true
            button.isSelected
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