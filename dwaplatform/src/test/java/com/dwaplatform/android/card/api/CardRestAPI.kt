package com.dwaplatform.android.card.api

import com.android.volley.Request
import com.android.volley.VolleyError
import com.dwaplatform.android.card.api.CardRestAPI
import com.dwaplatform.android.card.api.volley.VolleyJsonObjectRequest
import com.dwaplatform.android.card.api.volley.VolleyStringRequest
import com.dwaplatform.android.card.helpers.CardHelper
import com.dwaplatform.android.card.helpers.JSONHelper
import com.nhaarman.mockito_kotlin.*
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.*
import java.util.*

class CardRestAPITest {

    @Mock lateinit var queue: IRequestQueue
    @Mock lateinit var requestProvider: IRequestProvider
    @Mock lateinit var jsonHelper: JSONHelper
    private val hostName = "myhostname.com"

    val cardNumber = "1234123412341234"
    val expiration = "1122"
    val cxv = "123"

    lateinit var cardRestAPI: CardRestAPI

    @Captor lateinit var captorMethod: ArgumentCaptor<Int>
    @Captor lateinit var captorQuery: ArgumentCaptor<String>
    @Captor lateinit var captorJsonObjectRequest: ArgumentCaptor<JSONObject>
    @Captor lateinit var captorHeaderRequest: ArgumentCaptor<Map<String, String>>
    @Captor lateinit var captorListenerJSONObject: ArgumentCaptor<(JSONObject) -> Unit>
    @Captor lateinit var captorListenerString: ArgumentCaptor<(String) -> Unit>
    @Captor lateinit var captorHashMapRequest: ArgumentCaptor<HashMap<String, String>>
    @Captor lateinit var captorListenerError: ArgumentCaptor<(VolleyError) -> Unit>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        cardRestAPI = CardRestAPI(hostName, queue, requestProvider, jsonHelper, true)
    }

    @Test
    fun test_getCardSafe_Sandbox() {

        // Given
        val cardToRegister = CardRestAPI.CardToRegister(cardNumber, expiration, cxv)
        val request = Mockito.mock(VolleyJsonObjectRequest::class.java)
        Mockito.`when`(requestProvider.jsonObjectRequest(any(), any(), anyOrNull(), any(), any()))
                .thenReturn(request)

        var handlerCalled = 0
        cardRestAPI.getCardSafe(cardToRegister) { optCardSafe, optErrorCS ->
            // Then
            handlerCalled++;
            Assert.assertNull(optErrorCS)
            Assert.assertNotNull(optCardSafe)

            Assert.assertEquals(cardNumber, optCardSafe?.cardNumber)
            Assert.assertEquals(cxv, optCardSafe?.cvx)
            Assert.assertEquals(expiration, optCardSafe?.expiration)

        }

        verify(requestProvider).jsonObjectRequest(capture(captorMethod), capture(captorQuery),
                capture(captorJsonObjectRequest), capture(captorListenerJSONObject),
                capture(captorListenerError))

        Assert.assertEquals(Request.Method.GET, captorMethod.value)
        Assert.assertEquals("https://myhostname.com/rest/client/user/account/card/test", captorQuery.value)

        Assert.assertNull(captorJsonObjectRequest.value)

        verify(request).setIRetryPolicy(any())
        verify(queue, times(1)).add(any<IRequest<JSONObject>>())

        Assert.assertNotNull(captorListenerError.value)
        Assert.assertNotNull(captorListenerJSONObject.value)

        val jsonObjectReply = Mockito.mock(JSONObject::class.java)
        Mockito.`when`(jsonObjectReply.optString("cardNumber")).thenReturn(cardNumber)
        Mockito.`when`(jsonObjectReply.optString("expiration")).thenReturn(expiration)
        Mockito.`when`(jsonObjectReply.optString("cxv")).thenReturn(cxv)

        captorListenerJSONObject.value.invoke(jsonObjectReply)

        Assert.assertEquals(1, handlerCalled)
    }

    @Test
    fun test_getCardSafe_NotSandbox() {

        // Given
        cardRestAPI = CardRestAPI(hostName, queue, requestProvider, jsonHelper, false)
        val cardToRegister = CardRestAPI.CardToRegister(cardNumber, expiration, cxv)

        var handlerCalled = 0
        // When
        cardRestAPI.getCardSafe(cardToRegister) { optCardSafe, optErrorCS ->
            // Then
            handlerCalled++;
            Assert.assertNull(optErrorCS)
            Assert.assertNotNull(optCardSafe)

            Assert.assertEquals(cardNumber, optCardSafe?.cardNumber)
            Assert.assertEquals(cxv, optCardSafe?.cvx)
            Assert.assertEquals(expiration, optCardSafe?.expiration)

        }

        Assert.assertEquals(1, handlerCalled)
    }

    @Test
    fun test_postCardRegister() {

        // Given

        val request = Mockito.mock(VolleyJsonObjectRequest::class.java)
        Mockito.`when`(requestProvider.jsonObjectRequest(any(), any(), any(), any(), any(), any()))
                .thenReturn(request)

        val jsonRequest = Mockito.mock(JSONObject::class.java)
        Mockito.`when`(jsonHelper.buildJSONObject()).thenReturn(jsonRequest)

        var handlerCalled = 0
        cardRestAPI.postCardRegister(token = "123", alias = "1111XXXX1111", expiration = "1122") { optCardRegistration, optErrorCS ->
            // Then
            handlerCalled++;
            Assert.assertNull(optErrorCS)
            Assert.assertNotNull(optCardRegistration)

            Assert.assertEquals("tokenCard123", optCardRegistration?.tokenCard)
            Assert.assertEquals("preregistrationDataABC", optCardRegistration?.preregistrationData)
            Assert.assertEquals("cardRegistrationId123", optCardRegistration?.cardRegistrationId)
            Assert.assertEquals("accessKey123", optCardRegistration?.accessKey)
            Assert.assertEquals("https://example.com", optCardRegistration?.url)

        }

        verify(requestProvider).jsonObjectRequest(capture(captorMethod), capture(captorQuery),
                capture(captorJsonObjectRequest), capture(captorHeaderRequest),
                capture(captorListenerJSONObject),
                capture(captorListenerError))

        Assert.assertEquals(Request.Method.POST, captorMethod.value)
        Assert.assertEquals("https://myhostname.com/rest/client/user/account/card/register", captorQuery.value)

        Assert.assertEquals("Bearer 123", captorHeaderRequest.value["Authorization"])

        Assert.assertNotNull(captorJsonObjectRequest.value)

        verify(jsonRequest).put("alias", "1111XXXX1111")
        verify(jsonRequest).put("expiration", "1122")

        verify(request).setIRetryPolicy(any())
        verify(queue, times(1)).add(any<IRequest<JSONObject>>())

        Assert.assertNotNull(captorListenerError.value)
        Assert.assertNotNull(captorListenerJSONObject.value)


        val jsonObjectReply = Mockito.mock(JSONObject::class.java)
        Mockito.`when`(jsonObjectReply.getString("cardRegistrationId")).thenReturn("cardRegistrationId123")
        Mockito.`when`(jsonObjectReply.getString("url")).thenReturn("https://example.com")
        Mockito.`when`(jsonObjectReply.getString("preregistrationData")).thenReturn("preregistrationDataABC")
        Mockito.`when`(jsonObjectReply.getString("accessKey")).thenReturn("accessKey123")
        Mockito.`when`(jsonObjectReply.getString("tokenCard")).thenReturn("tokenCard123")

        captorListenerJSONObject.value.invoke(jsonObjectReply)

        Assert.assertEquals(1, handlerCalled)
    }

    @Test
    fun test_postCardRegistrationData() {

        // Given

        val request = Mockito.mock(VolleyStringRequest::class.java)
        Mockito.`when`(requestProvider.stringRequest(any(), any(), any(), any(), any(), any()))
                .thenReturn(request)

        val cardToRegister = CardRestAPI.CardToRegister(cardNumber, expiration, cxv)
        val cardRegistration = CardRestAPI.CardRegistration("crid123",
                "https://example.com", "pd123", "ak123", tokenCard="tokenCard123")

        var handlerCalled = 0
        cardRestAPI.postCardRegistrationData(cardToRegister, cardRegistration) { optRegistration, optError ->
            // Then
            handlerCalled++;
            Assert.assertNull(optError)
            Assert.assertNotNull(optRegistration)

            Assert.assertEquals("regnumABC", optRegistration)
        }

        verify(requestProvider).stringRequest(capture(captorMethod), capture(captorQuery),
                capture(captorHashMapRequest), capture(captorHeaderRequest),
                capture(captorListenerString),
                capture(captorListenerError))

        Assert.assertEquals(Request.Method.POST, captorMethod.value)
        Assert.assertEquals("https://example.com", captorQuery.value)

        Assert.assertNotNull(captorHeaderRequest.value["Content-Type"])
        Assert.assertNotNull(captorHashMapRequest)

        verify(request).setIRetryPolicy(any())
        verify(queue, times(1)).add(any<IRequest<JSONObject>>())

        Assert.assertNotNull(captorListenerError.value)
        Assert.assertNotNull(captorListenerString.value)

        captorListenerString.value.invoke("regnumABC")

        Assert.assertEquals(1, handlerCalled)
    }


    @Test
    fun test_putRegisterCard() {

        // Given

        val request = Mockito.mock(VolleyJsonObjectRequest::class.java)
        Mockito.`when`(requestProvider.jsonObjectRequest(any(), any(), any(), any(), any(), any()))
                .thenReturn(request)

        val jsonRequest = Mockito.mock(JSONObject::class.java)
        Mockito.`when`(jsonHelper.buildJSONObject()).thenReturn(jsonRequest)

        var handlerCalled = 0
        cardRestAPI.putRegisterCard(token = "123", cardRegistrationId = "9876", registration = "reg123")
        { optCard, optError ->
            // Then
            handlerCalled++;
            Assert.assertNull(optError)
            Assert.assertNotNull(optCard)

            Assert.assertEquals("1234XXXX", optCard?.alias)
            //Assert.assertEquals("preregistrationDataABC", optCard?.create)
            Assert.assertEquals("EUR", optCard?.currency)
            Assert.assertEquals(true, optCard?.default)
            Assert.assertEquals("1122", optCard?.expiration)
            Assert.assertEquals("idABC", optCard?.id)
            Assert.assertEquals("OK", optCard?.status)
            Assert.assertEquals("1234-5678", optCard?.token)


        }

        verify(requestProvider).jsonObjectRequest(capture(captorMethod), capture(captorQuery),
                capture(captorJsonObjectRequest), capture(captorHeaderRequest),
                capture(captorListenerJSONObject),
                capture(captorListenerError))

        Assert.assertEquals(Request.Method.PUT, captorMethod.value)
        Assert.assertEquals("https://myhostname.com/rest/client/user/account/card/register/9876", captorQuery.value)

        Assert.assertEquals("Bearer 123", captorHeaderRequest.value["Authorization"])

        Assert.assertNotNull(captorJsonObjectRequest.value)

        verify(jsonRequest).put("registration", "reg123")

        verify(request).setIRetryPolicy(any())
        verify(queue, times(1)).add(any<IRequest<JSONObject>>())

        Assert.assertNotNull(captorListenerError.value)
        Assert.assertNotNull(captorListenerJSONObject.value)


        val jsonObjectReply = Mockito.mock(JSONObject::class.java)
        Mockito.`when`(jsonObjectReply.optString("create")).thenReturn("2017-07-22T09:17:00.000-07:00")
        Mockito.`when`(jsonObjectReply.optString("id")).thenReturn("idABC")
        Mockito.`when`(jsonObjectReply.optString("alias")).thenReturn("1234XXXX")
        Mockito.`when`(jsonObjectReply.optString("expiration")).thenReturn("1122")
        Mockito.`when`(jsonObjectReply.optString("currency")).thenReturn("EUR")
        Mockito.`when`(jsonObjectReply.optBoolean("default")).thenReturn(true)
        Mockito.`when`(jsonObjectReply.optString("status")).thenReturn("OK")
        Mockito.`when`(jsonObjectReply.optString("token")).thenReturn("1234-5678")

        captorListenerJSONObject.value.invoke(jsonObjectReply)

        Assert.assertEquals(1, handlerCalled)
    }

}
