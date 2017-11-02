package com.dwaplatform.android.card.api.volley

import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.dwaplatform.android.card.api.IRequest

/**
 * Volley implementation of IRequest for json String response
 */
open class VolleyStringRequest(method: Int,
                               url: String,
                               listener: (String) -> Unit,
                               errorListener: (VolleyError) -> Unit)
    : StringRequest(method, url,
        Response.Listener(listener),
        Response.ErrorListener(errorListener)), IRequest<String> {

    override fun setIRetryPolicy(retryPolicy: RetryPolicy): IRequest<String> {
        setRetryPolicy(retryPolicy)
        return this
    }
}
