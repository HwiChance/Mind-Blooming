package com.hwichance.android.mindblooming.rooms.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "idea_data")
data class IdeaData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idea_id") var ideaId: Long?,
    @ColumnInfo(name = "idea_title") var ideaTitle: String,
    @ColumnInfo(name = "created_date") var createdDate: Long,
    @ColumnInfo(name = "modified_date") var modifiedDate: Long,
    @ColumnInfo(name = "is_mind_map") var isMindMap: Boolean
)