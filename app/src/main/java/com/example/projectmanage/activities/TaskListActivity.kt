package com.example.projectmanage.activities

import android.os.Bundle
import com.example.projectmanage.R
import com.example.projectmanage.activities.firebase.FirestoreClass
import com.example.projectmanage.activities.models.Board
import com.example.projectmanage.databinding.ActivityTaskListActivtyBinding
import utils.Constants

class TaskListActivity : BaseActivity() {
    private val binding by lazy {
        ActivityTaskListActivtyBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog(resources.getString(R.string.pleaseWait))
        FirestoreClass().getBoardDetails(this,boardDocumentId)

    }
    fun boardDetails(board : Board){
        hideProgressDialog()
        setUpActionBar(board.name)
    }
    private fun setUpActionBar(title : String) {
        setSupportActionBar(binding.tbTaskListActivity)
        val action = supportActionBar
        if (action != null) {
            action.setDisplayHomeAsUpEnabled(true)
            action.title = title
        }
        binding.tbTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }
}