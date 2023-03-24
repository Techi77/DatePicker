package com.example.datepicker

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.datepicker.databinding.RfDatePickerBinding
import java.text.SimpleDateFormat
import java.util.*


class RFDataPicker : Fragment() {

    companion object {
        private const val MAX_YEAR = 2099
        private const val DAY = "DAY"
        private const val MONTH = "MONTH"
        private const val YEAR = "YEAR"
    }

    private val viewModel: DataPickerViewModel by viewModels()

    private val fullDateFormatter = SimpleDateFormat("E, LLL dd, yyyy", Locale.getDefault()) //Fri, Feb 24, 2023
    private val fullDateFormatter2 = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private val dayFormatter = SimpleDateFormat("dd", Locale.getDefault())
    private val monthNumberFormatter = SimpleDateFormat("MM", Locale.getDefault())
    private val monthNameFormat = SimpleDateFormat("LLL", Locale.getDefault())
    private val yearFormatter = SimpleDateFormat("yyyy", Locale.getDefault())

    private val cal: Calendar = Calendar.getInstance()

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
        if(viewModel.dateLiveData.value==null)viewModel.setDate(cal.time)

        settingMonthPicker()
        settingYearPicker()


        with(binding){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dayDatePicker.selectionDividerHeight = 0
                monthDatePicker.selectionDividerHeight = 0
                yearDatePicker.selectionDividerHeight = 0
            }
            changeDayInMonth()
            monthDatePicker.setOnValueChangedListener { _, _, newVal ->
                changeDate(newVal, MONTH)
            }
            yearDatePicker.setOnValueChangedListener { _, _, newVal ->
                changeDate(newVal-1, YEAR)
            }
            dayDatePicker.setOnValueChangedListener { _, _, newVal ->
                changeDate(newVal-1, DAY)
            }
            viewModel.dateLiveData.observe(viewLifecycleOwner) { calendar ->
                datePickerText.text = fullDateFormatter.format(calendar)
            }
        }
    }

    private fun changeDate(newValue:Int, type: String){
        if(type== YEAR || type == MONTH) changeDayInMonth()
        val correctNewValue = if (newValue < 9) "0${newValue + 1}" else "${newValue + 1}"
        var day = viewModel.dateLiveData.value?.let { dayFormatter.format(it) } ?: "01"
        var monthNum = viewModel.dateLiveData.value?.let { monthNumberFormatter.format(it) } ?: "01"
        var year = viewModel.dateLiveData.value?.let { yearFormatter.format(it) } ?: "2023"
        when(type){
            DAY -> day = correctNewValue
            MONTH -> monthNum = correctNewValue
            YEAR -> year = correctNewValue
        }
        val fullDate = fullDateFormatter2.parse("$monthNum/$day/$year") //MM/dd/yyyy
        fullDate?.let { viewModel.setDate(it) }
    }

    private fun settingMonthPicker(){
        val monthPicker = binding.monthDatePicker
        val shortMonths: Array<String> = Array(12) { "" }
        for (monthNum in 0..shortMonths.lastIndex) {
            val mDate =
                monthNumberFormatter.parse(if (monthNum < 9) "0${monthNum + 1}" else "${monthNum + 1}") ?: "01"
            shortMonths[monthNum] = monthNameFormat.format(mDate).uppercase()
        }
        setMonthPickerValues(monthPicker, shortMonths)
    }

    private fun settingYearPicker(){
        val year: Int = cal.get(Calendar.YEAR)
        with(binding) {
            yearDatePicker.minValue = year - 150
            yearDatePicker.maxValue = MAX_YEAR
            yearDatePicker.value = year
        }
    }

    private fun changeDayInMonth() {
        val monthNumber =binding.monthDatePicker.value.toString().toInt() + 1
        val days: Int = getCountOfDaysInMonth(monthNumber)
        val savedDay = viewModel.dateLiveData.value?.let { dayFormatter.format(it).toInt() } ?: 1
        with(binding) {
            dayDatePicker.minValue = 1
            dayDatePicker.maxValue = days
            deleteMeIMLog.text = "sd=$savedDay md=$days\n(savedDay<=days)=${savedDay<=days}"
            dayDatePicker.value = if(savedDay<=days) savedDay else {
                changeDate(days, DAY)
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

