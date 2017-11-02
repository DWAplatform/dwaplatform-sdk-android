package com.dwaplatform.android.card.helpers

import org.junit.Assert
import org.junit.Test
import java.util.*

class DataTimeConversionTest {


    @Test
    fun test_checkItems() {
        // Given
        val date = Calendar.getInstance()
        date.set(2012, 6, 22, 9, 17, 0)

        date.get(Calendar.HOUR_OF_DAY)
        // When
        val result = DateTimeConversion.convert2RFC3339(date.time)

        // Then
        Assert.assertTrue(result.contains("2012-07-22T09:17:00"))

    }

    @Test
    fun test_convertFromRFC3339() {
        // When
        val result = DateTimeConversion.convertFromRFC3339("2012-07-22T09:17:00+00:00")

        // Then
        Assert.assertEquals(1342948620000, result?.time)
    }
}