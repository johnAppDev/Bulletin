package com.example.bulletin


import kotlinx.serialization.Serializable
import android.app.appsearch.StorageInfo

class DateItem {

    var date: String? = null
    var eventItems: MutableList<EventItem>? = null


    constructor(date:String, eventItems: MutableList<EventItem>?){

        this.date = date
        this.eventItems = eventItems

    }
}