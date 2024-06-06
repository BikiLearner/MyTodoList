package com.example.mytodolist.Ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.database.dataClass.TodoDataClass
import com.example.mytodolist.database.TodoViewModel
import com.example.mytodolist.reusePackage.datePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date
@AndroidEntryPoint
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

        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]

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
                datePicker({selectedDate->
                    todoViewModel.getTaskByDate(selectedDate.time) {
                        inflateWithValues(it)
                    }
                }, this)
            }
        }
    }

}