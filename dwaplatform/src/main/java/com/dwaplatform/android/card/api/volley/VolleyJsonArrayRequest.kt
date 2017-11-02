package com.dwaplatform.android.card.api.volley

import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.dwaplatform.android.card.api.IRequest
import org.json.JSONArray

/**
 * Volley implementation of IRequest for json array request and reply
 */
open class VolleyJsonArrayRequest(method: Int,
                                  url: String,
                                  jsonRequest: JSONArray?,
                                  listener: (JSONArray) -> Unit,
                                  errorListener: (VolleyError) -> Unit)
    : JsonArrayRequest(method, url, jsonRequest, Response.Listener(listener),
        Response.ErrorListener(errorListener)), IRequest<JSONArray> {

    override fun setIRetryPolicy(retryPolicy: RetryPolicy): IRequest<JSONArray> {
        setRetryPolicy(retryPolicy)
        return this
    }
}
