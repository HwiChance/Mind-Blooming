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
    fun getIdeasByIds(ideaIds: LongArray): LiveData<List<IdeaData>>

    @Query("SELECT * FROM idea_data WHERE idea_id = :ideaId")
    fun getOneIdeaById(ideaId: Long): LiveData<IdeaData>

    @Query("SELECT * FROM idea_data WHERE is_starred = :isStarred")
    fun getStarredIdea(isStarred: Boolean): LiveData<List<IdeaData>>

    @Insert(onConflict = REPLACE)
    suspend fun insertIdea(idea: IdeaData): Long

    @Update
    suspend fun updateIdea(idea: IdeaData)

    @Delete
    suspend fun deleteIdea(idea: IdeaData)
}