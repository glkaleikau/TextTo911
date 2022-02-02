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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class LocationDetailsActivity : AppCompatActivity() {
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, WeaponsActivity::class.java)
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
        setContentView(R.layout.activity_location_details)
        supportActionBar?.hide()
        if(!checkForInternet(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                showPopup()
            }, 1000)
        }

        val continueBtn: Button = findViewById(R.id.button_continue4)
        continueBtn.setOnClickListener {
            //send info here
            val intent = Intent(this, OptionalEmergencyDetails::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            finish()
        }

        val backBtn : Button = findViewById(R.id.backButton3)
        backBtn.setOnClickListener{
            onBackPressed()
        }


        val indoor : Button = findViewById(R.id.indoorsBtn)
        val outdoor : Button = findViewById(R.id.outdoorsBtn)
        val moving : Button = findViewById(R.id.movingBtn)

        indoor.isSelected = Data.Companion.locationDetails.indoors
        outdoor.isSelected = Data.Companion.locationDetails.outdoors
        moving.isSelected = Data.Companion.locationDetails.moving
        updateButtons(indoor, outdoor, moving, continueBtn)

        indoor.setOnClickListener {
            indoor.isSelected = true
            outdoor.isSelected = false
            moving.isSelected = false
            Data.Companion.locationDetails.indoors = true
            Data.Companion.locationDetails.outdoors = false
            Data.Companion.locationDetails.moving = false
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.indoor_bottom_scheet_content, null)
            val btnClose = view.findViewById<Button>(R.id.dismiss)

            var textFieldRoomNumber = view.findViewById<TextInputEditText>(R.id.roomNumber)
            var textFieldFloorNumber = view.findViewById<TextInputEditText>(R.id.floorNumber)

            var roomLayout = view.findViewById<TextInputLayout>(R.id.roomNumberLayout)
            roomLayout.isHintEnabled = false

            var floorLayout = view.findViewById<TextInputLayout>(R.id.floorNumberLayout)
            floorLayout.isHintEnabled = false

            textFieldRoomNumber.setText(Data.Companion.locationDetails.indoorRoomNumber)
            textFieldFloorNumber.setText(Data.Companion.locationDetails.indoorFloorNumber)

            val indoorContinue = view.findViewById<Button>(R.id.indoor_continue)
            indoorContinue.setOnClickListener {
                Data.Companion.locationDetails.indoors = true
                updateButtons(indoor, outdoor, moving, continueBtn)
                Data.Companion.locationDetails.indoorRoomNumber = textFieldRoomNumber.text.toString()
                Data.Companion.locationDetails.indoorFloorNumber = textFieldFloorNumber.text.toString()
                dialog.dismiss()
            }
            btnClose.setOnClickListener {
                Data.Companion.locationDetails.indoors = false
                updateButtons(indoor, outdoor, moving, continueBtn)
                Data.Companion.locationDetails.indoorRoomNumber = ""
                Data.Companion.locationDetails.indoorFloorNumber = ""
                dialog.dismiss()
            }
            updateButtons(indoor, outdoor, moving, continueBtn)

            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()

        }

        outdoor.setOnClickListener {
            outdoor.isSelected = true
            indoor.isSelected = false
            moving.isSelected = false
            Data.Companion.locationDetails.outdoors = true
            Data.Companion.locationDetails.indoors = false
            Data.Companion.locationDetails.moving = false
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.outdoor_bottom_sheet_content, null)
            val btnClose = view.findViewById<Button>(R.id.button)


            var textFieldOutdoorDetails = view.findViewById<TextInputEditText>(R.id.outdoorDetails)
            textFieldOutdoorDetails.setText(Data.Companion.locationDetails.outdoorDetails)

            var outdoorLayout = view.findViewById<TextInputLayout>(R.id.outdoorDetailsLayout)
            outdoorLayout.isHintEnabled = false


            val outdoorContinue = view.findViewById<Button>(R.id.outdoor_continue)
            outdoorContinue.setOnClickListener {
                Data.Companion.locationDetails.outdoors = true
                updateButtons(indoor, outdoor, moving, continueBtn)
                Data.Companion.locationDetails.outdoorDetails = textFieldOutdoorDetails.text.toString()
                dialog.dismiss()
            }
            btnClose.setOnClickListener {
                Data.Companion.locationDetails.outdoors = false
                updateButtons(indoor, outdoor, moving, continueBtn)
                Data.Companion.locationDetails.outdoorDetails = ""
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }

        moving.setOnClickListener {
            moving.isSelected = true
            indoor.isSelected = false
            outdoor.isSelected = false
            Data.Companion.locationDetails.moving = true
            Data.Companion.locationDetails.indoors = false
            Data.Companion.locationDetails.outdoors = false
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.moving_bottom_sheet_content, null)
            val btnClose = view.findViewById<Button>(R.id.moving_skip)

            var textFieldBusTrainNumber = view.findViewById<TextInputEditText>(R.id.busTrainNumber)
            var textFieldHighwayExitNumber = view.findViewById<TextInputEditText>(R.id.highwayExitNumber)

            var busLayout = view.findViewById<TextInputLayout>(R.id.busTrainNumberLayout)
            busLayout.isHintEnabled = false

            var highwayLayout = view.findViewById<TextInputLayout>(R.id.highwayExitNumberLayout)
            highwayLayout.isHintEnabled = false

            textFieldBusTrainNumber.setText(Data.Companion.locationDetails.movingBusTrainNumber)
            textFieldHighwayExitNumber.setText(Data.Companion.locationDetails.movingHighwayExitNumber)

            val movingContinue = view.findViewById<Button>(R.id.moving_continue)
            movingContinue.setOnClickListener {
                Data.Companion.locationDetails.moving = true
                updateButtons(indoor, outdoor, moving, continueBtn)
                Data.Companion.locationDetails.movingBusTrainNumber = textFieldBusTrainNumber.text.toString()
                Data.Companion.locationDetails.movingHighwayExitNumber = textFieldHighwayExitNumber.text.toString()
                dialog.dismiss()
            }
            btnClose.setOnClickListener {
                Data.Companion.locationDetails.moving = false
                updateButtons(indoor, outdoor, moving, continueBtn)
                Data.Companion.locationDetails.movingBusTrainNumber = ""
                Data.Companion.locationDetails.movingHighwayExitNumber = ""
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }

        val skip : Button = findViewById(R.id.skipButton)
        skip.setOnClickListener {
            val intent = Intent(this, OptionalEmergencyDetails::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

        }

    }

    fun updateButtons(indoor : Button, outdoor : Button, moving : Button, continueBtn : Button)
    {
        if (Data.Companion.locationDetails.indoors)
        {
            indoor.isSelected = true
            outdoor.isSelected = false
            moving.isSelected = false
            continueBtn.isEnabled = true
            continueBtn.setBackgroundResource(R.drawable.rounded_corner)
        }
        else if (Data.Companion.locationDetails.outdoors)
        {
            outdoor.isSelected = true
            indoor.isSelected = false
            moving.isSelected = false
            continueBtn.isEnabled = true
            continueBtn.setBackgroundResource(R.drawable.rounded_corner)
        }
        else if (Data.Companion.locationDetails.moving)
        {
            moving.isSelected = true
            indoor.isSelected = false
            outdoor.isSelected = false
            continueBtn.isEnabled = true
            continueBtn.setBackgroundResource(R.drawable.rounded_corner)
        }
        else
        {
            moving.isSelected = false
            indoor.isSelected = false
            outdoor.isSelected = false
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