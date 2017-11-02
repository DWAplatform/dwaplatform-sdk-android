package com.dwaplatform.android.card.api


import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.VolleyError
import com.dwaplatform.android.card.CardAPI
import com.dwaplatform.android.card.helpers.DateTimeConversion
import com.dwaplatform.android.card.helpers.JSONHelper
import com.dwaplatform.android.card.models.Card
import org.json.JSONException
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*

/**
 * DWAplatform rest API communication class.
 * Please do not use directly, use CardAPI facade instead.
 */
open class CardRestAPI constructor(
        internal val hostName: String,
        internal val queue: IRequestQueue,
        internal val requestProvider: IRequestProvider,
        internal val jsonHelper: JSONHelper,
        internal val sandbox: Boolean) {


    private val PROTOCOL_CHARSET = "utf-8"

    private fun getURL(path: String): String = "https://$hostName$path"

    data class CardToRegister(val cardNumber: String,
                              val expiration: String,
                              val cvx: String)


    data class CardRegistration(val cardRegistrationId: String,
                                val url: String,
                                val preregistrationData: String,
                                val accessKey: String,
                                val tokenCard: String? = null)


    private val defaultpolicy = DefaultRetryPolicy(
            30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

    private fun authorizationToken(token: String): Map<String, String> {
        val header = HashMap<String, String>()
        header.put("Authorization", "Bearer $token")
        return header
    }

    private fun createRequestError(volleyError: VolleyError): CardAPI.APIReplyError {

        try {
            val response: NetworkResponse = volleyError.networkResponse
            val jsonString = String(response.data, Charset.forName(PROTOCOL_CHARSET))

            return CardAPI.APIReplyError(jsonHelper.buildJSONArray(jsonString), volleyError)
        } catch(e: JSONException) {
            return CardAPI.APIReplyError(null, volleyError)
        }
    }

    /**
     * Get Test Card data to use on sandbox environment.
     * If not in sandbox, will be returned the card dato get from cardFrom parameter.
     *
     * @param cardFrom original card data, to use only in production environment
     * @param completionHandler callback containing the card data to use for registration.
     */
    open fun getCardSafe(cardFrom: CardToRegister,
                            completionHandler: (CardToRegister?, Exception?) -> Unit) {

        if (!sandbox) {
            completionHandler(CardToRegister(cardFrom.cardNumber, cardFrom.expiration, cardFrom.cvx), null)
        } else {

            val url = getURL("/rest/client/user/account/card/test")

            val request = requestProvider.jsonObjectRequest(Request.Method.GET, url, null,
                    { response ->
                        try {

                            val cardToRegister = CardToRegister(response.optString("cardNumber"),
                                    response.optString("expiration"),
                                    response.optString("cxv"))

                            completionHandler(cardToRegister, null)
                        } catch (e: JSONException) {
                            completionHandler(null, CardAPI.ParseReplyParamsException(e))
                        }
                    }) { error -> completionHandler(null, createRequestError(error)) }

            request.setIRetryPolicy(defaultpolicy)
            queue.add(request)
        }

    }

    /**
     * Create a new registration card request, to obtain data useful to send to the card tokenizer service
     *
     * @param token dwaplatform token as get from create card post request
     * @param alias card number alias
     * @param expiration card expiration
     * @param completionHandler callback containing card registration object
     */
    open fun postCardRegister(token: String, alias: String, expiration: String,
                                 completionHandler: (CardRegistration?, Exception?) -> Unit) {

        val url = getURL("/rest/client/user/account/card/register")

        val params = jsonHelper.buildJSONObject()
        params.put("alias", alias)
        params.put("expiration", expiration)

        val request = requestProvider.jsonObjectRequest(
                Request.Method.POST, url, params, authorizationToken(token),
                { response ->

                    try {
                        val cardRegistration =
                                CardRegistration(
                                        response.getString("cardRegistrationId"),
                                        response.getString("url"),
                                        response.getString("preregistrationData"),
                                        response.getString("accessKey"),
                                        response.getString("tokenCard")
                                )
                        completionHandler(cardRegistration, null)
                    } catch (e: JSONException) {
                        completionHandler(null, CardAPI.ParseReplyParamsException(e))
                    }
                })
        { error -> completionHandler(null, createRequestError(error)) }

        request.setIRetryPolicy(defaultpolicy)
        queue.add(request)
    }

    /**
     * Send card registration to card tokenizer service
     *
     * @param card actual card data to tokenize
     * @param cardRegistration card registration data to authorize the tokenization
     * @param completionHandler callback containing registration key
     */
    open fun postCardRegistrationData(card: CardToRegister,
                                         cardRegistration: CardRegistration,
                                         completionHandler: (String?, Exception?) -> Unit) {

        val url = cardRegistration.url

        val params = HashMap<String, String>()
        params.put("data", cardRegistration.preregistrationData)
        params.put("accessKeyRef", cardRegistration.accessKey)
        params.put("cardNumber", card.cardNumber)
        params.put("cardExpirationDate", card.expiration)
        params.put("cardCvx", card.cvx)

        val header = HashMap<String, String>()
        header.put("Content-Type", "application/x-www-form-urlencoded")

        val request = requestProvider.stringRequest(Request.Method.POST, url, params, header, { response ->
            completionHandler(response, null)
        })
        { error -> completionHandler(null, createRequestError(error)) }

        request.setIRetryPolicy(defaultpolicy)
        queue.add(request)
    }

    /**
     * Complete card registration process.
     * @param token dwaplatform token as get from create card post request
     * @param cardRegistrationId univoke id obtained from card registration process
     * @param registration registration key obtained from tokenizer service
     * @param completionHandler callback containing the Card object
     */
    open fun putRegisterCard(token: String,
                                cardRegistrationId: String,
                                registration: String,
                                completionHandler: (Card?, Exception?) -> Unit) {

        val url = getURL("/rest/client/user/account/card/register/$cardRegistrationId")

        val params = jsonHelper.buildJSONObject()
        params.put("registration", registration)

        val request = requestProvider.jsonObjectRequest(Request.Method.PUT, url, params,
                authorizationToken(token),
                { response ->
                    try {
                        val createOpt: String? = response.optString("create")
                        val crateDate = createOpt?.let { create ->
                            DateTimeConversion.convertFromRFC3339(create)
                        }

                        val card = Card(response.optString("id"),
                                response.optString("alias"),
                                response.optString("expiration"),
                                response.optString("currency"),
                                response.optBoolean("default"),
                                response.optString("status"),
                                response.optString("token"),
                                crateDate)

                        completionHandler(card, null)
                    } catch (e: JSONException) {
                        completionHandler(null, CardAPI.ParseReplyParamsException(e))
                    }
                }) { error -> completionHandler(null, createRequestError(error)) }

        request.setIRetryPolicy(defaultpolicy)
        queue.add(request)

    }


}