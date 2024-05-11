package com.example.mytodolist.database

import androidx.room.TypeConverter
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
        return dateFormat.format(date!!)
    }

    @TypeConverter
    fun toDate(dateString: String?): Date? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateString?.let {
            dateFormat.parse(it)
        }
    }
}