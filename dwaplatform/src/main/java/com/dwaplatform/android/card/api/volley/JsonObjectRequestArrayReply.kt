package com.dwaplatform.android.card.api.volley

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Volley implementation of IRequest for json JsonObject request and jsonarray reply
 */
open class JsonObjectRequestArrayReply : JsonRequest<JSONArray> {

    /**
     * Creates a new request.
     * @param url URL to fetch the JSON from
     * *
     * @param listener Listener to receive the JSON response
     * *
     * @param errorListener Error listener, or null to ignore errors.
     */
    constructor(url: String,
                listener: (JSONArray) -> Unit,
                errorListener: (VolleyError) -> Unit)
            : super(Request.Method.GET, url, null,
            Response.Listener(listener), Response.ErrorListener(errorListener))

    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * *
     * @param url URL to fetch the JSON from
     * *
     * @param jsonRequest A [JSONArray] to post with the request. Null is allowed and
     * *   indicates no parameters will be posted along with request.
     * *
     * @param listener Listener to receive the JSON response
     * *
     * @param errorListener Error listener, or null to ignore errors.
     */
    constructor(method: Int, url: String, jsonRequest: JSONObject?,
                listener: (JSONArray) -> Unit,
                errorListener: (VolleyError) -> Unit)
            : super(method, url, jsonRequest?.toString(),
            Response.Listener(listener), Response.ErrorListener(errorListener))

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONArray> {
        try {
            val jsonString = String(response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers, JsonRequest.PROTOCOL_CHARSET)))
            return Response.success(JSONArray(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            return Response.error<JSONArray>(ParseError(e))
        } catch (je: JSONException) {
            return Response.error<JSONArray>(ParseError(je))
        }

    }
}
