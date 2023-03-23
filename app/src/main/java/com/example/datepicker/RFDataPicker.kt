package com.example.datepicker

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.datepicker.databinding.RfDatePickerBinding
import java.text.SimpleDateFormat
import java.util.*


class RFDataPicker : Fragment() {

    companion object {
        private const val MAX_YEAR = 2099
    }

    private fun setMonthPickerValues(numberPicker: NumberPicker, numbers: Array<String>) {
        numberPicker.maxValue = numbers.size - 1
        numberPicker.minValue = 0
        numberPicker.wrapSelectorWheel = true
        numberPicker.displayedValues = numbers
    }

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
        val cal: Calendar = Calendar.getInstance()
        val monthPicker = binding.monthDatePicker
        val yearPicker = binding.yearDatePicker

        val formatter = SimpleDateFormat("MM", Locale.getDefault())
        val dateFormat = SimpleDateFormat("LLL", Locale.getDefault())
        val shortMonths: Array<String> = Array(12) { "" }
        for (monthNum in 0..shortMonths.lastIndex) {
            val mDate =
                formatter.parse(if (monthNum < 9) "0${monthNum + 1}" else "${monthNum + 1}") ?: "01"
            shortMonths[monthNum] = dateFormat.format(mDate).uppercase()
        }
        setMonthPickerValues(monthPicker, shortMonths)

        val year: Int = cal.get(Calendar.YEAR)
        yearPicker.minValue = year - 150
        yearPicker.maxValue = MAX_YEAR
        yearPicker.value = year

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.dayDatePicker.selectionDividerHeight = 0
            monthPicker.selectionDividerHeight = 0
            yearPicker.selectionDividerHeight = 0
        }
        monthPicker.setOnValueChangedListener { _, _, _ ->
            changeDayInMonth()
        }
        yearPicker.setOnValueChangedListener { _, _, _ ->
            changeDayInMonth()
        }
    }

    private fun changeDayInMonth() {
        val dayPicker = binding.dayDatePicker
        val monthNumber =binding.monthDatePicker.value.toString().toInt() + 1
        val days: Int = getCountOfDaysInMonth(monthNumber)
        binding.datePickerText.text = days.toString()
        dayPicker.minValue = 1
        dayPicker.maxValue = days
        dayPicker.value = 1
    }

    private fun getCountOfDaysInMonth(monthNum: Int): Int {
        return when (monthNum) {
            4, 6, 9, 11 -> 30
            2 -> if (binding.yearDatePicker.value % 4 == 0) 29 else 28
            else -> 31
        }
    }

}

