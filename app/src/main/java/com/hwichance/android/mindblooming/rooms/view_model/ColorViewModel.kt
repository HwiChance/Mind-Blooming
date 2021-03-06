package com.hwichance.android.mindblooming.rooms.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.hwichance.android.mindblooming.rooms.data.ColorData
import com.hwichance.android.mindblooming.rooms.repository.ColorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ColorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ColorRepository.getInstance(application)
    private val colorData = repository.getData()

    fun getData(): LiveData<List<ColorData>> {
        return colorData
    }

    fun insert(data: ColorData) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(data)
    }

    fun delete(data: ColorData) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(data)
    }

    fun insertNewDeleteOld(newData: ColorData, oldData: ColorData) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNewDeleteOld(newData, oldData)
        }
}