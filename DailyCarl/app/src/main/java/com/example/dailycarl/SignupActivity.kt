package com.example.dailycarl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Register Activity"
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val point_to_login = findViewById<TextView>(R.id.point_to_login)
        point_to_login.setOnClickListener {
            val login_page = Intent(this, MainActivity::class.java)
            startActivity(login_page)
        }

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

//        if(mAuth!!.currentUser != null) {
//            startActivity(Intent(this@SignupActivity, GoalActivity::class.java))
//            finish()
//        }

        val name_signup = findViewById<EditText>(R.id.name_signup_input)
        val email_signup = findViewById<EditText>(R.id.email_signup_input)
        val pass_signup = findViewById<EditText>(R.id.pass_signup_input)
        val con_pass = findViewById<EditText>(R.id.con_pass_input)
        val signup_button = findViewById<TextView>(R.id.signup_button)

        signup_button.setOnClickListener {
            val username = name_signup.text.toString().trim { it <= ' ' }
            val email = email_signup.text.toString().trim { it <= ' ' }
            val password = pass_signup.text.toString().trim { it <= ' ' }
            val confirm_pass = con_pass.text.toString().trim { it <= ' ' }

            if(username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your name.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Name was empty!")
                return@setOnClickListener
            }
            if(email.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your email address.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Email was empty!")
                return@setOnClickListener
            }
            if(password.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Password was empty!")
                return@setOnClickListener
            }
            if(confirm_pass.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please enter your confirm password.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Confirm password was empty!")
                return@setOnClickListener
            }
            if((!password.isEmpty() && !confirm_pass.isEmpty()) && (password != confirm_pass)){
                Toast.makeText(getApplicationContext(), "Password and Confirm password do not match.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Confirm password doesn't match")
                return@setOnClickListener
            }

            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    if(password.length < 6){
                        pass_signup.error = "Please check your password. Password must have minimum 6 characters."
                        Log.d(TAG, "Enter password less than 6 characters.")
                    }else{
                        Toast.makeText(getApplicationContext(), "Account creating failed" + task.exception, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Fail to create account" + task.exception)
                    }
                }else{
//                    val database = FirebaseDatabase.getInstance()
//                    val ref = database.getReference("UserDB")
//                    val userId = mAuth!!.currentUser!!.uid
//                    ref.child(userId).setValue(UserDB(userId, username))
                    writeNewUser(mAuth!!.currentUser!!.uid, username)

                    Toast.makeText(getApplicationContext(), "Registered successfully!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Register successfully!")
                    startActivity(Intent(this@SignupActivity, GoalActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun writeNewUser(userId: String, username: String) {
        val user = UserDB(userId, username)
        database.child("users").child(userId).setValue(user)
    }
}
