package com.hwichance.android.mindblooming.rooms.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hwichance.android.mindblooming.enums.ItemPosEnum

@Entity(tableName = "mind_map_item_data")
data class MindMapItemData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id") var itemId: Long? = null,
    @ColumnInfo(name = "item_parent_id") var parentId: Long? = null,
    @ColumnInfo(name = "item_text") var itemText: String = "",
    @ColumnInfo(name = "item_pos") var itemPos: ItemPosEnum,
    @ColumnInfo(name = "item_background_color") var backgroundColor: Int,
    @ColumnInfo(name = "item_text_color") var textColor: Int,
    @ColumnInfo(name = "item_group_id") var itemGroup: Long
)
