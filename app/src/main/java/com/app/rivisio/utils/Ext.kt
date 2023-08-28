package com.app.rivisio.utils

import com.app.rivisio.ui.add_notes.TextNote
import com.app.rivisio.ui.add_topic.STUDIED_ON
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun TextNote?.stringifyJsonNote(): String {
    val jsonNote = JsonObject()
    if (this == null) {
        jsonNote.addProperty("title", "")
        jsonNote.addProperty("body", "")
    } else {
        jsonNote.addProperty("title", this.heading!!)
        jsonNote.addProperty("body", this.content!!)
    }
    return jsonNote.toString()
}

fun String?.getStudiedOnDateString(): String {
    val inputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault(Locale.Category.FORMAT))
    val currentDate = inputFormatter.format(Date())
    val outputFormatter =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT))
    return outputFormatter.format(currentDate).toString()
}