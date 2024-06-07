package com.example.mytodolist.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converter {
    @TypeConverter
    fun fromTime(time: Time): Long {
        return time.time
    }

    @TypeConverter
    fun toTime(timestamp: Long): Time {
        return Time(timestamp)
    }

    @TypeConverter
    fun fromDate(date: Date?): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return date?.let {
            dateFormat.format(it)
        } ?: ""
    }

    @TypeConverter
    fun toDate(dateString: String?): Date? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return if (dateString.isNullOrEmpty()) {
            null // or return defaultDate if you prefer
        } else {
            dateFormat.parse(dateString)
        }
    }

    @TypeConverter
    fun fromListInt(value: List<Int>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toListInt(value: String): List<Int> {
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }
}