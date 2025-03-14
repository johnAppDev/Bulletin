package com.example.bulletin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterClass(private val dataList: List<DataClass>):RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val  itemView = LayoutInflater.from(parent.context).inflate(R.layout.friendicons, parent, false)
        return ViewHolderClass(itemView)
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvTitle.text = currentItem.dataTitle
        holder.itemView.setOnClickListener{
            if (onClickListener != null){
                onClickListener!!.onClick(position, currentItem)
            }
        }
    }
    fun setOnClickListener(listener: OnClickListener?){
        this.onClickListener = listener
    }
    interface OnClickListener{
        fun onClick(position: Int, model: DataClass)
    }
    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView){
        val rvTitle: TextView = itemView.findViewById(R.id.title)

    }
}