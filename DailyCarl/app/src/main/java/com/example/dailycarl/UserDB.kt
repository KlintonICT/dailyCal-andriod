package com.example.dailycarl

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserDB(
    var userID: String? = "",
    var username: String? = ""
)