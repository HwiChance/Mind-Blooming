package com.hwichance.android.mindblooming.rooms.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series_data")
data class SeriesData(
    @PrimaryKey
    @ColumnInfo(name = "series_id") var seriesId: Long?,
    @ColumnInfo(name = "series_title") var seriesTitle: String,
    @ColumnInfo(name = "series_description") var seriesDescription: String,
    @ColumnInfo(name = "series_modified_date") var seriesModifiedDate: Long,
    @ColumnInfo(name = "series_favorite_date") var seriesFavoriteDate: Long? = null,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean = false
)
