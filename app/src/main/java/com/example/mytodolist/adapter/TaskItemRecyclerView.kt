package com.example.mytodolist.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.Ui.AddNewTask
import com.example.mytodolist.R
import com.example.mytodolist.database.dataClass.TodoDataClass
import kotlinx.coroutines.InternalCoroutinesApi
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Locale

class TaskItemRecyclerView(
    private val context: Context,
    private val todoDataClasses: List<TodoDataClass>,
    private val deleteCallBack: (TodoDataClass) -> Unit,
    private val taskComplete: (TodoDataClass) -> Unit,
) : RecyclerView.Adapter<TaskItemRecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val timeStartToEndTv: TextView = itemView.findViewById(R.id.timeStartToEndTv)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val editTaskButtonList: ImageButton = itemView.findViewById(R.id.editTaskButtonList)
        val deleteTaskButtonList: ImageButton = itemView.findViewById(R.id.deleteTaskButtonList)
        val taskDoneButton: ImageButton = itemView.findViewById(R.id.taskDoneButton)
        val descriptionTv: TextView = itemView.findViewById(R.id.descriptionTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.todoitemlayoutrv, parent, false)
        return ViewHolder(view)
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < todoDataClasses.size) {
            val todoDataClass = todoDataClasses.get(position)
            holder.titleTv.text = todoDataClass.taskName


            holder.timeStartToEndTv.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(todoDataClass.startTime)
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(todoDataClass.date)
            holder.dateTextView.text = formattedDate

            if (todoDataClass.isComplete) {
                holder.taskDoneButton.setImageResource(R.drawable.completeclicle)
            } else {
                holder.taskDoneButton.setImageResource(R.drawable.taskdone)
            }
            holder.descriptionTv.text = todoDataClass.taskDesc.ifEmpty { "No Description" }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, AddNewTask::class.java)
                intent.putExtra("TaskID", todoDataClass.todoId)
                intent.putExtra("isEdit", true)
                context.startActivity(intent)
            }

            holder.editTaskButtonList.setOnClickListener {
                val intent = Intent(context, AddNewTask::class.java)
                intent.putExtra("TaskID", todoDataClass.todoId)
                intent.putExtra("isEdit", true)
                context.startActivity(intent)
            }

            holder.deleteTaskButtonList.setOnClickListener {
                deleteCallBack(todoDataClass)
            }
            holder.taskDoneButton.setOnClickListener {
                taskComplete(todoDataClass)
            }

        }
    }

    private fun getTimeFormated(time: Time): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(time.time)
    }

    override fun getItemCount(): Int {
        return todoDataClasses.size
    }
}
