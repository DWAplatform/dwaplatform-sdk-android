package com.dwaplatform.android.card.api.volley

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.dwaplatform.android.card.api.IRequest
import com.dwaplatform.android.card.api.IRequestQueue

/**
 * Volley request queue implementation
 */
class VolleyRequestQueueProvider(private val requestQueue: RequestQueue) : IRequestQueue {

    override fun <T> add(request: IRequest<T>): IRequest<T> {
        requestQueue.add(request as Request<*>)
        return request
    }
}
