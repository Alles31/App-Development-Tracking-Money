package com.example.workchop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workchop.Transaction

/**
 * TransactionAdapter = penghubung antara DATA (list transaksi) dan
 * TAMPILAN (RecyclerView). Tugasnya: mengubah setiap objek Transaction
 * menjadi satu baris tampilan (item_transaction.xml).
 *
 * RecyclerView memanggil 3 fungsi penting:
 * - onCreateViewHolder : membuat 1 baris kosong dari XML
 * - onBindViewHolder   : mengisi baris dengan data transaksi
 * - getItemCount       : jumlah baris = jumlah transaksi
 **/
class TransactionAdapter(
    private var daftar: List<Transaction>,
    private var onClick: (Transaction) -> Unit,
    private var onDeleteClick: (Transaction) -> Unit,
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    /**
     * ViewHolder = "wadah" yang menyimpan referensi view di satu baris,
     * supaya tidak perlu findViewById berulang-ulang (lebih cepat).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textJudul: TextView = view.findViewById(R.id.textJudul)
        val textKategori: TextView = view.findViewById(R.id.textKategori)
        val textTanggal: TextView = view.findViewById(R.id.textTanggal)
        val textNominal: TextView = view.findViewById(R.id.textNominal)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    // Membuat baris baru dari layout item_transaction.xml.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    // Mengisi baris ke-"position" dengan data transaksi yang sesuai.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaksi = daftar[position]

        holder.textJudul.text = transaksi.title
        holder.textKategori.text = transaksi.category

        // Tampilkan tanggal & jam transaksi dalam format mudah dibaca.
        holder.textTanggal.text = formatTanggal(transaksi.date)

        // Tanda "+" untuk pemasukan, "-" untuk pengeluaran.
        val tanda = if (transaksi.type == "Pemasukan") "+" else "-"
        holder.textNominal.text = "$tanda Rp ${transaksi.amount}"

        // Warna bermakna: hijau pemasukan, merah pengeluaran.
        val warna = if (transaksi.type == "Pemasukan") {
            R.color.income_green
        } else {
            R.color.expense_red
        }

        holder.textNominal.setTextColor(
            holder.itemView.context.getColor(warna)
        )

        holder.itemView.setOnClickListener {
            onClick(transaksi)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(transaksi)
        }
    }

    // Jumlah item = jumlah transaksi dalam list.
    override fun getItemCount(): Int = daftar.size

    fun refresh(dataBaru: List<Transaction>) {
        daftar = dataBaru
        notifyDataSetChanged()
    }
}