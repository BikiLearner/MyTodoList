package com.example.mytodolist.database.dataClass

import java.util.Date

data class RepeatModel(
    val every: Int = 1,
    val weekDayMonthOrYear: String = "Week",
    val weekDay: List<Int> = emptyList(),
    val never: Boolean = false,
    val on: Boolean = false,
    val after: Boolean = false,
    val onDate: Date? = null,
    val afterOccurrence: Int = 0
)
