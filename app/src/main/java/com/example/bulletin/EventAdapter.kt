package com.example.bulletin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter( private val eventList: List<EventItem>): RecyclerView.Adapter<EventAdapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null
    override  fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_icon, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        val eventItem = eventList[position]
        val timeText = "${eventItem.startTime} \n - ${eventItem.endTime}"
        holder.eventTitle.text = eventItem.title
        holder.timeRangeDisplay.text =  timeText
    }
    override fun getItemCount(): Int{
        return eventList.size
    }
    fun setOnClickListener(listener: OnClickListener? ){
       this.onClickListener = listener
    }
    interface  OnClickListener{
        fun onClick(position: Int, model: EventItem)
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val timeRangeDisplay:TextView = itemView.findViewById(R.id.EventTime)
        val eventTitle:TextView = itemView.findViewById(R.id.EventDetails)
    }

}