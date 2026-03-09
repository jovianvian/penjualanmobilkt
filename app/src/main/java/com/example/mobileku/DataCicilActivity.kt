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

class DataCicilActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private val listData = ArrayList<String>()
    private val listCicil = ArrayList<Cicilan>()
    private val listKodeKredit = ArrayList<String>()
    private val baseUrl = "http://192.168.0.22/penjualanmobil"

    data class Cicilan(
        val kode: String,
        val kodeKredit: String,
        val tanggal: String,
        val cicilanKe: String,
        val jumlah: String,
        val sisaKe: String,
        val sisa: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_cicilan)
        listView = findViewById(R.id.listbayarcicilan)
        val tombolTambah = findViewById<Button>(R.id.tomboltambah)

        tombolTambah.setOnClickListener { showTambahDialog() }
        listView.setOnItemClickListener { _, _, position, _ -> showAksiDialog(position) }

        loadMasterData()
        loadData()
    }

    private fun loadMasterData() {
        val request = JsonArrayRequest(
            "$baseUrl/TampilKredit.php",
            { response ->
                listKodeKredit.clear()
                for (i in 0 until response.length()) {
                    val kode = response.getJSONObject(i).optString("kode_kredit", "")
                    if (kode.isNotEmpty()) listKodeKredit.add(kode)
                }
            },
            {}
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun loadData() {
        val request = JsonArrayRequest(
            "$baseUrl/TampilCicilanV2.php",
            { response ->
                listData.clear()
                listCicil.clear()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val item = Cicilan(
                        kode = obj.optString("kode_cicilan", "-"),
                        kodeKredit = obj.optString("kode_kredit", "-"),
                        tanggal = obj.optString("tanggal_cicilan", "-"),
                        cicilanKe = obj.optString("cicilanke", "0"),
                        jumlah = obj.optString("jumlah_cicilan", "0"),
                        sisaKe = obj.optString("sisacicilke", "0"),
                        sisa = obj.optString("sisa_cicilan", "0")
                    )
                    listCicil.add(item)
                    listData.add(
                        "Kode Cicilan: ${item.kode}\nKode Kredit: ${item.kodeKredit}\nTanggal: ${item.tanggal}\nCicilan Ke: ${item.cicilanKe}\nJumlah Cicilan: ${item.jumlah}\nSisa Cicil Ke: ${item.sisaKe}\nSisa Cicilan: ${item.sisa}"
                    )
                }
                listView.adapter = ArrayAdapter(this, R.layout.row_data_item, R.id.tvDataItem, listData)
            },
            { Toast.makeText(this, "Gagal ambil data cicilan", Toast.LENGTH_SHORT).show() }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun showAksiDialog(position: Int) {
        val item = listCicil[position]
        AlertDialog.Builder(this)
            .setTitle("Aksi Data Cicilan")
            .setItems(arrayOf("Edit", "Hapus")) { _, which ->
                if (which == 0) showEditDialog(item) else konfirmasiHapus(item)
            }
            .show()
    }

    private fun showTambahDialog() {
        if (listKodeKredit.isEmpty()) {
            Toast.makeText(this, "Data kode kredit belum siap, coba lagi", Toast.LENGTH_SHORT).show()
            loadMasterData()
            return
        }

        val container = dialogContainer()
        val inputKode = EditText(this).apply { hint = "Kode Cicilan" }
        val spinnerKodeKredit = createSpinner(listKodeKredit)
        val inputTanggal = createDateInput("")
        val inputCicilanKe = EditText(this).apply { hint = "Cicilan Ke" }
        val inputJumlah = EditText(this).apply { hint = "Jumlah Cicilan" }
        val inputSisaKe = EditText(this).apply { hint = "Sisa Cicil Ke" }
        val inputSisa = EditText(this).apply { hint = "Sisa Cicilan" }

        container.addView(inputKode)
        addLabel(container, "Kode Kredit")
        container.addView(spinnerKodeKredit)
        container.addView(inputTanggal)
        container.addView(inputCicilanKe)
        container.addView(inputJumlah)
        container.addView(inputSisaKe)
        container.addView(inputSisa)

        AlertDialog.Builder(this)
            .setTitle("Tambah Data Cicilan")
            .setView(container)
            .setPositiveButton("Simpan") { _, _ ->
                val kode = inputKode.text.toString().trim()
                val kodeKredit = spinnerKodeKredit.selectedItem?.toString().orEmpty()
                val tanggal = inputTanggal.text.toString().trim()
                val cicilanKe = inputCicilanKe.text.toString().trim()
                val jumlah = inputJumlah.text.toString().trim()
                val sisaKe = inputSisaKe.text.toString().trim()
                val sisa = inputSisa.text.toString().trim()
                if (kode.isEmpty() || kodeKredit.isEmpty() || tanggal.isEmpty() || cicilanKe.isEmpty() || jumlah.isEmpty() || sisaKe.isEmpty() || sisa.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else {
                    tambahCicilan(kode, kodeKredit, tanggal, cicilanKe, jumlah, sisaKe, sisa)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showEditDialog(item: Cicilan) {
        if (listKodeKredit.isEmpty()) {
            Toast.makeText(this, "Data kode kredit belum siap, coba lagi", Toast.LENGTH_SHORT).show()
            loadMasterData()
            return
        }

        val container = dialogContainer()
        val spinnerKodeKredit = createSpinner(listKodeKredit, item.kodeKredit)
        val inputTanggal = createDateInput(item.tanggal)
        val inputCicilanKe = EditText(this).apply {
            hint = "Cicilan Ke"
            setText(item.cicilanKe)
        }
        val inputJumlah = EditText(this).apply {
            hint = "Jumlah Cicilan"
            setText(item.jumlah)
        }
        val inputSisaKe = EditText(this).apply {
            hint = "Sisa Cicil Ke"
            setText(item.sisaKe)
        }
        val inputSisa = EditText(this).apply {
            hint = "Sisa Cicilan"
            setText(item.sisa)
        }

        addLabel(container, "Kode Kredit")
        container.addView(spinnerKodeKredit)
        container.addView(inputTanggal)
        container.addView(inputCicilanKe)
        container.addView(inputJumlah)
        container.addView(inputSisaKe)
        container.addView(inputSisa)

        AlertDialog.Builder(this)
            .setTitle("Edit Cicilan (${item.kode})")
            .setView(container)
            .setPositiveButton("Simpan") { _, _ ->
                val kodeKredit = spinnerKodeKredit.selectedItem?.toString().orEmpty()
                val tanggal = inputTanggal.text.toString().trim()
                val cicilanKe = inputCicilanKe.text.toString().trim()
                val jumlah = inputJumlah.text.toString().trim()
                val sisaKe = inputSisaKe.text.toString().trim()
                val sisa = inputSisa.text.toString().trim()
                if (kodeKredit.isEmpty() || tanggal.isEmpty() || cicilanKe.isEmpty() || jumlah.isEmpty() || sisaKe.isEmpty() || sisa.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else {
                    updateCicilan(item.kode, kodeKredit, tanggal, cicilanKe, jumlah, sisaKe, sisa)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun konfirmasiHapus(item: Cicilan) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Cicilan")
            .setMessage("Hapus data kode ${item.kode}?")
            .setPositiveButton("Hapus") { _, _ -> hapusCicilan(item.kode) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tambahCicilan(
        kode: String,
        kodeKredit: String,
        tanggal: String,
        cicilanKe: String,
        jumlah: String,
        sisaKe: String,
        sisa: String
    ) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/TambahCicilanV2.php",
            { response -> handleMutasiResponse(response, "Data cicilan berhasil ditambahkan") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "kode_cicilan" to kode,
                    "kode_kredit" to kodeKredit,
                    "tanggal_cicilan" to tanggal,
                    "cicilanke" to cicilanKe,
                    "jumlah_cicilan" to jumlah,
                    "sisacicilke" to sisaKe,
                    "sisa_cicilan" to sisa
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun updateCicilan(
        kode: String,
        kodeKredit: String,
        tanggal: String,
        cicilanKe: String,
        jumlah: String,
        sisaKe: String,
        sisa: String
    ) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/EditCicilan.php",
            { response -> handleMutasiResponse(response, "Data cicilan berhasil diupdate") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "kode_cicilan" to kode,
                    "kode_kredit" to kodeKredit,
                    "tanggal_cicilan" to tanggal,
                    "cicilanke" to cicilanKe,
                    "jumlah_cicilan" to jumlah,
                    "sisacicilke" to sisaKe,
                    "sisa_cicilan" to sisa
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun hapusCicilan(kode: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/HapusCicilan.php",
            { response -> handleMutasiResponse(response, "Data cicilan berhasil dihapus") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("kode_cicilan" to kode)
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
            hint = "Tanggal Cicilan (YYYY-MM-DD)"
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
