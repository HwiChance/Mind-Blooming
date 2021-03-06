package com.hwichance.android.mindblooming.rooms.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "color_data")
data class ColorData(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "color") var color: Int,
    @ColumnInfo(name = "last_used_date") var lastUsedDate: Long
)