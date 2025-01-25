package com.example.projectmanage.activities

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.example.projectmanage.R
import com.example.projectmanage.activities.firebase.FirestoreClass
import com.example.projectmanage.databinding.ActivityMainBinding
import com.example.projectmanage.databinding.AppBarMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import models.User
import utils.Constants

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    companion object{
        private const val MY_PROFILE_REQUEST_CODE : Int = 11
    }
    // In this Activity we bind two layout to Single Activity Below
    private val bindingOne by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val bindingTwo by lazy {
        AppBarMainBinding.inflate(layoutInflater)
    }
    private lateinit var mUserName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCloudinary()
//        setContentView(bindingOne.root)
//        bindingOne.root.addView(bindingTwo.root)
        setContentView(bindingOne.root)
        bindingOne.drawerLayout.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        bindingOne.mainContent.addView(bindingTwo.root)
        FirestoreClass().loadUserData(this)
        setUpActionBar()
        bindingOne.fabCreateBoard.setOnClickListener {
            val intent = Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivity(intent)
        }

        bindingOne.navView.setNavigationItemSelectedListener(this)
    }

    private fun setUpActionBar(){
        setSupportActionBar(bindingTwo.tbMainActivity)
        bindingTwo.tbMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        bindingTwo.tbMainActivity.setNavigationOnClickListener {
            // toggle drawer
            toggleDrawer()
        }

    }
    private fun toggleDrawer(){
        if (bindingOne.drawerLayout.isDrawerOpen(GravityCompat.START)){
            bindingOne.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            bindingOne.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (bindingOne.drawerLayout.isDrawerOpen(GravityCompat.START)){
            bindingOne.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }

    }
    fun updateUserNavigationDetails(user: User){
        val headerView = bindingOne.navView.getHeaderView(0)
        mUserName = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(headerView.findViewById(R.id.nav_user_image))
        headerView.findViewById<TextView>(R.id.tv_username).text = user.name

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        if(requestCode == Activity.RESULT_OK && resultCode == MY_PROFILE_REQUEST_CODE){
            Toast.makeText(this,"Got result for Main",Toast.LENGTH_SHORT).show()
            FirestoreClass().loadUserData(this)
        }
        else{
            Log.e("Cancelled","Cancelled")
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                startActivityForResult(Intent(this,ProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        bindingOne.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun initCloudinary() {

        try {
            MediaManager.get()
        } catch (e: IllegalStateException) {
            val config: HashMap<String, Any> = hashMapOf(
                "cloud_name" to "djwijjtul",
                "api_key" to "922125223919419",
                "api_secret" to "NvvOBuUtBIwhgSj4RqB67mQh20c@djwijjtul",
                "secure" to true
            )
            MediaManager.init(this, config)


        }
    }
}