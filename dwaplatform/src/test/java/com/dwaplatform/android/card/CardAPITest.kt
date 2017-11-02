package com.dwaplatform.android.card

import com.dwaplatform.android.card.api.CardRestAPI
import com.dwaplatform.android.card.helpers.CardHelper
import com.dwaplatform.android.card.helpers.SanityCheckException
import com.dwaplatform.android.card.helpers.SanityItem
import com.dwaplatform.android.card.log.Log
import com.dwaplatform.android.card.models.Card
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.lang.Exception
import java.util.*

class CardAPITest {

    @Mock lateinit var log: Log
    @Mock lateinit var cardHelper: CardHelper
    @Mock lateinit var restAPI: CardRestAPI

    val cardNumber = "1234123412341234"
    val expiration = "1122"
    val cxv = "123"

    lateinit var cardAPI: CardAPI

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        cardAPI = CardAPI(restAPI, log, cardHelper)
    }

    @Test
    fun registerCard_CardWrongFormat() {
        // Given
        val exception = SanityCheckException(SanityItem("field", "value", "regExp"))
        Mockito.`when`(cardHelper.checkCardFormat(cardNumber, expiration, cxv))
                .thenThrow(exception)

        try {
            // When
            cardAPI.registerCard("token", cardNumber, expiration, cxv) { optCard, optException -> }

            // Then
            Assert.fail("Expected throw Card Format Sanity Check exception")
        } catch(e: SanityCheckException) {
            // Then
            Assert.assertEquals(exception, e)
        }
    }

    @Test
    fun registerCard_postCardRegisterFailed() {
        // Given
        Mockito.`when`(cardHelper.generateAlias(cardNumber)).thenReturn("1234XXXXXXXX1234")
        val e = Exception()

        var handlerCalled = 0
        // When
        cardAPI.registerCard("1111-1111", cardNumber, expiration, cxv) { optCard, optException ->
            // Then
            handlerCalled++
            Assert.assertNull(optCard)
            Assert.assertNotNull(optException)
            Assert.assertEquals(e, optException!!)
        }

        // Then
        val captorToken = argumentCaptor<String>()
        val captorAlias = argumentCaptor<String>()
        val captorExpiration = argumentCaptor<String>()
        val captorHandler = argumentCaptor<(CardRestAPI.CardRegistration?, Exception?) -> Unit>()
        verify(restAPI).postCardRegister(captorToken.capture(), captorAlias.capture(),
                captorExpiration.capture(), captorHandler.capture())

        Assert.assertEquals("1111-1111", captorToken.lastValue)
        Assert.assertEquals("1234XXXXXXXX1234", captorAlias.lastValue)
        Assert.assertEquals(expiration, captorExpiration.lastValue)
        Assert.assertNotNull(captorHandler.lastValue)

        captorHandler.lastValue.invoke(null, e)
        Assert.assertEquals(1, handlerCalled)

    }

    @Test
    fun registerCard_postCardRegisterSuccess_getCardSafeCalledFailed() {
        // Given
        Mockito.`when`(cardHelper.generateAlias(cardNumber)).thenReturn("1234XXXXXXXX1234")
        val e = Exception()

        var handlerCalled = 0
        // When
        cardAPI.registerCard("1111-1111", cardNumber, expiration, cxv) { optCard, optException ->
            // Then
            handlerCalled++
            Assert.assertNull(optCard)
            Assert.assertNotNull(optException)
            Assert.assertEquals(e, optException!!)
        }

        val captorPostCardRegisterHandler = argumentCaptor<(CardRestAPI.CardRegistration?, Exception?) -> Unit>()
        verify(restAPI).postCardRegister(any(), any(),
                any(), captorPostCardRegisterHandler.capture())

        val cardRegistration = CardRestAPI.CardRegistration(cardRegistrationId = "123",
        url = "https://example.com", preregistrationData = "preReg", accessKey = "key",
                tokenCard = "123-456")
        captorPostCardRegisterHandler.lastValue.invoke(cardRegistration, null)

        // Then
        val captorCardToRegister = argumentCaptor<CardRestAPI.CardToRegister>()
        val captorHandler = argumentCaptor<(CardRestAPI.CardToRegister?, Exception?) -> Unit>()
        verify(restAPI).getCardSafe(captorCardToRegister.capture(), captorHandler.capture())

        Assert.assertEquals(cardNumber, captorCardToRegister.lastValue.cardNumber)
        Assert.assertEquals(expiration, captorCardToRegister.lastValue.expiration)
        Assert.assertEquals(cxv, captorCardToRegister.lastValue.cvx)

        captorHandler.lastValue.invoke(null, e)
        Assert.assertEquals(1, handlerCalled)

    }

    @Test
    fun registerCard_postCardRegisterSuccess_getCardSafeCalledSuccess_postCardRegistrationDataFailed() {
        // Given
        Mockito.`when`(cardHelper.generateAlias(cardNumber)).thenReturn("1234XXXXXXXX1234")
        val e = Exception()

        var handlerCalled = 0
        // When
        cardAPI.registerCard("1111-1111", cardNumber, expiration, cxv) { optCard, optException ->
            // Then
            handlerCalled++
            Assert.assertNull(optCard)
            Assert.assertNotNull(optException)
            Assert.assertEquals(e, optException!!)
        }

        val captorPostCardRegisterHandler = argumentCaptor<(CardRestAPI.CardRegistration?, Exception?) -> Unit>()
        verify(restAPI).postCardRegister(any(), any(),
                any(), captorPostCardRegisterHandler.capture())

        val cardRegistration = CardRestAPI.CardRegistration(cardRegistrationId = "123",
                url = "https://example.com", preregistrationData = "preReg", accessKey = "key",
                tokenCard = "123-456")
        captorPostCardRegisterHandler.lastValue.invoke(cardRegistration, null)

        val captorCardSafeHandler = argumentCaptor<(CardRestAPI.CardToRegister?, Exception?) -> Unit>()
        verify(restAPI).getCardSafe(any(), captorCardSafeHandler.capture())

        val cardToRegister = CardRestAPI.CardToRegister(cardNumber, expiration, cxv)
        captorCardSafeHandler.lastValue.invoke(cardToRegister, null)


        // Then
        val captorCardToRegister = argumentCaptor<CardRestAPI.CardToRegister>()
        val captorCardRegistration = argumentCaptor<CardRestAPI.CardRegistration>()
        val captorHandler = argumentCaptor<(String?, Exception?) -> Unit>()
        verify(restAPI).postCardRegistrationData(captorCardToRegister.capture(), captorCardRegistration.capture(),
                captorHandler.capture())

        Assert.assertEquals(cardNumber, captorCardToRegister.lastValue.cardNumber)
        Assert.assertEquals(expiration, captorCardToRegister.lastValue.expiration)
        Assert.assertEquals(cxv, captorCardToRegister.lastValue.cvx)

        Assert.assertEquals("key", captorCardRegistration.lastValue.accessKey)
        Assert.assertEquals("123", captorCardRegistration.lastValue.cardRegistrationId)
        Assert.assertEquals("preReg", captorCardRegistration.lastValue.preregistrationData)
        Assert.assertEquals("123-456", captorCardRegistration.lastValue.tokenCard)
        Assert.assertEquals("https://example.com", captorCardRegistration.lastValue.url)

        captorHandler.lastValue.invoke(null, e)
        Assert.assertEquals(1, handlerCalled)

    }

    @Test
    fun registerCard_postCardRegisterSuccess_getCardSafeCalledSuccess_postCardRegistrationDataSuccess_putRegisterCardFailed() {
        // Given
        Mockito.`when`(cardHelper.generateAlias(cardNumber)).thenReturn("1234XXXXXXXX1234")
        val e = Exception()

        var handlerCalled = 0
        // When
        cardAPI.registerCard("1111-1111", cardNumber, expiration, cxv) { optCard, optException ->
            // Then
            handlerCalled++
            Assert.assertNull(optCard)
            Assert.assertNotNull(optException)
            Assert.assertEquals(e, optException!!)
        }

        val captorPostCardRegisterHandler = argumentCaptor<(CardRestAPI.CardRegistration?, Exception?) -> Unit>()
        verify(restAPI).postCardRegister(any(), any(),
                any(), captorPostCardRegisterHandler.capture())

        val cardRegistration = CardRestAPI.CardRegistration(cardRegistrationId = "123",
                url = "https://example.com", preregistrationData = "preReg", accessKey = "key",
                tokenCard = "123-456")
        captorPostCardRegisterHandler.lastValue.invoke(cardRegistration, null)

        val captorCardSafeHandler = argumentCaptor<(CardRestAPI.CardToRegister?, Exception?) -> Unit>()
        verify(restAPI).getCardSafe(any(), captorCardSafeHandler.capture())

        val cardToRegister = CardRestAPI.CardToRegister(cardNumber, expiration, cxv)
        captorCardSafeHandler.lastValue.invoke(cardToRegister, null)

        val captorPostCardRegistrationDataHandler = argumentCaptor<(String?, Exception?) -> Unit>()
        verify(restAPI).postCardRegistrationData(any(), any(), captorPostCardRegistrationDataHandler.capture())


        captorPostCardRegistrationDataHandler.lastValue.invoke("registration123", null)

        // Then
        val captorToken = argumentCaptor<String>()
        val captorCardRegistrationId = argumentCaptor<String>()
        val captorRegistration = argumentCaptor<String>()
        val captorHandler = argumentCaptor<(Card?, Exception?) -> Unit>()
        verify(restAPI).putRegisterCard(captorToken.capture(), captorCardRegistrationId.capture(),
                captorRegistration.capture(), captorHandler.capture())

        Assert.assertEquals("1111-1111", captorToken.lastValue)
        Assert.assertEquals("123", captorCardRegistrationId.lastValue)
        Assert.assertEquals("registration123", captorRegistration.lastValue)
        Assert.assertNotNull(captorHandler.lastValue)


        captorHandler.lastValue.invoke(null, e)
        Assert.assertEquals(1, handlerCalled)


    }

    @Test
    fun registerCard_Success() {
        // Given
        Mockito.`when`(cardHelper.generateAlias(cardNumber)).thenReturn("1234XXXXXXXX1234")
        val card = Card("123456", "1234XXXXXXXX1234", "123", "EUR",
                true, "GOOD", "ABC", Calendar.getInstance().time)

        var handlerCalled = 0
        // When
        cardAPI.registerCard("1111-1111", cardNumber, expiration, cxv) { optCard, optException ->
            // Then
            handlerCalled++
            Assert.assertNotNull(optCard)
            Assert.assertNull(optException)
            Assert.assertEquals(card, optCard!!)
        }

        val captorPostCardRegisterHandler = argumentCaptor<(CardRestAPI.CardRegistration?, Exception?) -> Unit>()
        verify(restAPI).postCardRegister(any(), any(),
                any(), captorPostCardRegisterHandler.capture())

        val cardRegistration = CardRestAPI.CardRegistration(cardRegistrationId = "123",
                url = "https://example.com", preregistrationData = "preReg", accessKey = "key",
                tokenCard = "123-456")
        captorPostCardRegisterHandler.lastValue.invoke(cardRegistration, null)

        val captorCardSafeHandler = argumentCaptor<(CardRestAPI.CardToRegister?, Exception?) -> Unit>()
        verify(restAPI).getCardSafe(any(), captorCardSafeHandler.capture())

        val cardToRegister = CardRestAPI.CardToRegister(cardNumber, expiration, cxv)
        captorCardSafeHandler.lastValue.invoke(cardToRegister, null)

        val captorPostCardRegistrationDataHandler = argumentCaptor<(String?, Exception?) -> Unit>()
        verify(restAPI).postCardRegistrationData(any(), any(), captorPostCardRegistrationDataHandler.capture())


        captorPostCardRegistrationDataHandler.lastValue.invoke("registration123", null)

        // Then
        val captorHandler = argumentCaptor<(Card?, Exception?) -> Unit>()
        verify(restAPI).putRegisterCard(any(), any(),
                any(), captorHandler.capture())

        captorHandler.lastValue.invoke(card, null)
        Assert.assertEquals(1, handlerCalled)

    }

}