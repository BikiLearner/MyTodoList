package com.example.mytodolist.Ui

import com.example.mytodolist.bottomSheets.RepeatsBottomSheetClass
import android.os.Bundle
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
import com.example.mytodolist.database.dataClass.RepeatModel
import com.example.mytodolist.enums.IntentPassEnum
import com.example.mytodolist.reusePackage.datePicker
import com.example.mytodolist.reusePackage.showTimePickerDialog
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates


@AndroidEntryPoint
class AddNewTask : AppCompatActivity() {
    private lateinit var descriptionEditText: EditText
    private lateinit var taskNameEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var startTimeEdittext: TextView
    private lateinit var repeatButton: MaterialButton
    private lateinit var categoryNameAutoCompleteTextView: AutoCompleteTextView
    private var date: Date? = null
    private var startTime by Delegates.notNull<Long>()
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var deleteButton: ImageButton
    private  var repeatModel: RepeatModel?=null
    private lateinit var  repeatsBottomSheet:RepeatsBottomSheetClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        repeatsBottomSheet = RepeatsBottomSheetClass(this)
        val todoId = intent.getLongExtra(IntentPassEnum.TASKID.name, 0)
        val isEdit = intent.getBooleanExtra(IntentPassEnum.ISEDIT.name, false)

        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]


        descriptionEditText = findViewById(R.id.editTextDescription)
        taskNameEditText = findViewById(R.id.taskNameEditText)
        dateTextView = findViewById(R.id.DateAndTimeEditText)
        startTimeEdittext = findViewById(R.id.startTimeEditText)
        deleteButton = findViewById(R.id.deleteTaskButtonEditTask)
        categoryNameAutoCompleteTextView = findViewById(R.id.CategoryNameEditText)
        repeatButton = findViewById(R.id.repeatButton)
        val backButton = findViewById<ImageButton>(R.id.backButtonAddTask)
        val saveButton = findViewById<MaterialButton>(R.id.saveOrEditButton)
        val toolbarTaskAdd = findViewById<TextView>(R.id.toolBarTitleAddOrEditTask)



        deleteButton.visibility = View.GONE
        if (isEdit) {
            setDataToViews(todoId)
            saveButton.text = getString(R.string.edit_Done)
            toolbarTaskAdd.text = getString(R.string.edit_task)
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        dateTextView.setOnClickListener {
            datePicker({
                date = it.time
                dateTextView.text = SimpleDateFormat("MMMM d yyyy", Locale.getDefault()).format(it.timeInMillis)
            }, context = this)
        }

        saveButton.setOnClickListener {
            createNewTask(todoId)
        }


        startTimeEdittext.setOnClickListener {
            showTimePickerDialog ({ time ->
                startTime = time
                startTimeEdittext.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(time)
            }, supportFragmentManager = supportFragmentManager, context = this)
        }



        todoViewModel.getAllCategoryNames { todoCategoryList ->
            Log.e("TodoCategory", todoCategoryList.toString())
            val arrayAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, todoCategoryList)
            categoryNameAutoCompleteTextView.setAdapter(arrayAdapter)
            categoryNameAutoCompleteTextView.setThreshold(1)
        }


        repeatButton.setOnClickListener {
            repeatsBottomSheet.showBottomSheet{repeatMode->
                repeatModel = repeatMode
            }
        }


    }

    private fun setDataToViews(todoId: Long) {
        todoViewModel.getTaskByID(todoId).observe(this){todoData->

            taskNameEditText.setText(todoData.taskName)
            descriptionEditText.setText(todoData.taskDesc)
            date=todoData.date
            startTime=todoData.startTime
            dateTextView.text = SimpleDateFormat("MMMM d yyyy", Locale.getDefault()).format(todoData.date)
            startTimeEdittext.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(todoData.startTime)

            todoViewModel.getCategoryById(todoData.categoryID).observe(this){
                if(it!=null){
                    categoryNameAutoCompleteTextView.setText(it.categoryName)
                }else{
                    categoryNameAutoCompleteTextView.setText("")
                }
            }
            repeatModel=todoData.repeat
            repeatsBottomSheet.setUpdateRepeat(repeatModel!!)
        }

    }





    private fun createNewTask(todoId: Long) {

        val taskName = taskNameEditText.text.toString()
        val taskDesc = descriptionEditText.text.toString()
        val selectedCategoryName = categoryNameAutoCompleteTextView.text.toString()
        if (selectedCategoryName.isNotEmpty() &&
            taskName.isNotEmpty() && startTime != 0L &&
            date != null
        ) {
            val selectedCategoryId = todoViewModel.getCategoryIDByName(selectedCategoryName).value
            todoViewModel.createTodoCategoryIDAndInsert(
                context = this,
                todoId = todoId,
                taskName = taskName,
                taskDesc = taskDesc,
                date = date!!,
                startTime = startTime,
                categoryName = categoryNameAutoCompleteTextView.text.toString(),
                categoryId = selectedCategoryId ?: -1,
                repeatModel = repeatModel?:RepeatModel()
            )
            onBackPressedDispatcher.onBackPressed()
        } else {
            Toast.makeText(this, "Please Enter category", Toast.LENGTH_SHORT).show()
        }

    }


}