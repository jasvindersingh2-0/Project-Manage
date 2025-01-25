package com.example.projectmanage.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.projectmanage.R
import com.example.projectmanage.activities.firebase.FirestoreClass
import com.example.projectmanage.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import models.User

class SignInActivity : BaseActivity() {
    private val binding by lazy{
        ActivitySignInBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpActionBar()
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                )
        auth = FirebaseAuth.getInstance()
        binding.btnSignIn.setOnClickListener {
            signInUserWithEmail()
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.tbSignIn)
        val action = supportActionBar
        if(action != null){
            action.setDisplayHomeAsUpEnabled(true)
            action.title = "SignIn"
        }
        binding.tbSignIn.setNavigationOnClickListener { onBackPressed() }
    }
    private fun signInUserWithEmail(){
        var email = binding.etEmail.text.toString().trim(' ')
        var password = binding.etPassword.text.toString().trim(' ')
        if(validateForum(email,password)){
            showProgressDialog(resources.getString(R.string.signingIn))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        FirestoreClass().loadUserData(this)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign In", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

    }
    fun signInSuccess(user : User){
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity,MainActivity::class.java))
        finish()
    }
    private fun validateForum(email: String,password:String): Boolean{
        return when{

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