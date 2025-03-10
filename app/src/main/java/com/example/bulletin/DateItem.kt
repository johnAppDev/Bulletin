package com.example.bulletin

import android.app.appsearch.StorageInfo

class DateItem {
    var date: String? = null
    var eventInfo: String? = null
    constructor(date:String, eventInfo:String){
        this.date = date
        this.eventInfo = eventInfo
    }
}