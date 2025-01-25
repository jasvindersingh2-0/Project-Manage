package com.example.projectmanage.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.projectmanage.R
import com.example.projectmanage.activities.firebase.FirestoreClass
import com.example.projectmanage.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import models.User

class SignUpActivity : BaseActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpActionBar()
//        window.navigationBarColor = resources.getColor(R.color.colorPrimary, theme)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                )
//        window.apply {
//            navigationBarColor = android.graphics.Color.TRANSPARENT
//            navigationBarDividerColor = android.graphics.Color.TRANSPARENT
//
//        }
        binding.btnSignIn.setOnClickListener {
            registerUser()
        }

    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.tbSignUp)
        val action = supportActionBar
        if(action != null){
            action.setDisplayHomeAsUpEnabled(true)
            action.title = "SignUp"
        }
        binding.tbSignUp.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerUser(){
        var name = binding.etName.text.toString().trim(' ')
        var email = binding.etEmail.text.toString().trim(' ')
        var password = binding.etPassword.text.toString().trim(' ')

        if(validateForum(name,email,password)){
            showProgressDialog(resources.getString(R.string.signingIn))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).
            addOnCompleteListener {
                    task->
                if(task.isSuccessful){
                    val firebaseUser : FirebaseUser? = task.result!!.user!!
                    val registeredEmail = firebaseUser!!.email
                    val user = User(firebaseUser.uid, name, registeredEmail!!)
                    FirestoreClass().registerUser(this,user)
                }
                else{
                    hideProgressDialog()
                    Toast.makeText(this,"Something went Wrong",
                        Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
    fun userRegisteredSuccess(){
        hideProgressDialog()
        Toast.makeText(this@SignUpActivity,"User have registered successfully" +
                "with us", Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
    private fun validateForum(name : String,email: String,password:String): Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a email address")
                false
            }

            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }
            !email.endsWith("@gmail.com")->{
                showErrorSnackBar("Please Enter Proper email")
                false
            }
            password.length<6 ->{
                showErrorSnackBar("Password length must be 6 letters or long")
                false
            }

            else-> true
        }

    }

}