package com.example.bulletin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CalendarAdapter(private val dateList: List<DateItem>): RecyclerView.Adapter<CalendarAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_icon, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dateItem = dateList[position]
        holder.dateName.text = dateItem.date

    }

    override fun getItemCount(): Int {
        return dateList.size

    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateName: TextView = itemView.findViewById(R.id.date_display)
    }

}