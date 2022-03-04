package fi.metropolia.intellicircumstances.util

import java.text.DateFormat
import java.util.*

class DateUtil {
    companion object {
        fun getFormattedDate(date: Long): String {
            val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            return dateFormat.format(Date(date))
        }
    }
}