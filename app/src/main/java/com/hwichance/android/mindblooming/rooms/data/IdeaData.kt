package com.hwichance.android.mindblooming.rooms.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "idea_data")
data class IdeaData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idea_id") var ideaId: Long?,
    @ColumnInfo(name = "idea_title") var ideaTitle: String,
    @ColumnInfo(name = "created_date") var createdDate: Long,
    @ColumnInfo(name = "modified_date") var modifiedDate: Long,
    @ColumnInfo(name = "is_mind_map") var isMindMap: Boolean,
    @ColumnInfo(name = "series") var seriesId: Long?,
    @ColumnInfo(name = "starred_date") var starredDate: Long? = null,
    @ColumnInfo(name = "series_added_date") var seriesAddedDate: Long? = null,
    @ColumnInfo(name = "is_starred") var isStarred: Boolean = false
) : Serializable