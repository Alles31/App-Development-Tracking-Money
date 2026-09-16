package com.example.workchop

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatRupiah(amount: Long): String {
    val simbol = DecimalFormatSymbols(Locale.forLanguageTag("id-ID")).apply {
        groupingSeparator = '.'
    }
    val formatter = DecimalFormat ("#,###", simbol)
    return "Rp " + formatter.format(amount)
}