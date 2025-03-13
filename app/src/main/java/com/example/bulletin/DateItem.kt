package com.example.bulletin


import kotlinx.serialization.Serializable
import android.app.appsearch.StorageInfo
@Serializable
class DateItem {
    var date: String? = null
    var eventInfo: String? = null
    constructor(date:String, eventInfo:String){
        this.date = date
        this.eventInfo = eventInfo
    }
}