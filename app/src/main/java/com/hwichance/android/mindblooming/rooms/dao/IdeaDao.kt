package com.hwichance.android.mindblooming.rooms.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.hwichance.android.mindblooming.rooms.data.IdeaData

@Dao
interface IdeaDao {
    @Query("SELECT * FROM idea_data")
    fun getAll(): LiveData<List<IdeaData>>

    @Query("SELECT * FROM idea_data WHERE idea_id IN (:ideaIds)")
    fun getIdeasByIds(ideaIds: List<Long>): LiveData<List<IdeaData>>

    @Query("SELECT * FROM idea_data WHERE idea_id = :ideaId")
    fun getOneIdeaById(ideaId: Long): LiveData<IdeaData>

    @Query("SELECT * FROM idea_data WHERE is_starred = :isStarred")
    fun getStarredIdea(isStarred: Boolean): LiveData<List<IdeaData>>

    @Query("SELECT * FROM idea_data WHERE series = :seriesId")
    fun getIdeaInSeries(seriesId: Long): LiveData<List<IdeaData>>

    @Insert(onConflict = REPLACE)
    suspend fun insertIdea(idea: IdeaData): Long

    @Update
    suspend fun updateIdea(idea: IdeaData)

    @Query("UPDATE idea_data SET is_starred = :isStarred AND starred_date = NULL WHERE idea_id IN (:ideaIds)")
    suspend fun updateNotStarred(isStarred: Boolean, ideaIds: List<Long>)

    @Query("UPDATE idea_data SET series = NULL AND series_added_date = NULL WHERE idea_id IN (:ideaIds)")
    suspend fun updateNoSeries(ideaIds: List<Long>)

    @Query("UPDATE idea_data SET series = NULL AND series_added_date = NULL WHERE series IN (:seriesIds)")
    suspend fun updateSeriesDeleted(seriesIds: List<Long>)

    @Delete
    suspend fun deleteIdea(idea: IdeaData)

    @Query("DELETE FROM idea_data WHERE idea_id IN (:ideaIds)")
    suspend fun deleteIdeaInList(ideaIds: List<Long>)
}