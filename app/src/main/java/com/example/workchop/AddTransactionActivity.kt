package com.example.workchop

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workchop.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class AddTransactionActivity : AppCompatActivity() {
    private var tanggalDipilih: Long = System.currentTimeMillis()
    private var idEdit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        title = "Tambah Transaksi"

        val editKeterangan = findViewById<EditText>(R.id.editKeterangan)
        val editNominal = findViewById<EditText>(R.id.editNominal)
        val groupTipe = findViewById<RadioGroup>(R.id.groupTipe)
        val spinnerKategori = findViewById<Spinner>(R.id.spinnerKategori)
        val buttonPilihTanggal = findViewById<Button>(R.id.buttonPilihTanggal)
        val buttonSimpan = findViewById<Button>(R.id.buttonSimpan)
        val textTanggalDipilih = findViewById<TextView>(R.id.textTanggalDipilih)

        textTanggalDipilih.text = formatTanggal(tanggalDipilih)

        buttonPilihTanggal.setOnClickListener{
            pilihTanggalDanJam {
                millis -> tanggalDipilih = millis
                textTanggalDipilih.text = formatTanggal(millis)
            }
        }

        buttonSimpan.setOnClickListener{
            val keterangan = editKeterangan.text.toString().trim()
            val nominalText = editNominal.text.toString().trim()

            if(keterangan.isEmpty() || nominalText.isEmpty()){
                Toast.makeText(this, "Keterangan & nominal wajib diisi", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }

            val nominal = nominalText.toLongOrNull()
            if(nominal == null || nominal <= 0){
              Toast.makeText(this, "Nominal tidak valid", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }

            val tipe = if(groupTipe.checkedRadioButtonId == R.id.radioPemasukan){
                "Pemasukan"
            } else {
                "Pengeluaran"
            }
            val kategori = spinnerKategori.selectedItem.toString()

            val hasil = Intent().apply {
                putExtra(EXTRA_ID, idEdit)
                putExtra(EXTRA_JUDUL, keterangan)
                putExtra(EXTRA_NOMINAL, nominal)
                putExtra(EXTRA_TIPE, tipe)
                putExtra(EXTRA_KATEGORI, kategori)
                putExtra(EXTRA_TANGGAL, tanggalDipilih)
            }
            setResult(Activity.RESULT_OK, hasil )
            finish()
        }
        idEdit = intent.getIntExtra(EXTRA_ID, 0)
        if (idEdit == 0) {
            title = getString(R.string.judul_form_tambah)
        } else {
            title = "Edit Transaksi"
            editKeterangan.setText(intent.getStringExtra(EXTRA_ID))
            editNominal.setText(intent.getLongExtra(EXTRA_NOMINAL, 0).toString())
            val tipe = intent.getStringExtra(EXTRA_TIPE)
            if (tipe == "Pemasukan") {
                groupTipe.check(R.id.radioPemasukan)
            } else {
                groupTipe.check(R.id.radioPengeluaran)
            }
        }
    }

    private fun pilihTanggalDanJam(onSelesai: (Long) -> Unit){
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(tanggalDipilih)
            .setCalendarConstraints(batasTidakMasaDepan())
            .build()

        datePicker.addOnPositiveButtonClickListener { pilihanTanggalUtc ->
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = pilihanTanggalUtc

            val awal = Calendar.getInstance().apply {timeInMillis = tanggalDipilih}
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(awal.get(Calendar.HOUR_OF_DAY))
                .setMinute(awal.get(Calendar.MINUTE))
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val cal = Calendar.getInstance()
                cal.set(
                    utc.get(Calendar.YEAR),
                    utc.get(Calendar.MONTH),
                    utc.get(Calendar.DAY_OF_MONTH),
                    timePicker.hour,
                    timePicker.minute,
                    0
                )
                cal.set(Calendar.MILLISECOND, 0)

                val sekarang = System.currentTimeMillis()
                if(cal.timeInMillis > sekarang) {
                    Toast.makeText(this, "Waktu tidak boleh masa depan", Toast.LENGTH_SHORT).show()
                    onSelesai(sekarang)
                } else {
                    onSelesai(cal.timeInMillis)
                }
            }
            timePicker.show(supportFragmentManager, "pemilih_jam")
        }
        datePicker.show(supportFragmentManager, "pemilih_tanggal")
    }

    companion object{
        const val EXTRA_ID = "extra_id"
        const val EXTRA_JUDUL = "extra_judul"
        const val EXTRA_NOMINAL = "extra_nominal"
        const val EXTRA_TIPE = "extra_tipe"
        const val EXTRA_KATEGORI = "extra_kategori"
        const val EXTRA_TANGGAL = "extra_tanggal"
    }
}