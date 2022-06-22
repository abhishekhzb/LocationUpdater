package com.andorid.locationupdater

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    lateinit var latLngTextView: TextView
    lateinit var addressTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.startBtn)
        latLngTextView = findViewById(R.id.textLocation)
        addressTextView = findViewById(R.id.textAddress)

        button.setBackgroundColor(Color.BLUE)
        button.setOnClickListener {
            if (button.text == "START") {
                button.text = "STOP"
                button.setBackgroundColor(Color.RED)
                callLocationLooper()
            } else {
                button.text = "START"
                button.setBackgroundColor(Color.BLUE)
                handler.removeCallbacksAndMessages(null)
            }
        }
    }

    //calling location every 10 secs interval time
    val handler = Handler()
    private fun callLocationLooper() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                //Call your function here
                getLastKnownLocation(applicationContext)
                handler.postDelayed(this, 10000)  //10 sec delay
            }
        }, 0)
    }

    //getting last known location
    fun getLastKnownLocation(context: Context) {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.getProviders(true)
        var location: Location? = null
        for (i in providers.size - 1 downTo 0) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            location = locationManager.getLastKnownLocation(providers[i])
            if (location != null)
                break
        }
        val gps = DoubleArray(2)
        if (location != null) {
            gps[0] = location.getLatitude()
            gps[1] = location.getLongitude()
            Log.e("gpsLat", gps[0].toString())
            Log.e("gpsLong", gps[1].toString())
            latLngTextView.text = "Last Location \n\nLatitude: " + gps[0] + "\nLongitude: " + gps[1]
            getAddressInfo(gps[0],gps[1])
            Toast.makeText(this,"Updated",Toast.LENGTH_SHORT).show()
        }
    }

    //getting address from lat lng
    private fun getAddressInfo(latitude:Double, longitude:Double){
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)

        val address: String = addresses[0].getAddressLine(0)
        val city: String = addresses[0].locality
        val state: String = addresses[0].adminArea
        val country: String = addresses[0].countryName
        val postalCode: String = addresses[0].postalCode
        val knownName: String = addresses[0].featureName
        Log.e("ADDRESS",""+address)

        addressTextView.setText(address)
    }
}
