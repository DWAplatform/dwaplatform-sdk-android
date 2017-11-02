package com.dwaplatform.android.card.api

/**
 * Request Queue Interface
 */
interface IRequestQueue {
    fun <T> add(request: IRequest<T>): IRequest<T>
}
