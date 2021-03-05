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

    fun findOneIdeaById(ideaId: Long): LiveData<IdeaData> {
        return repository.findOneIdeaById(ideaId)
    }

    fun findStarredIdea(isStarred: Boolean): LiveData<List<IdeaData>> {
        return repository.findStarredIdea(isStarred)
    }

    fun findIdeaInSeries(seriesId: Long): LiveData<List<IdeaData>> {
        return repository.findIdeaInSeries(seriesId)
    }

    fun insert(idea: IdeaData, func: (id: Long) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        func(repository.insert(idea))
    }

    fun update(idea: IdeaData) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(idea)
    }

    fun updateStar(isStarred: Boolean, ideaIds: List<Long>) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStar(isStarred, ideaIds)
        }

    fun updateSeries(ideaIds: List<Long>) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateSeries(ideaIds)
    }

    fun updateSeriesDelete(seriesIds: List<Long>) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateSeriesDelete(seriesIds)
    }


    fun delete(idea: IdeaData) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(idea)
    }

    fun deleteIdeas(ideaIds: List<Long>) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteIdeas(ideaIds)
    }
}