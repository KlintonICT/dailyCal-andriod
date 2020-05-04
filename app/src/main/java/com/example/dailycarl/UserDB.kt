/**
 * Vipawan  Jarukitpipat 6088044
 * Klinton  Chhun        6088111
 *
 * Format database
 */
package com.example.dailycarl

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserDB(
    var userID: String? = "",
    var username: String? = ""
)