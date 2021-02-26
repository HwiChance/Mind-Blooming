package com.hwichance.android.mindblooming.rooms.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.repository.IdeaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IdeaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = IdeaRepository.getInstance(application)
    private val ideaList = repository.getAllIdeas()

    fun getAllIdeas(): LiveData<List<IdeaData>> {
        return ideaList
    }

    fun findIdeasByIds(ideaIds: LongArray): LiveData<List<IdeaData>> {
        return repository.findIdeasByIds(ideaIds)
    }

    fun findOneIdeaById(ideaId: Long): LiveData<IdeaData> {
        return repository.findOneIdeaById(ideaId)
    }

    fun insert(idea: IdeaData, func: (id: Long) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        func(repository.insert(idea))
    }

    fun update(idea: IdeaData) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(idea)
    }

    fun delete(idea: IdeaData) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(idea)
    }
}