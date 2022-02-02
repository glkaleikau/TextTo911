package com.accesSOS.text911app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash_screen)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

// we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            var intent = Intent(this, MapActivity::class.java)
            if(!selfPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
                intent = Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 500) // 3000 is the delayed time in milliseconds.
    }

    fun selfPermissionGranted(permission: String): Boolean {
        // For Android < Android M, self permissions are always granted.
        var result = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = if (android.R.attr.targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                (this.checkSelfPermission(permission)
                        === PackageManager.PERMISSION_GRANTED)
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                (PermissionChecker.checkSelfPermission(this, permission)
                        == PermissionChecker.PERMISSION_GRANTED)
            }
        }
        return result
    }
}