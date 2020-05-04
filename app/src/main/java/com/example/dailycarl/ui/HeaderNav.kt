/**
 * Vipawan  Jarukitpipat 6088044
 * Klinton  Chhun        6088111
 *
 * Retrieve user name from database and show in navigation bar
 */
package com.example.dailycarl.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dailycarl.R
import com.example.dailycarl.database.UserDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class HeaderNav : AppCompatActivity(){

    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    /**
     * @param: savedInstanceState: Bundle?
     *
     * Retrieve user name from database
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.header_nav)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        var headerUserName = findViewById<TextView>(R.id.header_nav_username)
        var userId = ""
        var currentUser = mAuth!!.currentUser
        currentUser?.let { userId = currentUser.uid }
        database.child("Users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userDB = dataSnapshot.getValue<UserDB>()
                    headerUserName.text = userDB!!.username.toString()
                }
            })
    }
}