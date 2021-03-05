package com.hwichance.android.mindblooming.rooms.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hwichance.android.mindblooming.enums.OrderEnum
import com.hwichance.android.mindblooming.enums.SortEnum

@Entity(tableName = "sort_data")
data class SortData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sort_id") var sortId: Long?,
    @ColumnInfo(name = "sort_enum") var sortEnum: SortEnum = SortEnum.TITLE,
    @ColumnInfo(name = "order_enum") var orderEnum: OrderEnum = OrderEnum.ASC
)