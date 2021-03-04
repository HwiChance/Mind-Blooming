package com.hwichance.android.mindblooming.rooms.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hwichance.android.mindblooming.rooms.data.SeriesData
import com.hwichance.android.mindblooming.rooms.repository.SeriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SeriesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SeriesRepository.getInstance(application)
    private val seriesList = repository.getAll()

    fun getAll(): LiveData<List<SeriesData>> {
        return seriesList
    }

    fun findFavoriteSeries(isFavorite: Boolean): LiveData<List<SeriesData>> {
        return repository.findFavoriteSeries(isFavorite)
    }

    fun findSeriesById(seriesId: Long): LiveData<SeriesData> {
        return repository.findSeriesById(seriesId)
    }

    fun insert(series: SeriesData, func: (id: Long) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            func(repository.insert(series))
        }

    fun update(series: SeriesData) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(series)
    }

    fun delete(series: SeriesData) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(series)
    }

    fun deleteSeriesByIds(seriesIds: List<Long>) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteSeriesByIds(seriesIds)
    }
}