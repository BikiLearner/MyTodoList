package com.example.mytodolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mytodolist.database.TodoDataClass
import com.example.mytodolist.database.TodoViewModel
import com.google.android.material.button.MaterialButton
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddNewTask : AppCompatActivity() {
    private lateinit var descriptionEditText: EditText
    private lateinit var taskNameEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var startTimeEdittext: TextView
    private lateinit var endTimeEditText: TextView
    private lateinit var date: Date
    private lateinit var startTime: Time
    private lateinit var endTime: Time
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var  deleteButton:ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val todoId = intent.getIntExtra("TaskID",0)
        val isEdit = intent.getBooleanExtra("isEdit",false)

        todoViewModel = TodoViewModel(application)
        descriptionEditText = findViewById(R.id.editTextDescription)
        taskNameEditText = findViewById(R.id.taskNameEditText)
        dateTextView = findViewById(R.id.DateAndTimeEditText)
        startTimeEdittext=findViewById(R.id.startTimeEditText)
        endTimeEditText=findViewById(R.id.endTimeEditText)
        val backButton=findViewById<ImageButton>(R.id.backButtonAddTask)
        val saveButton=findViewById<MaterialButton>(R.id.saveOrEditButton)
        val toolbarTaskAdd=findViewById<TextView>(R.id.toolBarTitleAddOrEditTask)
        deleteButton=findViewById(R.id.deleteTaskButtonEditTask)
        deleteButton.visibility=View.GONE
        if(isEdit){
            setDataToViews(todoId)
            saveButton.text="Edit Done"
            toolbarTaskAdd.text="Edit Task"
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
            showTimePickerDialog { time ->
                // Extract hour and minute from the Time object
                val hour = time.hours
                val minute = time.minutes
                // Set the text of startTimeEditText with formatted time
                startTime=time
                startTimeEdittext.text = String.format("%02d:%02d", hour, minute)
            }
        }
        endTimeEditText.setOnClickListener {
            showTimePickerDialog { time ->
                // Extract hour and minute from the Time object
                val hour = time.hours
                val minute = time.minutes
                // Set the text of startTimeEditText with formatted time
                endTime=time
                endTimeEditText.text = String.format("%02d:%02d", hour, minute)
            }
        }


    }

    private fun setDataToViews(taskId:Int) {
        todoViewModel.getTaskByID(taskId).observe(this){todoData->
            if(todoData!=null){
                deleteButton.visibility=View.VISIBLE
                descriptionEditText.setText(todoData.taskDesc)
                taskNameEditText.setText(todoData.taskName)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(todoData.date.time)
                dateTextView.text = formattedDate
                date=todoData.date
                val hour = todoData.startTime.hours
                val minute = todoData.startTime.minutes
                startTime=todoData.startTime
                startTimeEdittext.text = String.format("%02d:%02d", hour, minute)
                val hourEnd = todoData.endTime.hours
                val minuteEnd = todoData.endTime.minutes
                endTime=todoData.endTime
                endTimeEditText.text = String.format("%02d:%02d", hourEnd, minuteEnd)
                deleteButton.setOnClickListener {
                    todoViewModel.deleteTodo(todoData)
                    finish()
                }

            }

        }
    }

    private fun datePicker() {
        val calendar = Calendar.getInstance()
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

        datePickerDialog.show()
    }
    private fun showTimePickerDialog(callback: (Time) -> Unit) {

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)


        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, hourOfDay: Int, minute: Int ->

                val selectedTime = Time(hourOfDay, minute, 0)

                callback(selectedTime)
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }



    private fun createNewTask(todoId: Int) {

        val taskName = taskNameEditText.text.toString()
        val taskDesc = descriptionEditText.text.toString()

        if (taskName.isEmpty() && date!=null && startTime==null && endTime==null) {
            Toast.makeText(this, "Please enter task name and date", Toast.LENGTH_LONG).show()
        } else {



            val todoDataClass = TodoDataClass(
                todoId = todoId,
                taskName=taskName,
                taskDesc=taskDesc,
                date=date,
                startTime=startTime,
                endTime=endTime
            )
            scheduleAlarm(this,todoDataClass)
            todoViewModel.addOrUpdateTodo(todoDataClass)

            onBackPressedDispatcher.onBackPressed()
        }
    }
}