package com.dwaplatform.android.card.log

/**
 * Log wrap to support mocking, use this instead of android Log directly
 */
open class Log {

    open fun debug(tag: String, msg: String) {
        android.util.Log.d(tag, msg)
    }

    open fun error(tag: String, msg: String, t: Throwable) {
        android.util.Log.e(tag, msg, t)
    }
}
