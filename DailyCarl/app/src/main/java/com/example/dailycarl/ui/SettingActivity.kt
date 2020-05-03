package com.example.dailycarl.ui

import android.util.Base64
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.dailycarl.R
import com.example.dailycarl.database.UserDB
import com.example.dailycarl.helper.Preference
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.io.ByteArrayOutputStream
import java.io.IOException

class SettingActivity : Fragment() {

    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private var imageView: ImageView? = null
    private val GALLERY = 1
    private val CAMERA = 2
    var capturePhotoPath = ""
    lateinit var preference: Preference

    val languageList = arrayOf("en", "th")

    companion object {
        fun newInstance(): Fragment {
            return SettingActivity()
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_setting, container, false)
        val context = activity!!.applicationContext
        preference = Preference(activity!!.applicationContext)
        val spinner = view.findViewById<Spinner>(R.id.language_list)
        val lang = preference.getLoginCount()
        val index = languageList.indexOf(lang)
        if(index >= 0){
            spinner.setSelection(index)
        }
        spinner.adapter = ArrayAdapter.createFromResource(context, R.array.language_list, android.R.layout.simple_spinner_dropdown_item)

        imageView = view.findViewById(R.id.profile_view)
        imageView!!.setOnClickListener{ showPictureDialog() }
        var settingNameInput  = view.findViewById<EditText>(R.id.setting_name_input)
        var settingEmailInput = view.findViewById<EditText>(R.id.setting_email_input)
        val updateBtn         = view.findViewById<TextView>(R.id.setting_update)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val currentUser = mAuth!!.currentUser
        var userId = ""
        currentUser?.let { userId = currentUser.uid }

        database.child("Users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userDB = dataSnapshot.getValue<UserDB>()
                    if(userDB!=null && userDB.userEmail.toString().isNotEmpty()){
                        settingEmailInput.setText(userDB.userEmail.toString())
                    }
                    if(userDB!=null && userDB.username.toString().isNotEmpty()){
                        settingNameInput.setText( userDB.username.toString() )
                    }
                    if(userDB!!.profilePic.toString().isNotEmpty()){
                        capturePhotoPath = userDB!!.profilePic.toString()
                        val img = Base64.decode(capturePhotoPath, Base64.DEFAULT)
                        val image = BitmapFactory.decodeByteArray(img, 0, img.size)
                        imageView!!.setImageBitmap(image)
                    }
                }
            })

        updateBtn.setOnClickListener {
            val name  = settingNameInput.text.toString()
            val email = settingEmailInput.text.toString()
            Toast.makeText(activity, "Waiting for updating...", Toast.LENGTH_LONG).show()
            if(name.isEmpty()) { Toast.makeText(activity, "Please enter your name.", Toast.LENGTH_SHORT).show(); return@setOnClickListener}
            if(email.isEmpty()) { Toast.makeText(activity, "Please enter your email address.", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            database.child("Users").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userDB = dataSnapshot.getValue<UserDB>()
                        if(userDB != null && userDB.userEmail.toString().isNotEmpty() && userDB.userPass.toString().isNotEmpty()){
                            val credential = EmailAuthProvider
                                .getCredential(userDB.userEmail.toString(), userDB.userPass.toString())
                            currentUser!!.reauthenticate(credential)
                                .addOnCompleteListener {
                                    val newCurrentUser = FirebaseAuth.getInstance()!!.currentUser
                                    newCurrentUser!!.updateEmail(email).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            database.child("Users").child(userId).child("username").setValue(name)
                                            database.child("Users").child(userId).child("userEmail").setValue(email)
                                            database.child("Users").child(userId).child("profilePic").setValue(capturePhotoPath)
                                            Toast.makeText(activity, "Updated Successfully.", Toast.LENGTH_SHORT).show()
                                            preference.setLoginCount(languageList[spinner.selectedItemPosition])
                                            startActivity(Intent(activity, HandleDrawerNav::class.java))
                                        }else{
                                            Toast.makeText(activity, "Updating Fail", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                        }
                    }
                })
        }

        return view
    }
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity)
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
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, contentURI)
                    saveImage(bitmap)
                    imageView!!.setImageBitmap(bitmap)
                }
                catch (e: IOException) { e.printStackTrace() }
            }
        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageView!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
        }
    }

    private fun saveImage(myBitmap: Bitmap) {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
        capturePhotoPath = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT)
    }

}
