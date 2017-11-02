package com.dwaplatform.android.card.models

import java.util.*

/**
 * Card data model
 */
data class Card(val id: String?,
                val alias: String?,
                val expiration: String?,
                val currency: String,
                val default: Boolean?,
                val status: String?,
                val token: String?,
                val create: Date?)

