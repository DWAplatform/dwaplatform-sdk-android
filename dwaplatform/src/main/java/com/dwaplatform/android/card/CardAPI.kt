package com.dwaplatform.android.card

import com.dwaplatform.android.card.api.CardRestAPI
import com.dwaplatform.android.card.helpers.CardHelper
import com.dwaplatform.android.card.log.Log
import com.dwaplatform.android.card.models.Card
import org.json.JSONArray
import java.lang.Exception

/**
 * Main class for API communication with DWAplatform to handle Cards.
 * Please use DWAplatform to get Card API instance; do not create directly
 *
 */
open class CardAPI constructor(
        internal val restAPI: CardRestAPI,
        internal val log: Log,
        internal val cardHelper: CardHelper) {

    /**
     * Represent the error send as reply from DWAplatform API.
     *
     * @property json error as json array, can be null in case of json parsing error
     * @property throwable error returned from the underlying HTTP library
     */
    data class APIReplyError(val json: JSONArray?, val throwable: Throwable) : Exception(throwable)

    /**
     * Represent the error during DWAplatform API response parse.
     *
     * @property throwable error returned from parser
     */
    data class ParseReplyParamsException(val throwable: Throwable) : Exception(throwable)

    private val TAG = "CardAPI"

    /**
     *  Register a Card. Use this method to register a user card and PLEASE DO NOT save card information on your own client or server side.
     *
     *  @property token token returned from DWAplatform to the create card request.
     *  @property cardNumber 16 digits user card number, without spaces or dashes
     *  @property expiration card expiration date in MMYY format
     *  @property cxv  3 digit cxv card number
     *  @property completionHandler callback called after the server communication is done and containing a Card object or an Exception in case of error.
     */
    open fun registerCard(token: String,
                     cardNumber: String,
                     expiration: String,
                     cxv: String,
                     completionHandler: (Card?, Exception?) -> Unit) {

        log.debug(TAG, "fun registerCard called")


        cardHelper.checkCardFormat(cardNumber, expiration, cxv)

        // TODO: enqueue the following requests in a modadic for comprehension style.
        restAPI.postCardRegister(token, cardHelper.generateAlias(cardNumber), expiration) { optCardRegistration, optError ->
            optError?.let { error -> completionHandler(null, error); return@postCardRegister }
            optCardRegistration?.let { cardRegistration ->

                restAPI.getCardSafe(CardRestAPI.CardToRegister(cardNumber, expiration, cxv)) { optCardSafe, optErrorCS ->
                    optErrorCS?.let { error -> completionHandler(null, error); return@getCardSafe }
                    optCardSafe?.let { cardSafe ->

                        restAPI.postCardRegistrationData(cardSafe, cardRegistration) { optRegistration, optErrorPCRD ->
                            optErrorPCRD?.let { error -> completionHandler(null, error); return@postCardRegistrationData }
                            optRegistration?.let { registration ->
                                restAPI.putRegisterCard(token, cardRegistration.cardRegistrationId, registration) { optCard, optErrorPRC ->
                                    optErrorPRC?.let { error -> completionHandler(null, error); return@putRegisterCard }
                                    completionHandler(optCard, null)
                                } // end putRegisterCard
                            }
                        } // end postCardRegistrationData
                    }
                } // end getCardSafe
            }
        } // end postCardRegister
    }

}