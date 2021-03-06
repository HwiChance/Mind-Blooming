package com.hwichance.android.mindblooming.rooms.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.*
import com.hwichance.android.mindblooming.enums.SortCaller
import com.hwichance.android.mindblooming.rooms.data.SortData

@Dao
interface SortDao {
    @Query("SELECT * FROM sort_data ORDER BY sort_id LIMIT 1")
    fun getSortData(): LiveData<SortData>

    @Query("SELECT * FROM sort_data WHERE caller = :caller ORDER BY sort_id LIMIT 1")
    fun getSortDataByCaller(caller: SortCaller): LiveData<SortData>

    @Insert(onConflict = REPLACE)
    suspend fun insertSortData(data: SortData)

    @Delete
    suspend fun deleteSortData(data: SortData)
}