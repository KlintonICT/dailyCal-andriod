package com.example.dailycarl.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailycarl.R
import com.example.dailycarl.database.RecycleviewDB
import kotlinx.android.synthetic.main.activity_recycleview_items.view.*


class RecycleViewAdapter(private val recycleList: List<RecycleviewDB>)  : RecyclerView.Adapter<RecycleViewAdapter.RecycleViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_recycleview_items, parent, false)
        return RecycleViewHolder(
            itemView
        )
    }

    override fun getItemCount() = recycleList.size

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        val currentItem = recycleList[position]
        holder.imageAct.setImageResource(currentItem.imageResource)
        holder.actTitle.text = currentItem.activityTitle
        holder.actData.text  = currentItem.activityData
        holder.cal.text      = currentItem.calories
        holder.place.text    = currentItem.place
    }

    class RecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageAct : ImageView = itemView.recycle_item_image
        val actTitle : TextView  = itemView.recycle_item_act_title
        val actData  : TextView  = itemView.recycle_item_act_data
        val cal      : TextView  = itemView.recycle_item_cal_data
        val place    : TextView  = itemView.recycle_item_place_data
        val delete   : TextView  = itemView.delete
    }
}