package com.example.dailycarl.database

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import java.io.IOException
import java.util.*

object GeoLocation {
    fun getAddress(
        locationAddress: String,
        context: Context?,
        handler: Handler?
    ) {
        val thread: Thread = object : Thread() {
            override fun run() {
                val geoCoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList: List<*>? =
                        geoCoder.getFromLocationName(locationAddress, 1)
                    if (addressList != null && addressList.isNotEmpty()) {
                        val address = addressList[0] as Address
                        val stringBuilder = StringBuilder()
                        stringBuilder.append(address.latitude).append(", ")
                        stringBuilder.append(address.longitude)
                        result = stringBuilder.toString()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    if (result != null) {
                        message.what = 1
                        val bundle = Bundle()
                        result = "$result"
                        bundle.putString("address", result)
                        message.data = bundle
                    }
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }

    operator fun invoke() {}
}