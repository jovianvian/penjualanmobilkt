package com.example.mobileku

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.util.Calendar
import java.util.Locale

class LaporanKeuanganActivity : AppCompatActivity() {
    private val baseUrl = "http://192.168.0.22/penjualanmobil"
    private val detailRows = ArrayList<String>()
    private var totalCash = 0.0
    private var totalKredit = 0.0
    private var totalCicilan = 0.0
    private var pendingRequest = 0
    private lateinit var tvTotalCash: TextView
    private lateinit var tvTotalKredit: TextView
    private lateinit var tvTotalCicilan: TextView
    private lateinit var tvTotalSemua: TextView
    private lateinit var inputPeriode: EditText
    private lateinit var spinnerPeriode: Spinner
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan_keuangan)

        spinnerPeriode = findViewById(R.id.spinnerPeriode)
        inputPeriode = findViewById(R.id.inputPeriode)
        listView = findViewById(R.id.listLaporan)
        tvTotalCash = findViewById(R.id.tvTotalCash)
        tvTotalKredit = findViewById(R.id.tvTotalKredit)
        tvTotalCicilan = findViewById(R.id.tvTotalCicilan)
        tvTotalSemua = findViewById(R.id.tvTotalSemua)
        val tombolMuat = findViewById<Button>(R.id.tombolMuatLaporan)

        val opsiPeriode = listOf("Bulanan", "Tahunan")
        val adapterPeriode = ArrayAdapter(this, android.R.layout.simple_spinner_item, opsiPeriode)
        adapterPeriode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriode.adapter = adapterPeriode
        spinnerPeriode.setSelection(0)
        spinnerPeriode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setDefaultPeriode()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        inputPeriode.inputType = InputType.TYPE_NULL
        inputPeriode.isFocusable = false
        inputPeriode.setOnClickListener { pilihPeriode() }

        tombolMuat.setOnClickListener { muatLaporan() }

        setDefaultPeriode()
        muatLaporan()
    }

    private fun setDefaultPeriode() {
        val cal = Calendar.getInstance()
        val mode = spinnerPeriode.selectedItem?.toString() ?: "Bulanan"
        if (mode == "Tahunan") {
            inputPeriode.setText(cal.get(Calendar.YEAR).toString())
        } else {
            inputPeriode.setText(
                String.format(
                    Locale.US,
                    "%04d-%02d",
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1
                )
            )
        }
    }

    private fun pilihPeriode() {
        val cal = Calendar.getInstance()
        val mode = spinnerPeriode.selectedItem.toString()
        DatePickerDialog(
            this,
            { _, year, month, _ ->
                if (mode == "Tahunan") {
                    inputPeriode.setText(year.toString())
                } else {
                    inputPeriode.setText(String.format(Locale.US, "%04d-%02d", year, month + 1))
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun muatLaporan() {
        val mode = spinnerPeriode.selectedItem.toString()
        val periodeInput = inputPeriode.text.toString().trim()
        val periode = normalizePeriode(mode, periodeInput)
        if (periode.isEmpty()) {
            Toast.makeText(this, "Periode wajib dipilih", Toast.LENGTH_SHORT).show()
            return
        }

        detailRows.clear()
        totalCash = 0.0
        totalKredit = 0.0
        totalCicilan = 0.0
        pendingRequest = 3
        render()

        ambilData(
            url = "$baseUrl/TampilCash.php",
            tipe = "CASH",
            fieldTanggal = "cash_tgl",
            fieldNominal = "cash_bayar",
            mode = mode,
            periode = periode
        )
        ambilData(
            url = "$baseUrl/TampilKredit.php",
            tipe = "KREDIT",
            fieldTanggal = "tanggal_kredit",
            fieldNominal = "bayar_kredit",
            mode = mode,
            periode = periode
        )
        ambilData(
            url = "$baseUrl/TampilCicilanV2.php",
            tipe = "CICILAN",
            fieldTanggal = "tanggal_cicilan",
            fieldNominal = "jumlah_cicilan",
            mode = mode,
            periode = periode
        )
    }

    private fun ambilData(
        url: String,
        tipe: String,
        fieldTanggal: String,
        fieldNominal: String,
        mode: String,
        periode: String
    ) {
        val request = JsonArrayRequest(
            url,
            { response ->
                olahResponse(response, tipe, fieldTanggal, fieldNominal, mode, periode)
                pendingRequest--
                render()
            },
            {
                pendingRequest--
                Toast.makeText(this, "Gagal ambil data $tipe", Toast.LENGTH_SHORT).show()
                render()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun olahResponse(
        response: JSONArray,
        tipe: String,
        fieldTanggal: String,
        fieldNominal: String,
        mode: String,
        periode: String
    ) {
        for (i in 0 until response.length()) {
            val obj = response.getJSONObject(i)
            val tanggal = obj.optString(fieldTanggal, "")
            if (!cocokPeriode(tanggal, periode, mode)) continue

            val nominal = obj.optString(fieldNominal, "0").toDoubleOrNull() ?: 0.0
            when (tipe) {
                "CASH" -> totalCash += nominal
                "KREDIT" -> totalKredit += nominal
                "CICILAN" -> totalCicilan += nominal
            }

            val kode = when (tipe) {
                "CASH" -> obj.optString("kode_cash", "-")
                "KREDIT" -> obj.optString("kode_kredit", "-")
                else -> obj.optString("kode_cicilan", "-")
            }

            detailRows.add(
                "$tipe\nKode: $kode\nTanggal: $tanggal\nNominal: ${formatRupiah(nominal)}"
            )
        }
    }

    private fun cocokPeriode(tanggal: String, periode: String, mode: String): Boolean {
        val normalizedDate = normalizeTanggal(tanggal)
        return if (mode == "Tahunan") {
            normalizedDate.take(4) == periode
        } else {
            normalizedDate.take(7) == periode
        }
    }

    private fun normalizePeriode(mode: String, rawPeriode: String): String {
        val cleaned = normalizeTanggal(rawPeriode)
        return if (mode == "Tahunan") {
            cleaned.take(4)
        } else {
            if (cleaned.length >= 7) cleaned.take(7) else cleaned
        }
    }

    private fun normalizeTanggal(rawTanggal: String): String {
        val cleaned = rawTanggal.replace('/', '-').trim()
        val parts = cleaned.split("-")
        if (parts.size >= 2) {
            val year = parts[0].filter { it.isDigit() }
            val month = parts[1].filter { it.isDigit() }.padStart(2, '0').take(2)
            val day = if (parts.size >= 3) parts[2].filter { it.isDigit() }.padStart(2, '0').take(2) else "01"
            if (year.length == 4 && month.isNotEmpty()) {
                return "$year-$month-$day"
            }
        }
        return cleaned
    }

    private fun render() {
        tvTotalCash.text = "Total Cash: ${formatRupiah(totalCash)}"
        tvTotalKredit.text = "Total Bayar Kredit: ${formatRupiah(totalKredit)}"
        tvTotalCicilan.text = "Total Bayar Cicilan: ${formatRupiah(totalCicilan)}"
        tvTotalSemua.text = "Total Pemasukan: ${formatRupiah(totalCash + totalKredit + totalCicilan)}"
        listView.adapter = ArrayAdapter(this, R.layout.row_data_item, R.id.tvDataItem, detailRows)
    }

    private fun formatRupiah(value: Double): String {
        return "Rp ${String.format(Locale.US, "%,.0f", value)}"
    }
}
