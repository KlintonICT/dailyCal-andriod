package com.example.dailycarl.ui

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dailycarl.R
import com.example.dailycarl.database.GeoLocation
import com.example.dailycarl.helper.ContextWrapper
import com.example.dailycarl.helper.Preference
import com.example.dailycarl.database.GeoLocation.getAddress
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(){

    private var mMap    : GoogleMap?            = null
    var mapFragment     : SupportMapFragment?   = null
    var locationInput   : EditText?             = null
    var locationResult  : TextView?             = null
    var searchBtn       : TextView?             = null
    var locationSubmit  : TextView?             = null
    var tempLoc = ""
    private lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)

        locationInput   = findViewById(R.id.location_page_search)
        locationResult  = findViewById(R.id.location_page_result)
        searchBtn       = findViewById(R.id.location_page_searchBtn)
        locationSubmit  = findViewById(R.id.location_page_submit)

        searchBtn!!.setOnClickListener {
            val input = locationInput!!.text.toString().trim { it <= ' ' }
            GeoLocation()
            getAddress(input, applicationContext, GeoHandler())
        }

        val activityType = intent.getStringExtra("activityType")
        locationSubmit!!.setOnClickListener {
            if(tempLoc.isEmpty()) {
                return@setOnClickListener
            }
            val intent = Intent(this@MapActivity, EatAndExActivity::class.java)
            intent.putExtra("activityTypeTo", activityType)
            intent.putExtra("location", tempLoc)
            startActivity(intent)
            finish()
        }
    }

    private inner class GeoHandler : Handler(), OnMapReadyCallback {
        override fun handleMessage(msg: Message) {
            val address: String?
            address = when (msg.what) {
                1 -> {
                    val bundle = msg.data
                    bundle.getString("address")
                }
                else -> null
            }
            locationResult!!.text = address
            mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }

        override fun onMapReady(googleMap: GoogleMap?) {
            mMap = googleMap
            val mGPS: String = (findViewById<TextView>(R.id.location_page_result)).text.toString()
            val splitWord = mGPS?.split(",")?.toTypedArray()
            val currentLoc = LatLng(splitWord?.get(0)?.toDouble(), splitWord?.get(1)?.toDouble())
            mMap!!.addMarker(MarkerOptions().position(currentLoc).title("Current Position"))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,16F))
            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(17F),1500,null)
            splitWord?.get(0)?.toDouble()
            splitWord?.get(1)?.toDouble()
            val geoCoder = Geocoder(applicationContext)
            val list = splitWord?.get(0)?.toDouble()?.let {
                geoCoder.getFromLocation(it, splitWord[1].toDouble(),1) }
            if (list != null) {
                val address: String = list[0].getAddressLine(0)
                locationResult!!.text = " $address"
                tempLoc = " $address"
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        preference = Preference(newBase!!)
        val lang = preference.getLoginCount()
        super.attachBaseContext(lang?.let { ContextWrapper.wrap(newBase, it) })
    }
}
