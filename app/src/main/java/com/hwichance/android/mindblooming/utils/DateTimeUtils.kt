package com.hwichance.android.mindblooming.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils {
    companion object {
        fun convertDateToString(prefix: String, date: Long): String {
            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return prefix.plus(" ${dateFormat.format(Date(date))}")
        }

        fun convertDateToFileName(appName: String, date: Long): String {
            val dateFormat = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault())
            return appName.plus("_${dateFormat.format(Date(date))}")
        }
    }
}