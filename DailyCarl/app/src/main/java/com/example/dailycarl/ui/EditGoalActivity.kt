package com.example.dailycarl.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailycarl.R
import com.example.dailycarl.database.ActivityDB
import com.example.dailycarl.database.UserDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.util.*

class EditGoalActivity : Fragment() {

    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference

    companion object {
        fun newInstance(): Fragment {
            return EditGoalActivity()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.activity_edit_goal, container, false)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val editGoalEx   = viewRoot.findViewById<EditText>(R.id.editGoal_ex_input)
        val editGoalFood = viewRoot.findViewById<EditText>(R.id.editGoal_food_input)
        val updateBtn    = viewRoot.findViewById<TextView>(R.id.editGoal_update_btn)
        val c       = Calendar.getInstance()
        val year    = c.get(Calendar.YEAR)
        val month   = c.get(Calendar.MONTH)
        val day     = c.get(Calendar.DAY_OF_MONTH)
        val currentDate = "" + day + "/" + (month+1) + "/" + year
        var userId = ""
        var currentUser = mAuth!!.currentUser
        currentUser?.let { userId = currentUser.uid }
        database.child("Users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userDB = dataSnapshot.getValue<UserDB>()
                    if(userDB != null){
                        editGoalEx.setText(userDB.defaultExGoal.toString())
                        editGoalFood.setText(userDB.defaultFoodGoal.toString())
                    }
                }
            })
        updateBtn.setOnClickListener {
            val goalEx   = editGoalEx.text.toString().trim{ it <= ' ' }
            val goalFood = editGoalFood.text.toString().trim{ it <= ' ' }
            if(goalFood.isNotEmpty()){
                database.child("Users").child(userId).child("defaultFoodGoal").setValue(goalFood)
                database.child("Users").child(userId).child("usersActivity")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for(snapShot in dataSnapshot.children){
                                val activityId = snapShot.key.toString()
                                val activityDB = snapShot.getValue<ActivityDB>()
                                if(activityDB != null && activityDB.type.toString() == "eat" && activityDB.date.toString() == currentDate){
                                    database.child("Users").child(userId).child("usersActivity").child(activityId).child("goal").setValue(goalFood)
                                }
                            }
                        }
                    })
            }
            if(goalEx.isNotEmpty()){
                database.child("Users").child(userId).child("defaultExGoal").setValue(goalEx)
                database.child("Users").child(userId).child("usersActivity")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for(snapShot in dataSnapshot.children){
                                val activityId = snapShot.key.toString()
                                val activityDB = snapShot.getValue<ActivityDB>()
                                if(activityDB != null && activityDB.type.toString() == "ex" && activityDB.date.toString() == currentDate){
                                    database.child("Users").child(userId).child("usersActivity").child(activityId).child("goal").setValue(goalEx)
                                }
                            }
                        }
                    })
            }
            startActivity(Intent(activity, HandleDrawerNav::class.java))
        }
        return viewRoot
    }
}