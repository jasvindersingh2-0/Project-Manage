package com.example.projectmanage.activities.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanage.R
import com.example.projectmanage.activities.models.Board
import com.example.projectmanage.databinding.ItemBoardBinding

open class BoardItemAdaptor(private val context: Context,
    private var list:ArrayList<Board>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MyViewHolder(private val binding: ItemBoardBinding):
        RecyclerView.ViewHolder(binding.root){
        val name = binding.tvItemBoardName
        val createdBy = binding.tvCreator
        val boardImage = binding.ivBoardImage
        fun bindData(id : Board){
            binding.root.setOnClickListener {

            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemBoardBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            Glide.with(context).
                    load(model.image).
                    centerCrop().
                    placeholder(R.drawable.ic_board_place_holder).
                    into(holder.boardImage)
            holder.name.text = model.name
            holder.createdBy.text = "Created By ${model.createdBy}"
            holder.bindData(list[position])
        }
    }

}