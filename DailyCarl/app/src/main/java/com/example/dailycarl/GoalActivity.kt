package com.example.dailycarl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class GoalActivity : AppCompatActivity() {

    private val TAG: String = "Goal Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val goal_ex = findViewById<EditText>(R.id.ex_goal_input)
        val goal_food = findViewById<EditText>(R.id.food_goal_input)
        val submit_goal = findViewById<TextView>(R.id.goal_submit)

        submit_goal.setOnClickListener {
            val ex_goal = Integer.parseInt(goal_ex.text.toString())
            val food_goal = Integer.parseInt(goal_food.text.toString())

            if(goal_ex.text.toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your activity goal", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Activity goal is empty")
                return@setOnClickListener
            }

            if(goal_ex.text.toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your food goal", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Food goal is empty")
                return@setOnClickListener
            }

            startActivity(Intent(this@GoalActivity, AddChoiceActivity::class.java))
            finish()
        }
    }
}
