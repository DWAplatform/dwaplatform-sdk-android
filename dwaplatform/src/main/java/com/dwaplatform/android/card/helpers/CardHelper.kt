package com.dwaplatform.android.card.helpers

/**
 * Card helper class.
 * Check card data validity and create card number alias.
 */
open class CardHelper constructor(internal val sanityCheck: SanityCheck) {

    open fun generateAlias(cardNumber: String): String {
        return "${cardNumber.substring(0, 6)}XXXXXX${cardNumber.substring(cardNumber.length - 4, cardNumber.length)}"
    }

    @Throws(SanityCheckException::class)
    open fun checkCardNumberFormat(cardNumber: String) {
        sanityCheck.checkThrowException(SanityItem("cardNumber", cardNumber, """\d{16}"""))
    }

    @Throws(SanityCheckException::class)
    open fun checkCardExpirationFormat(expiration: String) {
        sanityCheck.checkThrowException(SanityItem("expiration", expiration, """\d{4}"""))
    }

    @Throws(SanityCheckException::class)
    open fun checkCardCXVFormat(cxv: String) {
        sanityCheck.checkThrowException(SanityItem("cxv", cxv, """\d{3}"""))
    }

    @Throws(SanityCheckException::class)
    open fun checkCardFormat(cardNumber: String, expiration: String, cxv: String) {
        checkCardNumberFormat(cardNumber)
        checkCardExpirationFormat(expiration)
        checkCardCXVFormat(cxv)
    }


}