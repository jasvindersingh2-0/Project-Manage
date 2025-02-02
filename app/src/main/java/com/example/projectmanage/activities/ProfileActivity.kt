package com.example.projectmanage.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanage.R
import com.example.projectmanage.activities.firebase.FirestoreClass
import com.example.projectmanage.databinding.ActivityProfileBinding
import com.google.firebase.storage.StorageReference
import models.User
import utils.Constants
import java.io.IOException
import com.cloudinary.*;
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class ProfileActivity : BaseActivity() {

    private val cloudName = "djwijjtul"
    private val uploadPreset = "preset_for_file_upload"
    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl : String = ""
    private lateinit var mUserDetails : User
    private val cloudinary = Cloudinary("cloudinary://922125223919419:NvvOBuUtBIwhgSj4RqB67mQh20c@djwijjtul")




    // View binding for the activity
    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    // Activity Result Launcher for image picking
    private val pickImageLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            mSelectedImageFileUri = result.data?.data
            try {
                // Load selected image into the ImageView using Glide
                Glide
                    .with(this@ProfileActivity)
                    .load(mSelectedImageFileUri) // Pass the URI directly
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivProfile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpActionBar()
        FirestoreClass().loadUserData(this)

        binding.ivProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(pickImageLauncher)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnUpdate.setOnClickListener {
            if(mSelectedImageFileUri != null){
                updateImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.pleaseWait))
            updateUserProfileData()
            }
        }
    }


    // Method to handle permission requests
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(pickImageLauncher)
            } else {
                Toast.makeText(
                    this,
                    "Please allow Storage permission in app settings",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Method to show the image chooser


    // Method to set up the ActionBar
    private fun setUpActionBar() {
        setSupportActionBar(binding.tbProfileActivity)
        val action = supportActionBar
        if (action != null) {
            action.setDisplayHomeAsUpEnabled(true)
            action.title = resources.getString(R.string.my_profile)
        }
        binding.tbProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getFileExtension(uri : Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(contentResolver.getType(uri!!))
    }
    private fun updateImage() {
        showProgressDialog(resources.getString(R.string.updating_data))
        val imageName = "UserImage"+System.currentTimeMillis()+
                "."+getFileExtension(mSelectedImageFileUri)
        if (mSelectedImageFileUri != null) {
            MediaManager.get().upload(mSelectedImageFileUri)
                .unsigned(uploadPreset).option("folder","user_profile")
                .option("public_id",imageName)
                .callback(object : UploadCallback{
                    override fun onStart(requestId: String?) {
                        Log.d("Cloudinary Quickstart", "Upload start")

                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d("Cloudinary Quickstart", "Upload progress")
                    }

                    override fun onSuccess(
                        requestId: String?,
                        resultData: MutableMap<Any?, Any?>?,
                    ) {
                        Log.d("Cloudinary Quickstart", "Upload success")
                        mProfileImageUrl =
                            (resultData?.get("secure_url")as String) // Correct key is "secure_url"
                        Log.d("Cloudinary Image URL",
                            "Uploaded Image URL: $mProfileImageUrl")
//                        Glide
//                            .with(this@ProfileActivity)
//                            .load(mProfileImageUrl)
//                            .centerCrop()
//                            .placeholder(R.drawable.ic_user_place_holder)
//                            .into(binding.ivProfile)
                        hideProgressDialog()
                        updateUserProfileData()
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        hideProgressDialog()
                        Log.d("Cloudinary Quickstart", "Upload failed: ${error?.description}")
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.d("Cloudinary Quickstart", "Upload rescheduled")
                    }

                }).dispatch()
        }
    }


//            val sRef: StorageReference = FirebaseStorage.getInstance().reference
//                .child("UserImage"+System.currentTimeMillis()+"."
//                        +getFileExtension(mSelectedImageFileUri))
//            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
//                taskSnapShot->
//                Log.i("Firebase Image Url",
//                    taskSnapShot.metadata!!.reference!!.downloadUrl.toString()
//                )
//                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
//                    uri->
//                    Log.i("Downloadable image uri",uri.toString())
//                    mProfileImageUrl = uri.toString()
////                    TODO Update User Data
//                    hideProgressDialog()
//                }.addOnFailureListener {
//                    exception->
//                    Toast.makeText(this@ProfileActivity,exception.message
//                    ,Toast.LENGTH_LONG).show()
//                    hideProgressDialog()
//                }

//        }
    fun profileUpdateSuccess(){
        hideProgressDialog()
//        setResult(Activity.RESULT_OK)
        val resultIntent = Intent()
        resultIntent.putExtra(Constants.PROFILE_REQUEST_CODE,
            MainActivity.MY_PROFILE_REQUEST_CODE)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
    fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()
        if(mProfileImageUrl != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageUrl

        }
        if(binding.etName.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding.etName.text.toString()
        }
        if(binding.etMobile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding.etMobile.text.toString().toLong()

        }
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }

    // Method to set user data into the UI
    fun setUserDataInUI(user: User) {
        mUserDetails = user
        Glide
            .with(this@ProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivProfile)

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)

        if (user.mobile != 0L) {
            binding.etMobile.setText(user.mobile.toString())
        }
    }
}
