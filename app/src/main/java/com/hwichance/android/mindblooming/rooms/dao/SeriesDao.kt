package com.hwichance.android.mindblooming.rooms.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.hwichance.android.mindblooming.rooms.data.SeriesData

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series_data")
    fun getAllSeries(): LiveData<List<SeriesData>>

    @Query("SELECT * FROM series_data WHERE is_favorite = (:isFavorite)")
    fun getFavoriteSeries(isFavorite: Boolean): LiveData<List<SeriesData>>

    @Query("SELECT * FROM series_data WHERE series_id = (:seriesId)")
    fun getSeriesById(seriesId: Long): LiveData<SeriesData>

    @Insert(onConflict = REPLACE)
    suspend fun insertSeries(series: SeriesData): Long

    @Update
    suspend fun updateSeries(series: SeriesData)

    @Delete
    suspend fun deleteSeries(series: SeriesData)

    @Query("DELETE FROM series_data WHERE series_id IN (:seriesIds)")
    suspend fun deleteSeriesByIds(seriesIds: List<Long>)
}