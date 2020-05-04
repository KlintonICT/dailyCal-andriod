/**
 * Vipawan  Jarukitpipat 6088044
 * Klinton  Chhun        6088111
 *
 * Sign up Page for user registration
 */
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
import com.example.dailycarl.database.UserDB
import com.example.dailycarl.helper.ContextWrapper
import com.example.dailycarl.helper.Preference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Register Activity"
    private lateinit var database: DatabaseReference
    lateinit var preference: Preference
    /**
     * @param: savedInstanceState: Bundle?
     *
     * Fill in email, password and confirm password
     * Record in the database for authentication
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val pointToLogin = findViewById<TextView>(R.id.point_to_login)
        pointToLogin.setOnClickListener {
            val loginPage = Intent(this, MainActivity::class.java)
            startActivity(loginPage)
        }

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

//        if(mAuth!!.currentUser != null) {
//            startActivity(Intent(this@SignupActivity, GoalActivity::class.java))
//            finish()
//        }

        val nameSignup = findViewById<EditText>(R.id.name_signup_input)
        val emailSignup = findViewById<EditText>(R.id.email_signup_input)
        val passSignup = findViewById<EditText>(R.id.pass_signup_input)
        val conPass = findViewById<EditText>(R.id.con_pass_input)
        val signupButton = findViewById<TextView>(R.id.signup_button)

        signupButton.setOnClickListener {
            val username = nameSignup.text.toString().trim { it <= ' ' }
            val email = emailSignup.text.toString().trim { it <= ' ' }
            val password = passSignup.text.toString().trim { it <= ' ' }
            val confirmPass = conPass.text.toString().trim { it <= ' ' }

            if(username.isEmpty()) {
                Toast.makeText(applicationContext, "Please enter your name.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Name was empty!")
                return@setOnClickListener
            }
            if(email.isEmpty()) {
                Toast.makeText(applicationContext, "Please enter your email address.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Email was empty!")
                return@setOnClickListener
            }
            if(password.isEmpty()){
                Toast.makeText(applicationContext, "Please enter your password.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Password was empty!")
                return@setOnClickListener
            }
            if(confirmPass.isEmpty()){
                Toast.makeText(applicationContext, "Please enter your confirm password.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Confirm password was empty!")
                return@setOnClickListener
            }
            if((password.isNotEmpty() && confirmPass.isNotEmpty()) && (password != confirmPass)){
                Toast.makeText(applicationContext, "Password and Confirm password do not match.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Confirm password doesn't match")
                return@setOnClickListener
            }
            /**check password with the conditions**/
            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    if(password.length < 6){
                        passSignup.error = "Please check your password. Password must have minimum 6 characters."
                        Log.d(TAG, "Enter password less than 6 characters.")
                    }else{
                        Toast.makeText(applicationContext, "Account creating failed" + task.exception, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Fail to create account" + task.exception)
                    }
                }else{
                    var userId = ""
                    var currentUser = mAuth!!.currentUser
                    currentUser?.let { userId = currentUser.uid }
                    val user = UserDB(email, username, password)
                    database.child("Users").child(userId).setValue(user)
                    Toast.makeText(applicationContext, "Registered successfully!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Register successfully!")
                    startActivity(Intent(this@SignupActivity, GoalActivity::class.java)) //If the sign up is successful go to GoalActivity Page
                    finish()
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        preference = Preference(newBase!!)
        val lang = preference.getLoginCount()
        super.attachBaseContext(lang?.let { ContextWrapper.wrap(newBase, it) })
    }
}
