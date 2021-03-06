package com.hwichance.android.mindblooming.rooms.converter

import androidx.room.TypeConverter
import com.hwichance.android.mindblooming.enums.ItemPosEnum
import com.hwichance.android.mindblooming.enums.OrderEnum
import com.hwichance.android.mindblooming.enums.SortCaller
import com.hwichance.android.mindblooming.enums.SortEnum

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

    @TypeConverter
    fun toSortEnum(sort: Int): SortEnum {
        return when (sort) {
            0 -> SortEnum.TITLE
            1 -> SortEnum.CREATED_DATE
            2 -> SortEnum.LAST_MODIFIED_DATE
            3 -> SortEnum.STARRED_DATE
            else -> SortEnum.SERIES_ADDED_DATE
        }
    }

    @TypeConverter
    fun fromSortEnum(sort: SortEnum): Int {
        return when (sort) {
            SortEnum.TITLE -> 0
            SortEnum.CREATED_DATE -> 1
            SortEnum.LAST_MODIFIED_DATE -> 2
            SortEnum.STARRED_DATE -> 3
            SortEnum.SERIES_ADDED_DATE -> 4
        }
    }

    @TypeConverter
    fun toOrderEnum(order: Int): OrderEnum {
        return when (order) {
            0 -> OrderEnum.ASC
            else -> OrderEnum.DES
        }
    }

    @TypeConverter
    fun fromOrderEnum(order: OrderEnum): Int {
        return when (order) {
            OrderEnum.ASC -> 0
            OrderEnum.DES -> 1
        }
    }

    @TypeConverter
    fun toCallerEnum(caller: Int): SortCaller {
        return when (caller) {
            0 -> SortCaller.MAIN
            1 -> SortCaller.STARRED
            else -> SortCaller.SERIES
        }
    }

    @TypeConverter
    fun fromCallerEnum(caller: SortCaller): Int {
        return when (caller) {
            SortCaller.MAIN -> 0
            SortCaller.STARRED -> 1
            SortCaller.SERIES -> 2
        }
    }
}