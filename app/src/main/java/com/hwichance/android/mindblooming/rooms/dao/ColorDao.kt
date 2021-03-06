package com.hwichance.android.mindblooming.rooms.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.hwichance.android.mindblooming.rooms.data.ColorData

@Dao
interface ColorDao {
    @Query("SELECT * FROM color_data ORDER BY last_used_date DESC")
    fun getColorData(): LiveData<List<ColorData>>

    @Insert(onConflict = REPLACE)
    suspend fun insertData(data: ColorData)

    @Delete
    suspend fun deleteData(data: ColorData)
}