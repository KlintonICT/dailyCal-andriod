package com.example.dailycarl.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.dailycarl.R
import com.example.dailycarl.database.UserDB
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SettingActivity : Fragment() {

    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private var imageView: ImageView? = null
    private val GALLERY = 1
    private val CAMERA = 2
    lateinit var capturePhotoPath: String

    companion object {
        fun newInstance(): Fragment {
            return SettingActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val language_choose = ArrayAdapter.createFromResource(this,
//            R.array.language_list, android.R.layout.simple_spinner_dropdown_item)
//        language_choose.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_setting, container, false)
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
                                            Toast.makeText(activity, "Updated Successfully.", Toast.LENGTH_SHORT).show()
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
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {it ->
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        activity!!.applicationContext,
//                        "com.example.android.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, CAMERA)
//                }
//            }
//        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, contentURI)
//                    saveImage(bitmap)
                    imageView!!.setImageBitmap(bitmap)
                }
                catch (e: IOException)
                {
                    e.printStackTrace()
                }
            }
        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageView!!.setImageBitmap(thumbnail)
//            saveImage(thumbnail)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            capturePhotoPath = "file:$absolutePath"
        }
    }

//    private fun saveImage(myBitmap: Bitmap):String {
//        val bytes = ByteArrayOutputStream()
//        myBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
//        val wallpaperDirectory = File (
//            (Environment.getExternalStorageDirectory()).toString() + "/Photos")
//        Log.d("fee", wallpaperDirectory.toString())
//        if (!wallpaperDirectory.exists())
//        {
//            wallpaperDirectory.mkdirs()
//        }
//        try
//        {
//            Log.d("heel", wallpaperDirectory.toString())
//            val f = File(wallpaperDirectory, ((Calendar.getInstance()
//                .timeInMillis).toString() + ".png"))
//            f.createNewFile()
//            val fo = FileOutputStream(f)
//            fo.write(bytes.toByteArray())
//            MediaScannerConnection.scanFile(activity, arrayOf(f.path), arrayOf("image/png"), null)
//            fo.close()
//            Log.d("TAG", "File Saved::--->" + f.absolutePath)
//
//            return f.absolutePath
//        }
//        catch (e1: IOException){
//            e1.printStackTrace()
//        }
//        return ""
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
