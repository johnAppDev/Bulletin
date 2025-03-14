package com.example.bulletin

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PossibleEventAdapter(private val eventList: List<EventItem>):RecyclerView.Adapter<PossibleEventAdapter.MyViewHolder>() {
    private  var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.time_select_elements, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val eventItem = eventList[position]
        val timeRange =eventItem.startTime + "-" + eventItem.endTime
        holder.dateName.text = eventItem.date
        holder.timeRange.text = timeRange
        eventItem.editText = holder.titleInput
      //  holder.titleInput.setText(eventItem.title)
        holder.submitButton.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, eventItem)
            }
        }

    }

    override fun getItemCount(): Int {
        return eventList.size

    }
    // Set the click listener for the adapter
    public fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    // Interface for the click listener
    interface OnClickListener {
        fun onClick(position: Int, model: EventItem)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateName: TextView = itemView.findViewById(R.id.Date)
        val timeRange: TextView = itemView.findViewById(R.id.TimeRange)
        val titleInput: EditText = itemView.findViewById(R.id.possibleTitleText)
        val submitButton: Button = itemView.findViewById(R.id.button)
    }

}