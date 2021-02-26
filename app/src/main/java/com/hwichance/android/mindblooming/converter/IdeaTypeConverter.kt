package com.hwichance.android.mindblooming.converter

import androidx.room.TypeConverter
import com.hwichance.android.mindblooming.custom_views.flexible_view_use.ItemPosEnum

class IdeaTypeConverter {
    @TypeConverter
    fun toPositionEnum(pos: Int): ItemPosEnum {
        return when (pos) {
            0 -> ItemPosEnum.PRIMARY
            1 -> ItemPosEnum.LEFT
            else -> ItemPosEnum.RIGHT
        }
    }

    @TypeConverter
    fun fromPositionEnum(pos: ItemPosEnum): Int {
        return when (pos) {
            ItemPosEnum.PRIMARY -> 0
            ItemPosEnum.LEFT -> 1
            ItemPosEnum.RIGHT -> 2
        }
    }
}