package com.example.datepicker

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.datepicker.databinding.RfDatePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*


class RFDataPicker : BottomSheetDialogFragment() {

    companion object {
        private const val MAX_YEAR = 2099
        private const val DAY = "DAY"
        private const val MONTH = "MONTH"
        private const val YEAR = "YEAR"
    }

    private val viewModel: DataPickerViewModel by viewModels()

    private val fullDateFormatter =
        SimpleDateFormat("E, LLL dd, yyyy", Locale.getDefault()) //Fri, Feb 24, 2023
    private val fullDateFormatter2 = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private val dayFormatter = SimpleDateFormat("dd", Locale.getDefault())
    private val monthNumberFormatter = SimpleDateFormat("MM", Locale.getDefault())
    private val monthNameFormat = SimpleDateFormat("LLL", Locale.getDefault())
    private val yearFormatter = SimpleDateFormat("yyyy", Locale.getDefault())

    private val cal: Calendar = Calendar.getInstance()

    private lateinit var binding: RfDatePickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RfDatePickerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.dateLiveData.value == null) viewModel.setDate(cal.time)
        viewModel.dateLiveData.observe(viewLifecycleOwner) { calendar ->
            binding.datePickerText.text = fullDateFormatter.format(calendar)
        }

        settingMonthPicker()
        settingYearPicker()
        with(binding) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dayDatePicker.selectionDividerHeight = 0
                monthDatePicker.selectionDividerHeight = 0
                yearDatePicker.selectionDividerHeight = 0
            }
            changeDayInMonth()
            settingPickerListeners(monthDatePicker, MONTH)
            settingPickerListeners(yearDatePicker, YEAR)
            settingPickerListeners(dayDatePicker, DAY)
        }
        binding.background.setOnClickListener {
            Toast.makeText(context, "background.setOnClickListener", Toast.LENGTH_SHORT).show()
        }
        binding.dialog.setOnClickListener {
        }
    }

    private fun settingPickerListeners(picker: NumberPicker, type: String) {
        picker.setOnValueChangedListener { _, _, newVal ->
            changeDate(if (type == MONTH) newVal else newVal - 1, type)
        }
        picker.setOnScrollListener { view, scrollState ->
            changeColorInScrolling(view, scrollState)
        }
    }

    private fun changeColorInScrolling(view: NumberPicker, scrollState: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val textColor =
                requireContext().getColor(if (scrollState == 0) R.color.black else R.color.license_dialog_separator)
            view.textColor = textColor
        }
    }

    private fun changeDate(newValue: Int, type: String) {
        val correctNewValue = if (newValue < 9) "0${newValue + 1}" else "${newValue + 1}"
        var day = viewModel.dateLiveData.value?.let { dayFormatter.format(it) } ?: "01"
        var monthNum = viewModel.dateLiveData.value?.let { monthNumberFormatter.format(it) } ?: "01"
        var year = viewModel.dateLiveData.value?.let { yearFormatter.format(it) } ?: "2023"
        when (type) {
            DAY -> day = correctNewValue
            MONTH -> monthNum = correctNewValue
            YEAR -> year = correctNewValue
        }
        val fullDate = fullDateFormatter2.parse("$monthNum/$day/$year") //MM/dd/yyyy
        fullDate?.let { viewModel.setDate(it) }
        if (type == YEAR || type == MONTH) changeDayInMonth()
    }

    private fun settingMonthPicker() {
        val currentMonthNum =
            viewModel.dateLiveData.value?.let { monthNumberFormatter.format(it).toInt() - 1 }
                ?: (monthNumberFormatter.format(cal.time).toInt() - 1)
        val monthPicker = binding.monthDatePicker
        val shortMonths: Array<String> = Array(12) { "" }
        for (monthNum in 0..shortMonths.lastIndex) {
            val mDate =
                monthNumberFormatter.parse(if (monthNum < 9) "0${monthNum + 1}" else "${monthNum + 1}")
                    ?: "01"
            shortMonths[monthNum] = monthNameFormat.format(mDate).uppercase()
        }
        monthPicker.maxValue = shortMonths.size - 1
        monthPicker.minValue = 0
        monthPicker.wrapSelectorWheel = true
        monthPicker.displayedValues = shortMonths
        monthPicker.value = currentMonthNum
    }

    private fun settingYearPicker() {
        val year =
            viewModel.dateLiveData.value?.let { yearFormatter.format(it).toInt() } ?: cal.get(
                Calendar.YEAR
            )
        with(binding) {
            yearDatePicker.minValue = year - 150
            yearDatePicker.maxValue = MAX_YEAR
            yearDatePicker.value = year
        }
    }

    private fun changeDayInMonth() {
        val monthNumber = binding.monthDatePicker.value.toString().toInt() + 1
        val days: Int = getCountOfDaysInMonth(monthNumber)
        val savedDay = viewModel.dateLiveData.value?.let { dayFormatter.format(it).toInt() }
        with(binding) {
            dayDatePicker.minValue = 1
            dayDatePicker.maxValue = days
            dayDatePicker.value = if (savedDay == null) {
                dayFormatter.format(cal.time).toInt()
            } else if (savedDay <= days) savedDay else {
                changeDate(days - 1, DAY)
                days
            }
        }
    }

    private fun getCountOfDaysInMonth(monthNum: Int): Int {
        return when (monthNum) {
            4, 6, 9, 11 -> 30
            2 -> if (binding.yearDatePicker.value % 4 == 0) 29 else 28
            else -> 31
        }
    }

}

