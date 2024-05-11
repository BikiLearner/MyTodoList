package com.example.mytodolist

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.database.TodoDataClass
import com.example.mytodolist.database.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AllTaskActivity : AppCompatActivity() {
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var  filterTextView:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val isTodayComplete=intent.getBooleanExtra("DoTodayCompleteTask",false)




        val backButton = findViewById<ImageButton>(R.id.backButtonMyTask)
        val filterButton = findViewById<ImageButton>(R.id.filterListbutton)
        filterTextView = findViewById(R.id.filterTextView)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        todoViewModel = TodoViewModel(application)

        if(isTodayComplete){
            todoViewModel.getTaskByDateIsNotComplete(date = Date(),true) {
                inflateWithValues(it)
            }
        }else{
            todoViewModel.getAllData {
                inflateWithValues(it)
            }
        }


        filterButton.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun inflateWithValues(listOfTodo: List<TodoDataClass>) {
        val recyclerView = findViewById<RecyclerView>(R.id.todoTaskRvMyTask)

        if (listOfTodo.isEmpty()) {
            recyclerView.visibility = View.GONE
            findViewById<TextView>(R.id.tvNoTaskMyTask).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.tvNoTaskMyTask).visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            todoViewModel.displayDataRv(listOfTodo, recyclerView, this)
        }

    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.filter_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            handleMenuItemClick(item)
            true
        }
        popup.show()
    }

    private fun handleMenuItemClick(item: MenuItem) {
        filterTextView.text=item.title
        when (item.itemId) {
            R.id.AllTask -> {
                todoViewModel.getAllData {
                    inflateWithValues(it)
                }

            }
            R.id.acen -> {
                todoViewModel.getTasksByDateAscending {
                    inflateWithValues(it)
                }
            }
            R.id.dsen -> {
                todoViewModel.getTasksByDateDescending {
                    inflateWithValues(it)
                }
            }
            R.id.completed -> {
                todoViewModel.getCompletedTasks(true) {
                    inflateWithValues(it)
                }
            }
            R.id.notCompleted -> {
                todoViewModel.getCompletedTasks(false) {
                    inflateWithValues(it)
                }
            }
            R.id.calender -> {
                forCalenderFun()
            }
        }
    }

    private fun forCalenderFun() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                todoViewModel.getTaskByDate(selectedDate.time){
                    inflateWithValues(it)
                }

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

}