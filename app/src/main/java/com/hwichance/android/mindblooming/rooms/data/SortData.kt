package com.hwichance.android.mindblooming.rooms.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hwichance.android.mindblooming.enums.OrderEnum
import com.hwichance.android.mindblooming.enums.SortCaller
import com.hwichance.android.mindblooming.enums.SortEnum
import java.io.Serializable

@Entity(tableName = "sort_data")
data class SortData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sort_id") var sortId: Long?,
    @ColumnInfo(name = "caller") var caller: SortCaller,
    @ColumnInfo(name = "sort_enum") var sortEnum: SortEnum = SortEnum.TITLE,
    @ColumnInfo(name = "order_enum") var orderEnum: OrderEnum = OrderEnum.ASC
) : Serializable