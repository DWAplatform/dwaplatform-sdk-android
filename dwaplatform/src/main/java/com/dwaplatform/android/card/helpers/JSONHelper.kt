package com.dwaplatform.android.card.helpers

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


open class JSONHelper {
    open fun buildJSONObject(): JSONObject = JSONObject()
    open fun buildJSONArray(): JSONArray = JSONArray()

    @Throws(JSONException::class)
    open fun buildJSONArray(str: String): JSONArray = JSONArray(str)
}
