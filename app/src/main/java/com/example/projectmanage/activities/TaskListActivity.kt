package com.example.projectmanage.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanage.R
import com.example.projectmanage.activities.adaptors.TaskListItemsAdaptor
import com.example.projectmanage.activities.firebase.FirestoreClass
import com.example.projectmanage.activities.models.Board
import com.example.projectmanage.activities.models.Task
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
        val addTaskList = Task(resources.getString(R.string.addList))
        board.taskList.add(addTaskList)
        binding.rvTaskList.layoutManager = LinearLayoutManager(
            this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvTaskList.hasFixedSize()
        val adaptor = TaskListItemsAdaptor(this,board.taskList)
        binding.rvTaskList.adapter = adaptor
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