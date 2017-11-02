package com.dwaplatform.android.card.api.volley

import com.android.volley.AuthFailureError
import com.android.volley.VolleyError
import com.dwaplatform.android.card.api.IRequestProvider
import org.json.JSONArray
import org.json.JSONObject

/**
 * Volley implementation for request provider
 */
class VolleyRequestProvider : IRequestProvider {

    override fun jsonArrayRequest(method: Int,
                                  url: String,
                                  jsonRequest: JSONArray?,
                                  listener: (JSONArray) -> Unit,
                                  errorListener: (VolleyError) -> Unit): VolleyJsonArrayRequest {
        return VolleyJsonArrayRequest(method, url, jsonRequest, listener, errorListener)
    }

    override fun jsonArrayRequest(method: Int,
                                  url: String,
                                  jsonRequest: JSONArray?,
                                  header: Map<String, String>,
                                  listener: (JSONArray) -> Unit,
                                  errorListener: (VolleyError) -> Unit): VolleyJsonArrayRequest {
        return object : VolleyJsonArrayRequest(method, url, jsonRequest, listener, errorListener) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return header
            }
        }
    }

    override fun jsonObjectRequest(method: Int,
                                   url: String,
                                   jsonRequest: JSONObject?,
                                   listener: (JSONObject) -> Unit,
                                   errorListener: (VolleyError) -> Unit): VolleyJsonObjectRequest {
        return VolleyJsonObjectRequest(method, url, jsonRequest, listener, errorListener)
    }

    override fun jsonObjectRequest(method: Int,
                                   url: String,
                                   jsonRequest: JSONObject?,
                                   header: Map<String, String>,
                                   listener: (JSONObject) -> Unit,
                                   errorListener: (VolleyError) -> Unit): VolleyJsonObjectRequest {
        return object : VolleyJsonObjectRequest(method, url, jsonRequest, listener, errorListener) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return header
            }
        }
    }


    override fun stringRequest(method: Int, url: String, params: Map<String, String>,
                               header: Map<String, String>,
                               listener: (String) -> Unit,
                               errorListener: (VolleyError) -> Unit): VolleyStringRequest {
        return object : VolleyStringRequest(method, url, listener, errorListener) {
            override fun getParams(): Map<String, String> {
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return header
            }
        }
    }

    override fun jsonObjectRequestArrayReply(method: Int,
                                             url: String,
                                             jsonRequest: JSONObject?,
                                             header: Map<String, String>,
                                             listener: (JSONArray) -> Unit,
                                             errorListener: (VolleyError) -> Unit): VolleyJsonObjectRequestArrayReply {

        return object : VolleyJsonObjectRequestArrayReply(method, url, jsonRequest, listener, errorListener) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return header
            }
        }
    }
}
