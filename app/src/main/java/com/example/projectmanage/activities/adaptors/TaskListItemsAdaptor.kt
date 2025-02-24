package com.example.projectmanage.activities.adaptors

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanage.activities.TaskListActivity
import com.example.projectmanage.activities.models.Task
import com.example.projectmanage.databinding.ItemTaskBinding

open class TaskListItemsAdaptor(private val context:Context,
                                private var list: ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        class MyViewHolder(private val binding : ItemTaskBinding)
            :RecyclerView.ViewHolder(binding.root){
                val addTaskList = binding.tvAddTaskList
                val clTaskItem = binding.clTaskItem

        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false)

        val layoutParams = ConstraintLayout.LayoutParams(
            (parent.width*0.7).toInt(),ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp()).toPx(),0,(40.toDp()).toPx(),0)
//        parent.layoutParams = layoutParams
        binding.root.layoutParams = layoutParams
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size-1){
                holder.addTaskList.visibility = View.VISIBLE
                holder.clTaskItem.visibility = View.GONE
            }
            else{
                holder.addTaskList.visibility = View.GONE
                holder.clTaskItem.visibility = View.VISIBLE
            }
        }
    }
    private fun Int.toDp(): Int =
        (this/Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int =
        (this/Resources.getSystem().displayMetrics.density).toInt()
}