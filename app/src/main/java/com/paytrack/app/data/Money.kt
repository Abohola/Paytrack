package com.paytrack.app.data

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object Money {
    fun parseMinor(input: String): Long? {
        val normalized = normalize(input) ?: return null
        return runCatching {
            normalized.toBigDecimal()
                .movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact()
                .takeIf { it > 0 }
        }.getOrNull()
    }

    fun format(amountMinor: Long, locale: Locale = Locale.getDefault()): String =
        NumberFormat.getCurrencyInstance(locale).format(BigDecimal(amountMinor).movePointLeft(2))

    fun currencyCode(locale: Locale = Locale.getDefault()): String =
        runCatching { Currency.getInstance(locale).currencyCode }.getOrDefault("USD")

    private fun normalize(raw: String): String? {
        val compact = raw.trim().replace(" ", "").replace("'", "")
        if (compact.isBlank()) return null
        val lastDot = compact.lastIndexOf('.')
        val lastComma = compact.lastIndexOf(',')
        val decimalIndex = maxOf(lastDot, lastComma)
        return if (decimalIndex >= 0) {
            val integer = compact.substring(0, decimalIndex).replace(".", "").replace(",", "")
            val fraction = compact.substring(decimalIndex + 1).replace(".", "").replace(",", "")
            "$integer.$fraction"
        } else compact
    }
}
