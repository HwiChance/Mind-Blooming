package com.hwichance.android.mindblooming.rooms.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.hwichance.android.mindblooming.rooms.dao.SeriesDao
import com.hwichance.android.mindblooming.rooms.data.SeriesData
import com.hwichance.android.mindblooming.rooms.database.AppDatabase

class SeriesRepository(application: Application) {
    companion object {
        private var seriesRepoInstance: SeriesRepository? = null

        fun getInstance(app: Application): SeriesRepository {
            return seriesRepoInstance ?: synchronized(this) {
                seriesRepoInstance ?: SeriesRepository(app).also { seriesRepoInstance = it }
            }
        }
    }

    private val seriesDao: SeriesDao by lazy {
        val db = AppDatabase.getInstance(application.applicationContext)
        db.seriesDao()
    }

    private val seriesList: LiveData<List<SeriesData>> by lazy {
        seriesDao.getAllSeries()
    }

    fun getAll(): LiveData<List<SeriesData>> {
        return seriesList
    }

    fun findFavoriteSeries(isFavorite: Boolean): LiveData<List<SeriesData>> {
        return seriesDao.getFavoriteSeries(isFavorite)
    }

    fun findSeriesById(seriesId: Long): LiveData<SeriesData> {
        return seriesDao.getSeriesById(seriesId)
    }

    suspend fun insert(series: SeriesData): Long {
        return seriesDao.insertSeries(series)
    }

    suspend fun update(series: SeriesData) {
        seriesDao.updateSeries(series)
    }

    suspend fun delete(series: SeriesData) {
        seriesDao.deleteSeries(series)
    }

    suspend fun deleteSeriesByIds(seriesIds: List<Long>) {
        seriesDao.deleteSeriesByIds(seriesIds)
    }
}