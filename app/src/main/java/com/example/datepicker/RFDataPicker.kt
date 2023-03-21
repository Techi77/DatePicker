package com.example.datepicker

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.datepicker.databinding.RfDatePickerBinding
import java.text.DateFormatSymbols
import java.util.*


class RFDataPicker : Fragment() {

    companion object {
        private const val MAX_YEAR = 2099
    }

    private fun setNumberPicker(numberPicker: NumberPicker, numbers: Array<String>) {
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
        val dayPicker = binding.dayDatePicker

        val shortMonths: Array<String> = DateFormatSymbols().shortMonths

        for(monthNum in 0..shortMonths.lastIndex){
            shortMonths[monthNum]=shortMonths[monthNum].uppercase()
        }

        val days: Int = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
        dayPicker.minValue = 1
        dayPicker.maxValue = days
        dayPicker.value = days

        setNumberPicker(monthPicker, shortMonths)
        val year: Int = cal.get(Calendar.YEAR)
        yearPicker.minValue = year - 150
        yearPicker.maxValue = MAX_YEAR
        yearPicker.value = year

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dayPicker.selectionDividerHeight = 0
            monthPicker.selectionDividerHeight = 0
            yearPicker.selectionDividerHeight = 0
        }
    }

}

