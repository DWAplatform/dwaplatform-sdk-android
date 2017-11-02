package com.dwaplatform.android.card.helpers

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CardHelperTest {

    lateinit var cardHelper: CardHelper

    @Before
    fun setUp() {
        cardHelper = CardHelper(SanityCheck())
    }

    @Test
    fun test_generateAlias() {
        // When
        val result = cardHelper.generateAlias("1111222233334444")

        // Then
        Assert.assertNotEquals("1234123412341234", result)
        Assert.assertTrue(result.contains("XXXXXX"))
        Assert.assertEquals(16, result.length)

    }

    @Test
    fun test_checkCardNumberFormat_Correct() {

        // When
        cardHelper.checkCardNumberFormat("1111222233334444")
        cardHelper.checkCardNumberFormat("1234123412341234")

        // Then never throw
    }

    @Test
    fun test_checkCardNumberFormat_WrongFormat() {
        try {
            // When
            cardHelper.checkCardNumberFormat("12341234112341234")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }

        try {
            // When
            cardHelper.checkCardNumberFormat("123412A412341234")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }

        try {
            // When
            cardHelper.checkCardNumberFormat("123412341234123-")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }


    }

    @Test
    fun test_checkExpirationFormat_Correct() {

        // When
        cardHelper.checkCardExpirationFormat("1122")
        cardHelper.checkCardExpirationFormat("8899")

        // Then never throw
    }

    @Test
    fun test_checkExpirationFormat_WrongFormat() {
        try {
            // When
            cardHelper.checkCardExpirationFormat("11223")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }

        try {
            // When
            cardHelper.checkCardExpirationFormat("1A23")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }

        try {
            // When
            cardHelper.checkCardExpirationFormat(" 123")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }


    }

    @Test
    fun test_checkCardCXVFormat_Correct() {

        // When
        cardHelper.checkCardCXVFormat("123")
        cardHelper.checkCardCXVFormat("999")

        // Then never throw
    }

    @Test
    fun test_checkCardCXVFormat_WrongFormat() {
        try {
            // When
            cardHelper.checkCardCXVFormat("1123")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }

        try {
            // When
            cardHelper.checkCardCXVFormat("A12")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }

        try {
            // When
            cardHelper.checkCardCXVFormat("  12")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }
    }

    @Test
    fun test_checkCardFormat_Correct() {

        // When
        cardHelper.checkCardFormat("1234123412341234", "1122", "123")
        cardHelper.checkCardFormat("1111222233334444", "9988", "999")

        // Then never throw
    }

    @Test
    fun test_checkCardFormat_WrongFormat() {
        try {
            // When
            cardHelper.checkCardFormat("12341234123412346", "1122", "123")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }

        try {
            // When
            cardHelper.checkCardFormat("1234123412341234", "112A", "123")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }

        try {
            // When
            cardHelper.checkCardFormat("1234123412341234", "1122", "13")
            Assert.fail()
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertNotNull(e.message)
        }


    }


}