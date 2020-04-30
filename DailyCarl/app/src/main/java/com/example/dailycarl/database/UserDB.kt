package com.example.dailycarl.database

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserDB(
    var userEmail       :   String? = "",
    var username        :   String? = "",
    var userPass        :   String? = "",
    var defaultFoodGoal :   String? = "",
    var defaultExGoal   :   String? = "",
    var profilePic      :   String? = ""
)