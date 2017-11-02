package com.dwaplatform.android

import android.content.Context
import com.android.volley.toolbox.Volley
import com.dwaplatform.android.card.CardAPI
import com.dwaplatform.android.card.api.CardRestAPI
import com.dwaplatform.android.card.api.volley.VolleyRequestProvider
import com.dwaplatform.android.card.api.volley.VolleyRequestQueueProvider
import com.dwaplatform.android.card.helpers.CardHelper
import com.dwaplatform.android.card.helpers.JSONHelper
import com.dwaplatform.android.card.helpers.SanityCheck
import com.dwaplatform.android.card.log.Log


/**
 * DWAplatform Main Class.
 * Obtain all DWAplatform objects using this class.
 * Notice: before use any factory method you have to call initialize.
 *
 * Usage Example class extends Android Activity:
 *
class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val config = DWAplatform.Configuration("DWAPLATFORM_SANDBOX_HOSTNAME", true)
        DWAplatform.initialize(config)

        val cardAPI = DWAplatform.getCardAPI(this)
        val callApi = findViewById<Button>(R.id.callApi)

        //get token from POST call:
        // rest/v1/:clientId/users/:userId/accounts/:accountId/cards
        val token = "f422ac9c-bd76-11e7-a21f-bf8fecc6b286"
        val cardNumber = "1234567812345678"
        val expiration = "1122"
        val cxv = "123"

        callApi.setOnClickListener {
            cardAPI.registerCard(token, cardNumber, expiration, cxv) { card, e ->
                if (e != null) {
                    if (e is CardAPI.APIReplyError) {
                        val js = (e as CardAPI.APIReplyError?)?.json
                        if (js != null) {
                            Log.d("MainActivity", js.toString())
                        }
                        else
                            Log.d("MainActivity", e.throwable.message)
                    } else {
                        Log.d("MainActivity", e.message)
                    }
                } else {
                    Log.d("MainActivity", card.toString())
                }
            }
        }
    }
}
 *
 */
class DWAplatform {

    data class Configuration(val hostName: String, val sandbox: Boolean)

    companion object {
        @Volatile private var conf:  Configuration? = null
        @Volatile private var cardAPIInstance: CardAPI? = null

        /**
         * Initialize DWAplatform
         * @param config Configuration
         */
        fun initialize(config: Configuration) {
            conf = config
        }

        /**
         * Factory method to get CardAPI object
         */
        fun getCardAPI(context: Context): CardAPI =
                cardAPIInstance ?: synchronized(this) {

                    val c = conf ?: throw Exception("DWAplatform init configuration missing")

                    cardAPIInstance ?: buildCardAPI(c.hostName,
                            context,
                            c.sandbox).also {
                        cardAPIInstance = it }
                }

        private fun buildCardAPI(hostName: String, context: Context, sandbox: Boolean): CardAPI {

            return CardAPI(CardRestAPI(hostName,
                    VolleyRequestQueueProvider(Volley.newRequestQueue(context)),
                    VolleyRequestProvider(),
                    JSONHelper(),
                    sandbox), Log(), CardHelper(SanityCheck()))
        }
    }

}

