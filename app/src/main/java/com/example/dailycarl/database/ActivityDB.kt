package com.example.dailycarl.database

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ActivityDB(
    var menu                : String? = "",
    var activityCalory      : String? = "",
    var type                : String? = "",
    var location            : String? = "",
    var date                : String? = "",
    var goal                : String? = "",
    var activityPic         : String? = ""
)