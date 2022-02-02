package com.accesSOS.text911app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode

import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point

import retrofit2.Call
import retrofit2.Callback
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory

import com.mapbox.mapboxsdk.camera.CameraPosition

// Base Realm Packages
// Realm Authentication Packages

// MongoDB Service Packages
// Utility Packages
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import java.util.regex.Pattern

class MapActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

    private var mapView: MapView? = null
    private lateinit var mapboxMap: MapboxMap
    private lateinit var eligibleTag: TextView
    private lateinit var numberAndStreetLabel: TextView
    private lateinit var cityStateZipLabel: TextView
    private lateinit var confirmLocationButton : Button


    private lateinit var recenterButton: Button

    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2

    private var permissionsManager: PermissionsManager = PermissionsManager(this)

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000


    fun normalizeCounty(input: String) : String {
        return input.lowercase().replace("county", "").trim()
    }

    fun normalizeState(input: String) : String {
        return input.lowercase().replace("us-", "").trim()
    }

    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent(this, SetupCompleteActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
//        finish()
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
                (PermissionChecker.checkSelfPermission(this, permission!!)
                        == PermissionChecker.PERMISSION_GRANTED)
            }
        }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        if(!checkForInternet(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                showPopup()
            }, 1000)
        }

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        //latView = findViewById(R.id.latTextView)
        //longView = findViewById(R.id.longTextView)
        //addressView = findViewById(R.id.addressTextView)
        recenterButton = findViewById(R.id.recenterMap)
        numberAndStreetLabel = findViewById(R.id.numberAndStreetLabel)
        cityStateZipLabel = findViewById(R.id.cityStateZipLabel)
        //currentCounty = findViewById(R.id.currentCounty)

        eligibleTag = findViewById(R.id.eligibleTag)
        eligibleTag.setMovementMethod(LinkMovementMethod.getInstance())


        // Create drop pin using custom image
        val dropPinView = ImageView(this)

        dropPinView.setImageResource(R.drawable.ic_pin_48)

        // Statically Set drop pin in center of screen
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        val density = resources.displayMetrics.density
        params.bottomMargin = (12 * density).toInt()
        params.height = 50
        params.width = 50
        dropPinView.layoutParams = params
        mapView?.addView(dropPinView)

        confirmLocationButton = findViewById(R.id.confimLocationButton)
        confirmLocationButton.setBackgroundResource(R.drawable.unselected_btn)

        confirmLocationButton.setOnClickListener {
            val intent = Intent(this, EmergencyDetailsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
            finish()

        }

        recenterButton.setOnClickListener {
            Log.d("recentering", "recentering")

            var loc = mapboxMap.locationComponent.lastKnownLocation
            val mapLat = loc!!.latitude
            val mapLong = loc.longitude

            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(mapLat, mapLong))
                .zoom(18.0)
                .bearing(0.0)
                .tilt(0.0)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))


            //latView.text = mapLat.toString()
            //longView.text = mapLong.toString()

            Log.d("lat", mapLat.toString())
            Log.d("long", mapLong.toString())

            updateWithSpecifiedCoordinates(mapLong,mapLat)
        }

        doAsync {
            val result = URL(getString(R.string.accessos_backend)).readText()
            uiThread {
                val regex = "\\{.*?\\}.*?\\}(?m)"
                val pattern = Pattern.compile(regex)
                val matcher = pattern.matcher(result)
                while (matcher.find()) {
                    var entry = matcher.group(0)
                    Log.d("entry:", entry)
                    val state_str = "\"State\""
                    val county_str = "\"County\""
                    val text_911_str = "\"TextTo911\""
                    var state = entry.substring(
                        entry.indexOf(state_str) + 9,
                        entry.indexOf(county_str) - 2
                    )
                    var county = entry.substring(
                        entry.indexOf(county_str) + 10,
                        entry.indexOf(text_911_str) - 2
                    )
                    var eligibleCounty = entry.substring(
                        entry.indexOf(text_911_str) + 12,
                        entry.indexOf(text_911_str) + 13
                    )
                    var eligible = eligibleCounty.toInt() != 0



                    state = normalizeState(state)
                    county = normalizeCounty(county)

                    if (Data.Companion.countyData.get(state) == null)
                    {
                        Data.Companion.countyData.put(state, HashMap<String, Boolean>())
                    }
                    if (Data.Companion.countyData.get(state)?.get(county) == null)
                    {
                        Data.Companion.countyData.get(state)?.put(county, eligible)
                    }
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        Data.Companion.countyData.putIfAbsent(state, HashMap<String, Boolean>())
//                        Data.Companion.countyData.get(state)?.putIfAbsent(county, eligible)
//                    }
//                    else
//                    {
//                        if (Data.Companion.countyData.get(state) == null)
//                        {
//                            Data.Companion.countyData.put(state, HashMap<String, Boolean>())
//                        }
//                        if (Data.Companion.countyData.get(state)?.get(county) == null)
//                        {
//                            Data.Companion.countyData.get(state)?.put(county, eligible)
//                        }
//                    }
                }

//                Data.Companion.countyData.put("ca", HashMap<String, Boolean>())
//                Data.Companion.countyData.get("ca")?.put("san francisco", true)
//
//                Data.Companion.countyData.put("ca", HashMap<String, Boolean>())
//                Data.Companion.countyData.get("ca")?.put("san francisco", true)

                val mapLatLng = mapboxMap.cameraPosition.target
                val mapLat = mapLatLng.latitude
                val mapLong = mapLatLng.longitude
                updateWithSpecifiedCoordinates(mapLong,mapLat)
            }
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.Builder().fromUri(getString(R.string.mapbox_style)
            )
        ) {
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments

            // here?
            enableLocationComponent(it)
            if (Data.Companion.locationInfo.gpsLatitude != 0.0)
            {
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(Data.Companion.locationInfo.gpsLatitude, Data.Companion.locationInfo.gpsLongitude))
                    .zoom(18.0)
                    .bearing(0.0)
                    .tilt(0.0)
                    .build()
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
            else
            {
                var loc = mapboxMap.locationComponent.lastKnownLocation
                val mapLat = loc!!.latitude
                val mapLong = loc.longitude
                updateWithSpecifiedCoordinates(mapLong,mapLat)
            }
        }


        mapboxMap.addOnMoveListener(object : MapboxMap.OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                Log.d("move begin", "move begin")

                val mapLatLng = mapboxMap.cameraPosition.target
                val mapLat = mapLatLng.latitude
                val mapLong = mapLatLng.longitude
                //latView.text = mapLat.toString()
                //longView.text = mapLong.toString()
            }

            override fun onMove(detector: MoveGestureDetector) {
                Log.d("moving", "moving")
                val mapLatLng = mapboxMap.cameraPosition.target
                val mapLat = mapLatLng.latitude
                val mapLong = mapLatLng.longitude

                confirmLocationButton.isEnabled = false
                confirmLocationButton.setBackgroundResource(R.drawable.unselected_btn)

                //latView.text = mapLat.toString()
                //longView.text = mapLong.toString()

                //currentCounty.text = "updating"
                //eligibleTag.text = "updating"
            }


            override fun onMoveEnd(detector: MoveGestureDetector) {
                Log.d("move end", "move end")
                Data.Companion.generateSMSString()
                Log.d("smsstring", Data.Companion.smsString)
                updateWithMapViewCoordinates()
                var loc = mapboxMap.locationComponent.lastKnownLocation
                Log.d("my current latitude", loc?.latitude.toString())
                Log.d("my current longitude", loc?.longitude.toString())
            }
        })
    }

    fun updateWithSpecifiedCoordinates(mapLong : Double, mapLat : Double)
    {
        //latView.text = mapLat.toString()
        //longView.text = mapLong.toString()

        // Updating data for SMS String - GPS Coordinates
        Data.Companion.locationInfo.gpsLatitude = mapLat
        Data.Companion.locationInfo.gpsLongitude = mapLong

        val addressLookup =
            MapboxGeocoding.builder().reverseMode(GeocodingCriteria.REVERSE_MODE_DISTANCE)
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(mapLong, mapLat))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build()

        addressLookup.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                Log.d("FAILURE", t.toString())
            }

            override fun onResponse(
                call: Call<GeocodingResponse>,
                response: retrofit2.Response<GeocodingResponse>
            ) {
                if (response.isSuccessful) {
                    val results = response.body()!!.features()
                    if (results.size > 0) {
                        var address = results[0].placeName().toString()
                        var commaLoc = address.indexOf(",")
                        var numberAndStreet = address.substring(0, commaLoc)
                        var cityStateZip = address.substring(commaLoc + 2)

                        numberAndStreetLabel.text = numberAndStreet
                        cityStateZipLabel.text = cityStateZip
                        Data.Companion.locationInfo.address = address
                    }
                    else
                    {
                        numberAndStreetLabel.text = "Address Unavailable"
                        cityStateZipLabel.text = "We're unable to verify this address"
                        Data.Companion.locationInfo.address = "none"
                    }
                }
                else
                {
                    numberAndStreetLabel.text = "Address Unavailable"
                    cityStateZipLabel.text = "We're unable to verify this address"
                    Data.Companion.locationInfo.address = "none"
                }
            }
        })

        val districtLookup =
            MapboxGeocoding.builder().reverseMode(GeocodingCriteria.REVERSE_MODE_SCORE)
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(mapLong, mapLat))
                .geocodingTypes(GeocodingCriteria.TYPE_DISTRICT)
                .build()

        districtLookup.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                Log.d("FAILURE", t.toString())
            }

            override fun onResponse(
                call: Call<GeocodingResponse>,
                response: retrofit2.Response<GeocodingResponse>
            ) {
                if (response.isSuccessful) {
                    val results = response.body()!!.features()
                    Log.d("results2", results.toString())
                    if (results.size > 0) {
                        Log.d("result", results.toString())
                        Log.d("district", results[0].text().toString())

                        var normalizedCounty = normalizeCounty(results[0].text().toString())
                        var normalizedState : String = ""
                        for (context in results[0].context()!!) {
                            if (context.id()?.startsWith("region") == true) {
                                println("\t State: " + context.shortCode())
                                var state = context.shortCode().toString()
                                normalizedState = normalizeState(state)
                                Data.Companion.locationInfo.state = normalizedState
                                Log.d("normalizedState", normalizedState)
                            }
                            Log.d("2 - context.toJson()", context.toJson())
                        }
                        Data.Companion.locationInfo.county = normalizedCounty
                        //eligibleTag.text = Data.Companion.countyData.get(normalizedState)?.get(normalizedCounty).toString()

                        if (Data.Companion.countyData.get(normalizedState)?.get(normalizedCounty) == true)
                        {
                            eligibleTag.isVisible = false
                            confirmLocationButton.isEnabled = true
                            confirmLocationButton.setBackgroundResource(R.drawable.rounded_corner)
                        }
                        else
                        {
                            eligibleTag.isVisible = true
                            confirmLocationButton.isEnabled = false
                            confirmLocationButton.setBackgroundResource(R.drawable.unselected_btn)
                        }
                    }
                    else
                    {
                        eligibleTag.isVisible = true
                        confirmLocationButton.isEnabled = false
                        confirmLocationButton.setBackgroundResource(R.drawable.unselected_btn)
                    }
                }
                else
                {
                    eligibleTag.isVisible = true
                    confirmLocationButton.isEnabled = false
                    confirmLocationButton.setBackgroundResource(R.drawable.unselected_btn)
                }
            }
        })
    }

    fun updateWithMapViewCoordinates() {
        val mapLatLng = mapboxMap.cameraPosition.target
        val mapLat = mapLatLng.latitude
        val mapLong = mapLatLng.longitude
        updateWithSpecifiedCoordinates(mapLong, mapLat)
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.accessRED))
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            //Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show()
            finish()
        }
    }


    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        mapboxMap.removeOnMoveListener(object: MapboxMap.OnMoveListener
        {
            override fun onMoveBegin(detector: MoveGestureDetector) {
            }

            override fun onMove(detector: MoveGestureDetector) {
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        mapboxMap.removeOnMoveListener(object: MapboxMap.OnMoveListener
        {
            override fun onMoveBegin(detector: MoveGestureDetector) {
            }

            override fun onMove(detector: MoveGestureDetector) {
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {
            }
        })
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