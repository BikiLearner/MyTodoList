package com.example.mytodolist.bottomSheets

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.mytodolist.R
import com.example.mytodolist.database.dataClass.RepeatModel
import com.example.mytodolist.reusePackage.datePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.CornerFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RepeatsBottomSheetClass(private val context: Context) {

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var editTextIntervalNumber: EditText? = null
    private var editTextOnSelected: TextView? = null
    private var editTextAfterSelected: EditText? = null
    private var textViewTextMenuBottomSheet: AutoCompleteTextView? = null

    private var onDate: Date? = null

    private lateinit var radioButtonNever: RadioButton
    private lateinit var radioButtonOn: RadioButton
    private lateinit var radioButtonAfter: RadioButton
    private var lastSelectedRadioButton: RadioButton? = null

    private var dayOfWeeKChips: ChipGroup? = null

    private val selectedDayOfWeek = ArrayList<Int>()

    private var onMenuItemSelectedBottomSheet: String = "Week"
    private lateinit var view: View

    init {
        Log.e("The selected Day of Week", "Selected: $selectedDayOfWeek")
        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        view = LayoutInflater.from(context).inflate(R.layout.repeat_bottom_sheet_layout, null)

        editTextIntervalNumber = view.findViewById(R.id.editTextIntervalNumber)
        editTextOnSelected = view.findViewById(R.id.editTextOnSelected)
        editTextAfterSelected = view.findViewById(R.id.editTextAfterSelected)
        textViewTextMenuBottomSheet = view.findViewById(R.id.textViewTextMenuBottomSheet)
        dayOfWeeKChips = view.findViewById(R.id.days_of_week_chip_group)
        chipsFunctionality()

        textViewTextMenuBottomSheet?.setText(onMenuItemSelectedBottomSheet)

        editTextOnSelected?.isEnabled = false
        editTextAfterSelected?.isEnabled = false

        editTextOnSelected!!.text = SimpleDateFormat("MMMM d ", Locale.getDefault()).format(Date())

        radioButtonNever = view.findViewById(R.id.radio_button_never)
        radioButtonOn = view.findViewById(R.id.radio_button_on)
        radioButtonAfter = view.findViewById(R.id.radio_button_after)

        radioButtonNever.isChecked = true
        lastSelectedRadioButton=radioButtonNever

        setTextViewTextMenuBottomSheetMenu()

        editTextOnSelected!!.setOnClickListener {
            datePicker({ selectedDate ->
                onDate = selectedDate.time
                editTextOnSelected!!.text =
                    SimpleDateFormat(
                        "MMMM d ",
                        Locale.getDefault()
                    ).format(selectedDate.timeInMillis)
            }, context = context)
        }

        radioButtonFunction()

        bottomSheetDialog = BottomSheetDialog(context).apply {
            setContentView(view)


        }
    }

    fun showBottomSheet(repeatModel: (RepeatModel) -> Unit) {
        // Show the bottom sheet
        bottomSheetDialog?.show()
        view.findViewById<MaterialButton>(R.id.done_button_bottomSheet)?.setOnClickListener {
            Log.e("The selected Day of Week", "Selected: $selectedDayOfWeek")

            repeatModel.invoke(getRepeatModel())
            dismissBottomSheet()
        }
    }

    private fun getRepeatModel(): RepeatModel {
        return RepeatModel(
            editTextIntervalNumber?.text.toString().toInt(),
            onMenuItemSelectedBottomSheet,
            selectedDayOfWeek,
            (lastSelectedRadioButton === radioButtonNever),
            (lastSelectedRadioButton === radioButtonOn),
            (lastSelectedRadioButton === radioButtonAfter),
            onDate = onDate,
            editTextAfterSelected?.text.toString().toInt()
        )
    }

    private fun setTextViewTextMenuBottomSheetMenu() {
        val items = arrayOf("Day", "Week", "Month", "Year")
        val adapter = ArrayAdapter(context, R.layout.menu_text_view_layout, items)

        textViewTextMenuBottomSheet?.setAdapter(adapter)
        textViewTextMenuBottomSheet?.setOnItemClickListener { _, _, position, _ ->
            onMenuItemSelectedBottomSheet = items[position]
        }
    }

    private fun chipsFunctionality() {
        val daysOfWeekShort = arrayOf("S", "M", "T", "W", "T", "F", "S")

        for (i in daysOfWeekShort.indices) {
            val chip = Chip(context).apply {
                text = daysOfWeekShort[i]
                id = i+1
                isCheckable = true
                isChecked = false // Initially checked
                isChipIconVisible = false
                isCloseIconVisible = false
                setTextColor(ContextCompat.getColorStateList(context, R.color.lightGreen))
                chipBackgroundColor =
                    ContextCompat.getColorStateList(context, R.color.chip_background_selector)
                setTextColor(ContextCompat.getColorStateList(context, R.color.darkBlue))

                // Set a listener to handle chip clicks
                setOnCheckedChangeListener { _, isChecked ->
                    Log.e("The Chip", "Checked: $id")
                    if (isChecked) {
                        selectedDayOfWeek.add(id)
                    } else {
                        selectedDayOfWeek.remove(id)
                    }
                }

                // Make the chip circular
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCorners(
                        CornerFamily.ROUNDED,
                        resources.getDimension(R.dimen.chip_corner_radius)
                    )
                    .build()
            }

            dayOfWeeKChips?.addView(chip)
        }
    }

    private fun radioButtonFunction() {
        val radioButtonClickListener = View.OnClickListener { v ->
            val clickedRadioButton = v as RadioButton

            // Deselect previously selected RadioButton
            lastSelectedRadioButton?.isChecked = false

            // Select the clicked RadioButton
            clickedRadioButton.isChecked = true
            lastSelectedRadioButton = clickedRadioButton


            editTextOnSelected?.isEnabled = radioButtonOn.isChecked
            editTextAfterSelected?.isEnabled = radioButtonAfter.isChecked
        }

        radioButtonNever.setOnClickListener(radioButtonClickListener)
        radioButtonOn.setOnClickListener(radioButtonClickListener)
        radioButtonAfter.setOnClickListener(radioButtonClickListener)
    }

    fun setUpdateRepeat(repeatModel: RepeatModel) {
        radioButtonNever.isChecked = repeatModel.never
        radioButtonOn.isChecked = repeatModel.on
        radioButtonAfter.isChecked = repeatModel.after

        editTextIntervalNumber?.setText(repeatModel.every.toString())
        editTextOnSelected?.text = SimpleDateFormat("MMMM d ", Locale.getDefault()).format(
            repeatModel.onDate ?: Date()
        )
        editTextAfterSelected?.setText(repeatModel.afterOccurrence.toString())
        textViewTextMenuBottomSheet?.setText(repeatModel.weekDayMonthOrYear)
        Log.e("The selected Day of Week", "SelectedUpdate: ${repeatModel.weekDay}")
        selectedDayOfWeek.clear()
        selectedDayOfWeek.addAll(repeatModel.weekDay)
        repeatModel.weekDay.forEach {
            val chip = dayOfWeeKChips?.findViewById<Chip>(it)
            chip?.isChecked = true
        }
    }

    private fun dismissBottomSheet() {
        bottomSheetDialog?.dismiss()
    }
}
