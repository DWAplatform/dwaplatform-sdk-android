package com.dwaplatform.android.card.helpers

import org.junit.Assert
import org.junit.Test

class SanityCheckTest {

    @Test
    fun test_checkItems() {
        // Given
        val a = SanityItem("A", "10", """\d{2}""")
        val b = SanityItem("B", "1", """\d{2}""")
        val c = SanityItem("C", "100", """\d{2}""")
        val d = SanityItem("D", "100", """\d{3}""")
        val e = SanityItem("E", "1000", """\d{3}""")

        val sanityCheck = SanityCheck()
        // When
        val result = sanityCheck.check(listOf(a, b, c, d, e))

        // Then
        Assert.assertEquals(3, result.size)
        Assert.assertEquals(b, result[0])
        Assert.assertEquals(c, result[1])
        Assert.assertEquals(e, result[2])

    }

    @Test
    fun test_checkThrowException_Success() {
        // Given
        val sanityCheck = SanityCheck()
        // When
        sanityCheck.checkThrowException(SanityItem("A", "10", """\d{2}"""))

        // Then do not throw
    }

    @Test
    fun test_checkThrowException_Fail() {
        // Given
        val sanityCheck = SanityCheck()

        try {
            // When
            sanityCheck.checkThrowException(SanityItem("A", "10", """\d{3}"""))

            // Then
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertTrue(e.message?.contains("A") ?: false)
            Assert.assertTrue(e.message?.contains("10")?: false)
            Assert.assertTrue(e.message?.contains("\\d{3}")?: false)
        }

    }
}