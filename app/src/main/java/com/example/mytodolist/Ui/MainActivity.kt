package com.example.mytodolist.Ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.database.TodoViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.Date


class MainActivity : AppCompatActivity() {
    private lateinit var todoViewModel: TodoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        todoViewModel = TodoViewModel(application)

        val addNewTaskButton = findViewById<FloatingActionButton>(R.id.addNewTaskButton)
        addNewTaskButton.setOnClickListener {
            val intent = Intent(this@MainActivity, AddNewTask::class.java)
            startActivity(intent)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.todoTaskRv)
        val currentDate = Date()
        todoViewModel.getTaskByDateIsNotComplete(currentDate,false) { listOfTodo ->
            if(listOfTodo.isEmpty()){
                recyclerView.visibility=View.GONE
                findViewById<TextView>(R.id.tvNoTask).visibility=View.VISIBLE
            }else{
                findViewById<TextView>(R.id.tvNoTask).visibility=View.GONE
                recyclerView.visibility=View.VISIBLE
               todoViewModel.displayDataRv(listOfTodo, recyclerView,this)
            }
            progressFunction(currentDate)
        }

        findViewById<MaterialButton>(R.id.viewAllTaskButton).setOnClickListener {
            val intent = Intent(this@MainActivity, AllTaskActivity::class.java)
            startActivity(intent)
        }
        findViewById<MaterialCardView>(R.id.taskStatusCard).setOnClickListener {
            val intent = Intent(this@MainActivity, AllTaskActivity::class.java)
            intent.putExtra("DoTodayCompleteTask",true)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.seeAllTextView).setOnClickListener {
            val intent = Intent(this@MainActivity, AllTaskActivity::class.java)
            startActivity(intent)
        }
        permissionNotification(this)
    }

    private fun progressFunction(currentDate: Date) {
        val progressIndicator = findViewById<CircularProgressIndicator>(R.id.circularTaskIndicator)
        val progressTV = findViewById<TextView>(R.id.percentageCompleted)

        var total = 0
        var completedTask = 0


        todoViewModel.getTotalTaskSizeByDate(currentDate).observe(this) { totalTasks ->
            total = totalTasks
            updateProgress(progressIndicator, progressTV, completedTask, total)
        }

        // Observe the number of completed tasks
        todoViewModel.getTotalTaskSizeCompletedByDate(true, date = currentDate).observe(this) { completedTasks ->
            completedTask = completedTasks
            updateProgress(progressIndicator, progressTV, completedTask, total)
        }
    }
    private fun updateProgress(progressIndicator: CircularProgressIndicator, progressTV: TextView, completedTask: Int, total: Int) {
        progressIndicator.max = total
        progressIndicator.progress = completedTask

        val percentage = if (total != 0) {
            ((completedTask.toDouble() / total.toDouble()) * 100).toInt()
        } else {
            0
        }
        val progressDone = "$percentage%"
        progressTV.text = progressDone
    }




    private fun permissionNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

        } else {

        }
    }



}