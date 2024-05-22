package com.example.mytodolist.Ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mytodolist.R
import com.example.mytodolist.database.TodoViewModel

import com.example.mytodolist.enums.IntentPassEnum
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class AddNewTask : AppCompatActivity() {
    private lateinit var descriptionEditText: EditText
    private lateinit var taskNameEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var startTimeEdittext: TextView
    private lateinit var categoryNameAutoCompleteTextView: AutoCompleteTextView
    private lateinit var endTimeEditText: TextView
    private lateinit var date: Date
    private lateinit var startTime: Time
    private lateinit var endTime: Time
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var deleteButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val todoId = intent.getLongExtra(IntentPassEnum.TASKID.name, 0)
        val isEdit = intent.getBooleanExtra(IntentPassEnum.ISEDIT.name, false)

        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        descriptionEditText = findViewById(R.id.editTextDescription)
        taskNameEditText = findViewById(R.id.taskNameEditText)
        dateTextView = findViewById(R.id.DateAndTimeEditText)
        startTimeEdittext = findViewById(R.id.startTimeEditText)
        endTimeEditText = findViewById(R.id.endTimeEditText)
        deleteButton = findViewById(R.id.deleteTaskButtonEditTask)
        categoryNameAutoCompleteTextView = findViewById(R.id.CategoryNameEditText)
        val backButton = findViewById<ImageButton>(R.id.backButtonAddTask)
        val saveButton = findViewById<MaterialButton>(R.id.saveOrEditButton)
        val toolbarTaskAdd = findViewById<TextView>(R.id.toolBarTitleAddOrEditTask)



        deleteButton.visibility = View.GONE
        if (isEdit) {
            setDataToViews(todoId)
            saveButton.text = "Edit Done"
            toolbarTaskAdd.text = "Edit Task"
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        dateTextView.setOnClickListener {
            datePicker()
        }

        saveButton.setOnClickListener {
            createNewTask(todoId)
        }


        startTimeEdittext.setOnClickListener {
            showTimePickerDialog("Start") { time ->
                // Extract hour and minute from the Time object
                val hour = time.hours
                val minute = time.minutes
                // Set the text of startTimeEditText with formatted time
                startTime = time
                startTimeEdittext.text = String.format("%02d:%02d", hour, minute)
            }
        }


        endTimeEditText.setOnClickListener {
            showTimePickerDialog("End") { time ->
                // Extract hour and minute from the Time object
                val hour = time.hours
                val minute = time.minutes
                // Set the text of startTimeEditText with formatted time
                endTime = time
                endTimeEditText.text = String.format("%02d:%02d", hour, minute)
            }
        }

        todoViewModel.getAllCategoryNames { todoCategoryList ->
            Log.e("TodoCategory", todoCategoryList.toString())
            val arrayAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, todoCategoryList)
            categoryNameAutoCompleteTextView.setAdapter(arrayAdapter)
            categoryNameAutoCompleteTextView.setThreshold(1)
        }

    }

    private fun setDataToViews(taskId: Long) {
        todoViewModel.getTaskByID(taskId).observe(this) { todoData ->
            if (todoData != null) {
                deleteButton.visibility = View.VISIBLE
                descriptionEditText.setText(todoData.taskDesc)
                taskNameEditText.setText(todoData.taskName)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(todoData.date.time)
                dateTextView.text = formattedDate
                date = todoData.date
                val hour = todoData.startTime.hours
                val minute = todoData.startTime.minutes
                startTime = todoData.startTime
                startTimeEdittext.text = String.format("%02d:%02d", hour, minute)
                val hourEnd = todoData.endTime.hours
                val minuteEnd = todoData.endTime.minutes
                endTime = todoData.endTime
                endTimeEditText.text = String.format("%02d:%02d", hourEnd, minuteEnd)
                deleteButton.setOnClickListener {
                    todoViewModel.deleteTodo(todoData)
                    finish()
                }
                todoViewModel.getCategoryById(todoData.categoryID).observe(this){categoryModel->
                    if(categoryModel!=null){
                        categoryNameAutoCompleteTextView.setText(categoryModel.categoryName)
                    }
                }
            }

        }
    }

    private fun datePicker() {
        val calendar = Calendar.getInstance()
        val dateValidator: DateValidator =
            DateValidatorPointForward.from(System.currentTimeMillis())
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Task date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(
                    CalendarConstraints.Builder().setValidator(dateValidator).build()
                )
                .build()


        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(selectedDate))
            date = Date(selectedDate)
            dateTextView.text = formattedDate
        }


        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)


                date = selectedDate.time
                dateTextView.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(startOrEnd: String, callback: (Time) -> Unit) {

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val isSystem24Hour = is24HourFormat(this)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(hour)
                .setMinute(minute)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTitleText("Set Task $startOrEnd time")
                .build()

        picker.show(supportFragmentManager, "timePicker")

        picker.addOnPositiveButtonClickListener {
            val time = Time(picker.hour, picker.minute, 0)
            callback(time)
        }


    }


    private fun createNewTask(todoId: Long) {

        val taskName = taskNameEditText.text.toString()
        val taskDesc = descriptionEditText.text.toString()
        val selectedCategoryName = categoryNameAutoCompleteTextView.text.toString()
        if (selectedCategoryName.isNotEmpty() &&
            taskName.isNotEmpty() &&
            date != null &&
            startTime != null &&
            endTime != null

        ) {
            val selectedCategoryId = todoViewModel.getCategoryIDByName(selectedCategoryName).value
            todoViewModel.createTodoCategoryIDAndInsert(
                context = this,
                todoId = todoId,
                taskName = taskName,
                taskDesc = taskDesc,
                date = date,
                startTime = startTime,
                endTime = endTime,
                categoryName = categoryNameAutoCompleteTextView.text.toString(),
                categoryId = selectedCategoryId ?: -1
            )
            onBackPressedDispatcher.onBackPressed()
        } else {
            Toast.makeText(this, "Please Enter category", Toast.LENGTH_SHORT).show()
        }

    }


    // TODO(
//      1 Fix getCategoryByID 2 FixNotification,
//  )
}