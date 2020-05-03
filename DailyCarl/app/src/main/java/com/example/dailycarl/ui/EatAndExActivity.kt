package com.example.dailycarl.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dailycarl.R
import com.example.dailycarl.database.ActivityDB
import com.example.dailycarl.database.UserDB
import com.example.dailycarl.helper.ContextWrapper
import com.example.dailycarl.helper.Preference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class EatAndExActivity : AppCompatActivity() {

    private var imageView: ImageView? = null
    private val GALLERY = 1
    private val CAMERA = 2
    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private lateinit var menu   : EditText
    private lateinit var calory : EditText
    private lateinit var location: EditText
    private lateinit var okBtn  : TextView
    lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat_and_ex)

        val activityType:String = intent.getStringExtra("activityType")
        val activityBar         = findViewById<TextView>(R.id.eat_and_act_tab_bar)
        val backBtn             = findViewById<ImageView>(R.id.left_arrow)
        val pickDay             = findViewById<TextView>(R.id.date_act_picker)
            menu                = findViewById(R.id.menu_input)
            calory              = findViewById(R.id.cal_act_input)
            location            = findViewById(R.id.location_input)
            okBtn               = findViewById(R.id.ok_act_btn)
        imageView               = findViewById(R.id.activity_image)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        imageView!!.setOnClickListener{ showPictureDialog() }

        backBtn.setOnClickListener{ startActivity(Intent(this@EatAndExActivity, HandleDrawerNav::class.java)) }

        if( activityType != null && activityType == "eat" ){
            activityBar.text = getString(R.string.eat_activity_exfood)
        }else if(activityType != null && activityType == "ex"){
            activityBar.text = getString(R.string.ex_activity_exfood)
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        pickDay.setOnClickListener {
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                pickDay.text = "" + mDay + "/" + (mMonth+1) + "/" + mYear
                handleOkayButton("" + mDay + "/" + (mMonth+1) + "/" + mYear, activityType)
            }, year, month, day).show()
        }
    }

    private fun handleOkayButton(date: String, actType: String) {
        okBtn.setOnClickListener {
            val menuField      = menu.text.toString().trim{ it <= ' ' }
            val caloryField    = calory.text.toString().trim{ it <= ' ' }
            val locationField = location.text.toString()
            if(menuField.isEmpty()){
                Toast.makeText(applicationContext, "Please enter your activity.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(caloryField.isEmpty()){
                Toast.makeText(applicationContext, "Please enter calory for this activity.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var userId = ""
            var currentUser = mAuth!!.currentUser
            currentUser?.let { userId = currentUser.uid }
            database.child("Users").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userDB = dataSnapshot.getValue<UserDB>()
                        var defaultGoal = ""
                        if(userDB != null && actType == "ex"){
                            defaultGoal = userDB.defaultExGoal.toString()
                        }
                        if(userDB != null && actType == "eat"){
                            defaultGoal = userDB.defaultFoodGoal.toString()
                        }
                        val activity = ActivityDB(menuField, caloryField, actType, locationField, date, defaultGoal)
                        database.child("Users").child(userId).child("usersActivity").push().setValue(activity)
                    }
                })
            startActivity(Intent(this@EatAndExActivity, HandleDrawerNav::class.java))
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Choose photo from...")
        val pictureDialogItems = arrayOf("Gallery", "Camera")
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> chooseImageFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun chooseImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    saveImage(bitmap)
                    imageView!!.setImageBitmap(bitmap)
                }
                catch (e: IOException)
                {
                    e.printStackTrace()
                    Toast.makeText(this@EatAndExActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageView!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
        }
    }

    private fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
        val wallpaperDirectory = File (
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY
        )
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }
        try
        {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .timeInMillis).toString() + ".png"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/png"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        }
        catch (e1: IOException){
            e1.printStackTrace()
        }
        return ""
    }

    companion object {
        private const val IMAGE_DIRECTORY = "/Photos"
    }

    override fun attachBaseContext(newBase: Context?) {
        preference = Preference(newBase!!)
        val lang = preference.getLoginCount()
        super.attachBaseContext(lang?.let { ContextWrapper.wrap(newBase, it) })
    }
}