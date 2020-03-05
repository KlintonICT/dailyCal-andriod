package com.example.dailycarl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Login Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val point_to_signup = findViewById<TextView>(R.id.point_to_signup)
        point_to_signup.setOnClickListener{
            val signup_page = Intent(this, SignupActivity::class.java)
            startActivity(signup_page)
        }

        mAuth = FirebaseAuth.getInstance()

//        if(mAuth!!.currentUser != null) {
//            startActivity(Intent(this@MainActivity, GoalActivity::class.java))
//            finish()
//        }

        val email_login_input = findViewById<EditText>(R.id.email_login_input)
        val pass_login_input = findViewById<EditText>(R.id.pass_login_input)
        val login_button = findViewById<TextView>(R.id.login_button)

        login_button.setOnClickListener {
            val email = email_login_input.text.toString().trim { it <= ' '}
            val password = pass_login_input.text.toString().trim { it <= ' '}

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

            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    if(password.length < 6){
                        pass_login_input.error = "Please check your password. Password must have minimum 6 characters."
                        Log.d(TAG, "Enter password less than 6 characters.")
                    }else{
                        Toast.makeText(getApplicationContext(), "Authentication Failed: " + task.exception, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Authentication Failed: " + task.exception)
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Sign in successfully!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Sign in successfully!")
                    startActivity(Intent(this@MainActivity, GoalActivity::class.java))
                    finish()
                }
            }
        }
    }
}
