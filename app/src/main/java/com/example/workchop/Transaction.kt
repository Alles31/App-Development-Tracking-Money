package com.example.workchop

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Transaction = satu catatan transaksi keuangan.
 *
 * @param id        nomor unik transaksi
 * @param title     keterangan, mis. "Makan siang"
 * @param amount    nominal dalam Rupiah (angka bulat, mis. 25000)
 * @param type      tipe: "Pemasukan " atau "Pengeluaran"
 * @param category  kategori, mis. "Makan", "Transportasi", "Gaji"
 * @param date      tanggal transaksi (timestamp milidetik)
 */

@Entity(tableName = "transactions")
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val amount: Long,
    val type: String,
    val category: String,
    val date: Long
)