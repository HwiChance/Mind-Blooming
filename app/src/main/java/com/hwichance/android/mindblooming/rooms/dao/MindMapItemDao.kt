package com.hwichance.android.mindblooming.rooms.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.hwichance.android.mindblooming.rooms.data.MindMapItemData

@Dao
interface MindMapItemDao {
    @Query("SELECT * FROM mind_map_item_data WHERE item_group_id = (:itemGroupId)")
    fun getItemsByGroupId(itemGroupId: Long): LiveData<List<MindMapItemData>>

    @Insert(onConflict = REPLACE)
    suspend fun insertItem(data: MindMapItemData): Long

    @Update
    suspend fun updateItem(data:MindMapItemData)

    @Delete
    suspend fun deleteItem(data: MindMapItemData)
}