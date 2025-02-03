package com.example.projectmanage.activities.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.browser.trusted.TrustedWebActivityCallback
import com.example.projectmanage.activities.CreateBoardActivity
import com.example.projectmanage.activities.MainActivity
import com.example.projectmanage.activities.ProfileActivity
import com.example.projectmanage.activities.SignInActivity
import com.example.projectmanage.activities.SignUpActivity
import com.example.projectmanage.activities.TaskListActivity
import com.example.projectmanage.activities.models.Board
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObject
import models.User
import utils.Constants

class FirestoreClass {
    private val mFireStoreInstance = FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity, userinfo: User){
        mFireStoreInstance.collection(Constants.USERS).
        document(getCurrentUserId()).set(userinfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }
    fun createBoard(activity : CreateBoardActivity,board: Board){
        mFireStoreInstance.collection(Constants.BOARDS)
            .document().set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Board Created Successfully")
                Toast.makeText(activity,
                    "Board Created Successfully",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                exception->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Error Creating Board"
                ,exception)
            }

    }
    fun getBoardDetails(activity: TaskListActivity,documentId : String){
        mFireStoreInstance.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document->
                Log.i(activity.javaClass.simpleName,document.toString())
                activity.boardDetails(document.toObject(Board::class.java)!!)
            }.addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while Creating the Board")

            }
    }
    fun loadUserData(activity: Activity,readBoardsList : Boolean = false){
        mFireStoreInstance.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener { document->
                val loggedInUser = document.toObject(User::class.java)
                when(activity){
                    is SignInActivity->{
                        activity.signInSuccess(loggedInUser!!)
                    }
                    is MainActivity->{
                        activity.updateUserNavigationDetails(loggedInUser!!,readBoardsList)
                    }
                    is ProfileActivity->{
                        activity.setUserDataInUI(loggedInUser!!)
                    }
                }


            }
            .addOnFailureListener { e ->
                when(activity){
                    is SignInActivity->{
                        activity.hideProgressDialog()
                        Log.e(
                            "SignInError",
                            "Error reading document",
                            e
                        )
                    }
                    is MainActivity->{
                        activity.hideProgressDialog()
                        Log.e(
                            "SignInError",
                            "Error reading document",
                            e
                        )
                    }
                }

            }
    }
    fun getCurrentUserId(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser != null){
            currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        }
        return currentUserId
    }
    fun getBoardsList(activity:MainActivity){
        mFireStoreInstance.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document->
                Log.i(activity.javaClass.simpleName,document.documents.toString())
                val boardList : ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoarsListOnUi(boardList)
            }.addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while Creating the Board")

            }
    }
    fun updateUserProfileData(activity: ProfileActivity
                              ,userHashMap : HashMap<String,Any>){
        mFireStoreInstance.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile Data Updated Successfully")
                Toast.makeText(activity,
                    "Profile Updated Successfully",Toast.LENGTH_SHORT).show()
                
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName,"Error while Updating Profile")
                Toast.makeText(activity,
                    "Error Updating Profile",Toast.LENGTH_SHORT).show()
                activity.hideProgressDialog()
            }

    }
}