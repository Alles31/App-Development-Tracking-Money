package com.example.workchop

import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * formatTanggal = mengubah timestamp (Long milidetik) menjadi teks
 * tanggal & jam yang sudah dibaca.
 * Contoh: 1757142600000 -> "06 Sept. 2026, 14:30"
 */

fun formatTanggal(millis: Long): String{
    val pola = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
    return pola.format(Date(millis))
}

/**
 * batasTidakMasaDepan = aturan untuk MaterialDatePicker agar tanggal
 * di MASA DEPAN tidak bisa dipilih (hanya hari ini & sebelumnya).
 * Dipakai di form input (hari kedua) dan filter (hari keempat).
 */

fun batasTidakMasaDepan(): CalendarConstraints = CalendarConstraints.Builder()
    .setValidator(DateValidatorPointBackward.now())
    .build()