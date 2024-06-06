package com.example.mytodolist.reusePackage

import android.app.DatePickerDialog
import android.content.Context
import android.text.format.DateFormat
import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar

fun datePicker(callback: (Calendar) -> Unit, context: Context) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            callback(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
    datePickerDialog.show()
}

fun showTimePickerDialog(
    callback: (Long) -> Unit,
    supportFragmentManager: FragmentManager,
    context: Context
) {
    val calendar = android.icu.util.Calendar.getInstance()
    val hour = calendar.get(android.icu.util.Calendar.HOUR_OF_DAY)
    val minute = calendar.get(android.icu.util.Calendar.MINUTE)
    val isSystem24Hour = DateFormat.is24HourFormat(context)
    val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
    val picker =
        MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(hour)
            .setMinute(minute)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setTitleText("Set Task Remind me")
            .build()

    picker.show(supportFragmentManager, "timePicker")

    picker.addOnPositiveButtonClickListener {
        // Calculate milliseconds from the selected time
        val selectedTimeMillis = android.icu.util.Calendar.getInstance().apply {
            set(android.icu.util.Calendar.HOUR_OF_DAY, picker.hour)
            set(android.icu.util.Calendar.MINUTE, picker.minute)
            set(android.icu.util.Calendar.SECOND, 0)
            set(android.icu.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

        callback(selectedTimeMillis)
    }
}
