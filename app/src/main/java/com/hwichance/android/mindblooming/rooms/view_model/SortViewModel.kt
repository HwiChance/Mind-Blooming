package com.hwichance.android.mindblooming.rooms.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hwichance.android.mindblooming.rooms.data.SortData
import com.hwichance.android.mindblooming.rooms.repository.SortRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SortViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SortRepository.getInstance(application)
    private val sortData = repository.getData()

    fun getData(): LiveData<SortData> {
        return sortData
    }

    fun insert(data: SortData) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(data)
    }

    fun delete(data: SortData) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(data)
    }
}