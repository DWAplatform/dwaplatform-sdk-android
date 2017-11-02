package com.dwaplatform.android.card.helpers

/**
 * Util class to check parameters compliance against regular expression.
 */
open class SanityCheck {

    /**
     * Filter items that match the regular expression.
     *
     * @return all items that fail the check
     */
    open fun check(items: List<SanityItem>): List<SanityItem> {
        return items.filter {
            Regex(it.regExp).matchEntire(it.value) == null
        }
    }

    /**
     * Check item compliance and throw exception in case of check fail
     */
    @Throws(SanityCheckException::class)
    open fun checkThrowException(item: SanityItem): Unit {
        if (check(listOf(item)).isNotEmpty()) throw SanityCheckException(item)
    }

}

/**
 * Exception throw in case of check fail.
 */
open class SanityCheckException(item: SanityItem) :
        Exception("Unexpected value $item.value on field $item.field, no match with regExp: $item.regExp") {

}

/**
 * Sanity Item containing the parameter name to check, the value of the parameter and the
 * regular expression to check for.
 */
data class SanityItem(val field: String, val value: String, val regExp: String)

