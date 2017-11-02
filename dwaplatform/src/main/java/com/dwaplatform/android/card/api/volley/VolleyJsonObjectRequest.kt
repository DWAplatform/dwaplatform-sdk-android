package com.dwaplatform.android.card.api.volley

import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.dwaplatform.android.card.api.IRequest
import org.json.JSONObject

/**
 * Volley implementation of IRequest for json JSONObject response
 */
open class VolleyJsonObjectRequest(method: Int,
                                   url: String,
                                   jsonRequest: JSONObject?,
                                   listener: (JSONObject) -> Unit,
                                   errorListener: (VolleyError) -> Unit)
    : JsonObjectRequest(method, url, jsonRequest,
        Response.Listener(listener),
        Response.ErrorListener(errorListener)), IRequest<JSONObject> {

    override fun setIRetryPolicy(retryPolicy: RetryPolicy): IRequest<JSONObject> {
        setRetryPolicy(retryPolicy)
        return this
    }
}
