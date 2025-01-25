package com.example.projectmanage.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.projectmanage.R
import com.example.projectmanage.activities.firebase.FirestoreClass
import com.example.projectmanage.activities.models.Board
import com.example.projectmanage.databinding.ActivityCreateBoardBinding
import models.User
import utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private val binding by lazy {
        ActivityCreateBoardBinding.inflate(layoutInflater)
    }
    private var mSelectedImageFileUri: Uri? = null
    private var mBoardImageUrl : String = ""
    private val uploadPreset = "preset_for_file_upload"

    private lateinit var mUserName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpActionBar()
        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }
        binding.ivCreateBoard.setOnClickListener {
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
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Add custom back press behavior here
                finish() // Close the activity
            }
        })
        binding.btnCreate.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadBoardImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.creating_board))
                createBoard()
            }
        }
    }
    private fun setUpActionBar() {
        setSupportActionBar(binding.tbCbActivity)
        val action = supportActionBar
        if (action != null) {
            action.setDisplayHomeAsUpEnabled(true)
            action.title = resources.getString(R.string.create_board_title)
        }
        binding.tbCbActivity.setNavigationOnClickListener { finish() }

    }
    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.creating_board))
        val imageName = "BoardImage"+System.currentTimeMillis()
        if (mSelectedImageFileUri != null) {
            MediaManager.get().upload(mSelectedImageFileUri)
                .unsigned(uploadPreset).option("folder","board_images")
                .option("public_id",imageName)
                .callback(object : UploadCallback {
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
                        mBoardImageUrl =
                            (resultData?.get("secure_url")as String) // Correct key is "secure_url"
                        Log.d("Cloudinary Image URL",
                            "Uploaded Image URL: $mBoardImageUrl")
                        hideProgressDialog()
                        createBoard()
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
    private fun createBoard(){
        val assignedUserArrayList : ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserID())
        Toast.makeText(this,"Got User Id",Toast.LENGTH_LONG).show()
        val board = Board(
            binding.etCreateBoard.text.toString(),
            mBoardImageUrl,
            mUserName,
            assignedUserArrayList
        )
        FirestoreClass().createBoard(this@CreateBoardActivity,board)
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        finish()
    }
    private val pickImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            mSelectedImageFileUri = result.data?.data
            try {
                // Load selected image into the ImageView using Glide
                Glide
                    .with(this@CreateBoardActivity)
                    .load(mSelectedImageFileUri) // Pass the URI directly
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(binding.ivCreateBoard)
            } catch (e: IOException) {
                e.printStackTrace()
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
}