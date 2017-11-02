package com.dwaplatform.android.card.helpers

import java.text.SimpleDateFormat
import java.util.*

/**
 * Date time conversion from and to RFC3339 standard
 */
object DateTimeConversion {
    val format = "yyyy-MM-dd'T'HH:mm:ssXXX"

    fun convert2RFC3339(ts: Date): String =
        SimpleDateFormat(format).format(ts)


    fun convertFromRFC3339(str: String): Date? =
        try {
            Date(SimpleDateFormat(format).parse(str).getTime())
        } catch(e: Exception) {
            null
        }
}
