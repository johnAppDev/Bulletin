package com.example.bulletin

import android.widget.EditText

class EventItem {
    var title: String? = null
    var date: String? = null
    var startTime: String? = null
    var endTime: String? = null
    var location: String? = null
    var publicityType: String? = null
    var invitees: List<String>? = null
    var details: String? = null
    var eventId:String? = null
    var editText: EditText? = null

    constructor(title:String, date:String, startTime:String, endTime:String, location:String, publicityType: String, invitees:List<String>?, details: String, eventId: String ){
        this.title = title
        this.date = date
        this.startTime = startTime
        this.endTime = endTime
        this.location = location
        this.publicityType = publicityType
        this.invitees = invitees
        this.details = details
        this.eventId = eventId

    }
}