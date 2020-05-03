package com.example.dailycarl.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.dailycarl.R
import com.example.dailycarl.helper.ContextWrapper
import com.example.dailycarl.helper.Preference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GoalActivity : AppCompatActivity() {

    private val TAG: String = "Goal Activity"
    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val goalEx = findViewById<EditText>(R.id.ex_goal_input)
        val goalFood = findViewById<EditText>(R.id.food_goal_input)
        val submitGoal = findViewById<TextView>(R.id.goal_submit)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        submitGoal.setOnClickListener {
            val ex   = goalEx.text.toString().trim { it <= ' ' }
            val food = goalFood.text.toString().trim{ it <= ' '}

            if(ex.isEmpty()) {
                Toast.makeText(applicationContext, "Please enter your activity goal", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Activity goal is empty")
                return@setOnClickListener
            }

            if(food.isEmpty()) {
                Toast.makeText(applicationContext, "Please enter your food goal", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Food goal is empty")
                return@setOnClickListener
            }
            var userId = ""
            var currentUser = mAuth!!.currentUser
            currentUser?.let { userId = currentUser.uid }
            database.child("Users").child(userId).child("defaultFoodGoal").setValue(food)
            database.child("Users").child(userId).child("defaultExGoal").setValue(ex)

            startActivity(Intent(this@GoalActivity, AddChoiceActivity::class.java))
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        preference = Preference(newBase!!)
        val lang = preference.getLoginCount()
        super.attachBaseContext(lang?.let { ContextWrapper.wrap(newBase, it) })
    }
}
