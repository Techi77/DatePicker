package com.example.datepicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class DataPickerViewModel : ViewModel() {
    private val dateMutableLiveData = MutableLiveData<Date>()
    val dateLiveData: LiveData<Date> = dateMutableLiveData

    fun setDate(newDate: Date){
        dateMutableLiveData.postValue(newDate)
    }
}