package com.hwichance.android.mindblooming.rooms.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hwichance.android.mindblooming.rooms.data.MindMapItemData
import com.hwichance.android.mindblooming.rooms.repository.MindMapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MindMapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MindMapRepository(application)
    private var mindMapItems: LiveData<List<MindMapItemData>>? = null

    fun getAll(groupId: Long): LiveData<List<MindMapItemData>> {
        return mindMapItems ?: synchronized(this) {
            mindMapItems ?: repository.getAll(groupId).also { mindMapItems = it }
        }
    }

    fun insert(item: MindMapItemData, func: (id: Long) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            func(repository.insert(item))
        }

    fun update(item: MindMapItemData) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(item)
    }

    fun delete(item: MindMapItemData) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(item)
    }
}