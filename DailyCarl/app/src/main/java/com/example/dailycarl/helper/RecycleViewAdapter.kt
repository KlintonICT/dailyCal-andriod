package com.example.dailycarl.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailycarl.R
import com.example.dailycarl.database.RecycleviewDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_recycleview_items.view.*


class RecycleViewAdapter(private val recycleList: ArrayList<RecycleviewDB>)  : RecyclerView.Adapter<RecycleViewAdapter.RecycleViewHolder>(){

    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_recycleview_items, parent, false)
        return RecycleViewHolder(itemView)
    }

    override fun getItemCount() = recycleList.size

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        val currentItem = recycleList[position]
        holder.imageAct.setImageResource(currentItem.imageResource)
        holder.actTitle.text = currentItem.activityTitle
        holder.actData.text  = currentItem.activityData
        holder.cal.text      = currentItem.calories
        holder.place.text    = currentItem.place

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        var userId = ""
        var currentUser = mAuth!!.currentUser
        currentUser?.let { userId = currentUser.uid }
        holder.delete.setOnClickListener {
            database.child("Users").child(userId).child("usersActivity").child(recycleList[position].actId).removeValue()
            recycleList.removeAt(position)
            notifyDataSetChanged()
        }
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