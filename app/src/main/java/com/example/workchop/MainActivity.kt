package com.example.workchop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workchop.AddTransactionActivity.Companion.EXTRA_ID
import com.example.workchop.AddTransactionActivity.Companion.EXTRA_JUDUL
import com.example.workchop.AddTransactionActivity.Companion.EXTRA_NOMINAL
import com.example.workchop.AddTransactionActivity.Companion.EXTRA_TIPE
import com.example.workchop.AddTransactionActivity.Companion.EXTRA_KATEGORI
import com.example.workchop.AddTransactionActivity.Companion.EXTRA_TANGGAL
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(){

    private lateinit var transactionDao: TransactionDao
    private lateinit var textSaldo: TextView
    private lateinit var textPemasukan: TextView
    private lateinit var textPengeluaran: TextView
    private var sumberSaatIni: LiveData<List<Transaction>>? = null
    // Daftar transaksi sementara (in-memory).
    private val daftarTransaksi = mutableListOf<Transaction>()

    // Adapter yang menjembatani list ke RecyclerView.
    private lateinit var adapter: TransactionAdapter

    // Penghitung id sederhana untuk memberi id unik tiap transaksi.
    private var idBerikutnya = 1

    // Launcher untuk membuka AddTransactionActivity & menerima hasilnya.
// Ini pengganti modern dari startActivityForResult yang lama.
    private val addLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Hanya proses jika form ditekan Simpan (RESULT_OK).
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult

            // Ambil data yang dikirim balik dari form.
            val idEdit = data.getIntExtra(AddTransactionActivity.EXTRA_ID, 0)
            val judul = data.getStringExtra(AddTransactionActivity.EXTRA_JUDUL) ?: ""
            val nominal = data.getLongExtra(AddTransactionActivity.EXTRA_NOMINAL, 0)
            val tipe = data.getStringExtra(AddTransactionActivity.EXTRA_TIPE) ?: "Pengeluaran"
            val kategori = data.getStringExtra(AddTransactionActivity.EXTRA_KATEGORI) ?: "Lainnya"
            // Tanggal/jam yang dipilih user di form (default: sekarang).
            val tanggal = data.getLongExtra(
                AddTransactionActivity.EXTRA_TANGGAL, System.currentTimeMillis()
            )

            // Buat objek transaksi baru, masukkan ke awal list.
            val transaksi = Transaction(
                id = 0,
                title = judul,
                amount = nominal,
                type = tipe,
                category = kategori,
                date = tanggal
            )

//            daftarTransaksi.add(0, transaksi)
            lifecycleScope.launch {
                transactionDao.insert(transaksi)
            }

            // Beri tahu adapter agar RecyclerView memperbarui tampilan.
            adapter.notifyItemInserted(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactionDao = AppDatabase
            .getInstance(this@MainActivity).transactionDao()
        // Siapkan RecyclerView: susun item vertikal + pasang adapter.
        val recycler = findViewById<RecyclerView>(R.id.recyclerViewTransaksi)
        adapter = TransactionAdapter(daftarTransaksi, onClick = { formEdit(it)} )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        textSaldo = findViewById(R.id.textSaldo)
        textPemasukan = findViewById(R.id.textPemasukan)
        textPengeluaran = findViewById(R.id.textPengeluaran)

        // Tombol Tambah -> buka form AddTransactionActivity.
        findViewById<Button>(R.id.btnTambahTransaksi).setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            addLauncher.launch(intent)
        }
        loadData(transactionDao.getAll())
    }

    private fun pindahScreen() {
        findViewById<Button>(R.id.btnTambahTransaksi).setOnClickListener {
            val intent = Intent(this@MainActivity, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadData(sumber: LiveData<List<Transaction>>) {
        sumberSaatIni?.removeObserver { this }
        sumberSaatIni = sumber
        sumber.observe ( this ) {daftar ->
            adapter.refresh(daftar)
            hitungSaldo(daftar)
        }
    }

    private fun hitungSaldo(daftar: List<Transaction>) {
        val pemasukan = daftar.filter { it.type == "Pemasukan" }.sumOf { it.amount }
        val pengeluaran = daftar.filter { it.type == "Pengeluaran" }.sumOf { it.amount }
        textPemasukan.text = formatRupiah(pemasukan)
        textPengeluaran.text = formatRupiah(pengeluaran)
        textSaldo.text = formatRupiah(pemasukan - pengeluaran)
    }

    private fun formEdit(transaksi: Transaction) {
        val intent = Intent(this, AddTransactionActivity::class.java).apply {
            putExtra(EXTRA_ID, transaksi.id)
            putExtra(EXTRA_JUDUL, transaksi.title)
            putExtra(EXTRA_NOMINAL, transaksi.amount)
            putExtra(EXTRA_TIPE, transaksi.type)
            putExtra(EXTRA_KATEGORI, transaksi.category)
            putExtra(EXTRA_TANGGAL, transaksi.date)
        }
        addLauncher.launch(intent)
    }
}

