/**
 * Vipawan  Jarukitpipat 6088044
 * Klinton  Chhun        6088111
 *
 * Login Page with authentication
 */
package com.example.dailycarl.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.example.dailycarl.R
import com.example.dailycarl.helper.ContextWrapper
import com.example.dailycarl.helper.Preference

class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Login Activity"
    private lateinit var preference: Preference
    /**
     * @param: savedInstanceState: Bundle?
     *
     * If the user click sign up, it will go to "SignupActivity"
     * If the user click log in, it will check with the Firebase authentication
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pointToSignup = findViewById<TextView>(R.id.point_to_signup)
        pointToSignup.setOnClickListener{
            val signupPage = Intent(this, SignupActivity::class.java)
            startActivity(signupPage) // Go to Signup Activity
        }

        mAuth = FirebaseAuth.getInstance()

//        if(mAuth!!.currentUser != null) {
//            startActivity(Intent(this@MainActivity, GoalActivity::class.java))
//            finish()
//        }

        val emailLoginInput = findViewById<EditText>(R.id.email_login_input)
        val passLoginInput = findViewById<EditText>(R.id.pass_login_input)
        val loginButton = findViewById<TextView>(R.id.login_button)

        loginButton.setOnClickListener {
            val email = emailLoginInput.text.toString().trim { it <= ' '}
            val password = passLoginInput.text.toString().trim { it <= ' '}
            /**Check whether email and password is empty**/
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
            /**Authentication**/
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    if(password.length < 6){
                        passLoginInput.error = "Please check your password. Password must have minimum 6 characters."
                        Log.d(TAG, "Enter password less than 6 characters.")
                    }else{
                        Toast.makeText(applicationContext, "Couldn't find your DailyCarl account" + task.exception, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Authentication Failed: " + task.exception)
                    }
                }else{
                    Toast.makeText(applicationContext, "Sign in successfully!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Sign in successfully!")
                    startActivity(Intent(this@MainActivity, HandleDrawerNav::class.java))
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
