package com.accesSOS.text911app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class EmergencyLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_location)
        supportActionBar?.hide()

        val button: Button = findViewById(R.id.button_confirm_location)

        button.setOnClickListener {
            Log.d("test", "test")
            val intent = Intent(this, EmergencyDetailsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
            //setContentView(R.layout.activity_location_access)
            // Do something in response to button click
        }

    }
}