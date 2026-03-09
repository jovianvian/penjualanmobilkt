package com.example.mobileku

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

class DataCashActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private val listData = ArrayList<String>()
    private val listCash = ArrayList<Cash>()
    private val listKtp = ArrayList<String>()
    private val listKodeMobil = ArrayList<String>()
    private val baseUrl = "http://192.168.0.22/penjualanmobil"

    data class Cash(
        val kode: String,
        val ktp: String,
        val kodeMobil: String,
        val tanggal: String,
        val bayar: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_cash)
        listView = findViewById(R.id.listbayarcash)
        val tombolTambah = findViewById<Button>(R.id.tomboltambah)

        tombolTambah.setOnClickListener { showTambahDialog() }
        listView.setOnItemClickListener { _, _, position, _ -> showAksiDialog(position) }

        loadMasterData()
        loadData()
    }

    private fun loadMasterData() {
        val reqPembeli = JsonArrayRequest(
            "$baseUrl/Tampilpembeli.php",
            { response ->
                listKtp.clear()
                for (i in 0 until response.length()) {
                    val ktp = response.getJSONObject(i).optString("ktp", "")
                    if (ktp.isNotEmpty()) listKtp.add(ktp)
                }
            },
            {}
        )
        val reqMobil = JsonArrayRequest(
            "$baseUrl/TampilMobil.php",
            { response ->
                listKodeMobil.clear()
                for (i in 0 until response.length()) {
                    val kode = response.getJSONObject(i).optString("kode_mobil", "")
                    if (kode.isNotEmpty()) listKodeMobil.add(kode)
                }
            },
            {}
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(reqPembeli)
        queue.add(reqMobil)
    }

    private fun loadData() {
        val request = JsonArrayRequest(
            "$baseUrl/TampilCash.php",
            { response ->
                listData.clear()
                listCash.clear()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val item = Cash(
                        kode = obj.optString("kode_cash", "-"),
                        ktp = obj.optString("ktp", "-"),
                        kodeMobil = obj.optString("kode_mobil", "-"),
                        tanggal = obj.optString("cash_tgl", "-"),
                        bayar = obj.optString("cash_bayar", "0")
                    )
                    listCash.add(item)
                    listData.add(
                        "Kode Cash: ${item.kode}\nKTP: ${item.ktp}\nKode Mobil: ${item.kodeMobil}\nTanggal: ${item.tanggal}\nBayar: ${item.bayar}"
                    )
                }
                listView.adapter = ArrayAdapter(this, R.layout.row_data_item, R.id.tvDataItem, listData)
            },
            { Toast.makeText(this, "Gagal ambil data cash", Toast.LENGTH_SHORT).show() }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun showAksiDialog(position: Int) {
        val item = listCash[position]
        AlertDialog.Builder(this)
            .setTitle("Aksi Data Cash")
            .setItems(arrayOf("Edit", "Hapus")) { _, which ->
                if (which == 0) showEditDialog(item) else konfirmasiHapus(item)
            }
            .show()
    }

    private fun showTambahDialog() {
        if (listKtp.isEmpty() || listKodeMobil.isEmpty()) {
            Toast.makeText(this, "Data KTP/Kode Mobil belum siap, coba lagi", Toast.LENGTH_SHORT).show()
            loadMasterData()
            return
        }

        val container = dialogContainer()
        val inputKode = EditText(this).apply { hint = "Kode Cash" }
        val spinnerKtp = createSpinner(listKtp)
        val spinnerKodeMobil = createSpinner(listKodeMobil)
        val inputTanggal = createDateInput("")
        val inputBayar = EditText(this).apply { hint = "Bayar Cash" }

        container.addView(inputKode)
        addLabel(container, "KTP")
        container.addView(spinnerKtp)
        addLabel(container, "Kode Mobil")
        container.addView(spinnerKodeMobil)
        container.addView(inputTanggal)
        container.addView(inputBayar)

        AlertDialog.Builder(this)
            .setTitle("Tambah Data Cash")
            .setView(container)
            .setPositiveButton("Simpan") { _, _ ->
                val kode = inputKode.text.toString().trim()
                val ktp = spinnerKtp.selectedItem?.toString().orEmpty()
                val kodeMobil = spinnerKodeMobil.selectedItem?.toString().orEmpty()
                val tanggal = inputTanggal.text.toString().trim()
                val bayar = inputBayar.text.toString().trim()
                if (kode.isEmpty() || ktp.isEmpty() || kodeMobil.isEmpty() || tanggal.isEmpty() || bayar.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else {
                    tambahCash(kode, ktp, kodeMobil, tanggal, bayar)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showEditDialog(item: Cash) {
        if (listKtp.isEmpty() || listKodeMobil.isEmpty()) {
            Toast.makeText(this, "Data KTP/Kode Mobil belum siap, coba lagi", Toast.LENGTH_SHORT).show()
            loadMasterData()
            return
        }

        val container = dialogContainer()
        val spinnerKtp = createSpinner(listKtp, item.ktp)
        val spinnerKodeMobil = createSpinner(listKodeMobil, item.kodeMobil)
        val inputTanggal = createDateInput(item.tanggal)
        val inputBayar = EditText(this).apply {
            hint = "Bayar Cash"
            setText(item.bayar)
        }

        addLabel(container, "KTP")
        container.addView(spinnerKtp)
        addLabel(container, "Kode Mobil")
        container.addView(spinnerKodeMobil)
        container.addView(inputTanggal)
        container.addView(inputBayar)

        AlertDialog.Builder(this)
            .setTitle("Edit Cash (${item.kode})")
            .setView(container)
            .setPositiveButton("Simpan") { _, _ ->
                val ktp = spinnerKtp.selectedItem?.toString().orEmpty()
                val kodeMobil = spinnerKodeMobil.selectedItem?.toString().orEmpty()
                val tanggal = inputTanggal.text.toString().trim()
                val bayar = inputBayar.text.toString().trim()
                if (ktp.isEmpty() || kodeMobil.isEmpty() || tanggal.isEmpty() || bayar.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else {
                    updateCash(item.kode, ktp, kodeMobil, tanggal, bayar)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun konfirmasiHapus(item: Cash) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Cash")
            .setMessage("Hapus data kode ${item.kode}?")
            .setPositiveButton("Hapus") { _, _ -> hapusCash(item.kode) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tambahCash(kode: String, ktp: String, kodeMobil: String, tanggal: String, bayar: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/TambahCashV2.php",
            { response -> handleMutasiResponse(response, "Data cash berhasil ditambahkan") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "kode_cash" to kode,
                    "ktp" to ktp,
                    "kode_mobil" to kodeMobil,
                    "cash_tgl" to tanggal,
                    "cash_bayar" to bayar
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun updateCash(kode: String, ktp: String, kodeMobil: String, tanggal: String, bayar: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/EditCash.php",
            { response -> handleMutasiResponse(response, "Data cash berhasil diupdate") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "kode_cash" to kode,
                    "ktp" to ktp,
                    "kode_mobil" to kodeMobil,
                    "cash_tgl" to tanggal,
                    "cash_bayar" to bayar
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun hapusCash(kode: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/HapusCash.php",
            { response -> handleMutasiResponse(response, "Data cash berhasil dihapus") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("kode_cash" to kode)
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun dialogContainer(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
    }

    private fun addLabel(container: LinearLayout, text: String) {
        container.addView(TextView(this).apply { this.text = text })
    }

    private fun createSpinner(options: List<String>, selectedValue: String? = null): Spinner {
        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        if (!selectedValue.isNullOrEmpty()) {
            val idx = options.indexOf(selectedValue)
            if (idx >= 0) spinner.setSelection(idx)
        }
        return spinner
    }

    private fun createDateInput(initialValue: String): EditText {
        val input = EditText(this).apply {
            hint = "Tanggal (YYYY-MM-DD)"
            isFocusable = false
            isClickable = true
            inputType = InputType.TYPE_NULL
            if (initialValue.isNotEmpty()) setText(initialValue)
        }
        input.setOnClickListener { showDatePicker(input) }
        return input
    }

    private fun showDatePicker(target: EditText) {
        val cal = Calendar.getInstance()
        val parts = target.text.toString().split("-")
        if (parts.size == 3) {
            parts[0].toIntOrNull()?.let { cal.set(Calendar.YEAR, it) }
            parts[1].toIntOrNull()?.let { cal.set(Calendar.MONTH, it - 1) }
            parts[2].toIntOrNull()?.let { cal.set(Calendar.DAY_OF_MONTH, it) }
        }
        DatePickerDialog(
            this,
            { _, year, month, day ->
                target.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun handleMutasiResponse(response: String, defaultSuccessMessage: String) {
        try {
            val json = JSONObject(response)
            val success = json.optBoolean("success", json.optInt("status", 0) == 1)
            val message = json.optString(
                "message",
                if (success) defaultSuccessMessage else "Operasi gagal"
            )
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (success) {
                loadData()
                loadMasterData()
            }
        } catch (_: Exception) {
            Toast.makeText(this, "Response server tidak valid", Toast.LENGTH_SHORT).show()
        }
    }
}
