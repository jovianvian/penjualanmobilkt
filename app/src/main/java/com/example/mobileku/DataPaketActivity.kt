package com.example.mobileku

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DataPaketActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private val listData = ArrayList<String>()
    private val listPaket = ArrayList<Paket>()
    private val baseUrl = "http://192.168.0.22/penjualanmobil"

    data class Paket(
        val kode: String,
        val uangMuka: String,
        val tenor: String,
        val bunga: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_paket)
        listView = findViewById(R.id.listpaket)
        val tombolTambah = findViewById<Button>(R.id.tomboltambah)

        tombolTambah.setOnClickListener { showTambahDialog() }
        listView.setOnItemClickListener { _, _, position, _ -> showAksiDialog(position) }

        loadData()
    }

    private fun loadData() {
        val request = JsonArrayRequest(
            "$baseUrl/TampilPaket.php",
            { response ->
                listData.clear()
                listPaket.clear()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val item = Paket(
                        kode = obj.optString("kode_paket", "-"),
                        uangMuka = obj.optString("uang_muka", "0"),
                        tenor = obj.optString("tenor", "0"),
                        bunga = obj.optString("bunga_cicilan", "0")
                    )
                    listPaket.add(item)
                    listData.add(
                        "Kode Paket: ${item.kode}\nUang Muka: ${item.uangMuka}\nTenor: ${item.tenor}\nBunga Cicilan: ${item.bunga}"
                    )
                }
                listView.adapter = ArrayAdapter(this, R.layout.row_data_item, R.id.tvDataItem, listData)
            },
            {
                Toast.makeText(this, "Gagal ambil data paket", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun showAksiDialog(position: Int) {
        val item = listPaket[position]
        AlertDialog.Builder(this)
            .setTitle("Aksi Data Paket")
            .setItems(arrayOf("Edit", "Hapus")) { _, which ->
                if (which == 0) showEditDialog(item) else konfirmasiHapus(item)
            }
            .show()
    }

    private fun showTambahDialog() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
        val inputKode = EditText(this).apply { hint = "Kode Paket" }
        val inputUangMuka = EditText(this).apply { hint = "Uang Muka" }
        val inputTenor = EditText(this).apply { hint = "Tenor" }
        val inputBunga = EditText(this).apply { hint = "Bunga Cicilan" }
        container.addView(inputKode)
        container.addView(inputUangMuka)
        container.addView(inputTenor)
        container.addView(inputBunga)

        AlertDialog.Builder(this)
            .setTitle("Tambah Data Paket")
            .setView(container)
            .setPositiveButton("Simpan") { _, _ ->
                val kode = inputKode.text.toString().trim()
                val uangMuka = inputUangMuka.text.toString().trim()
                val tenor = inputTenor.text.toString().trim()
                val bunga = inputBunga.text.toString().trim()
                if (kode.isEmpty() || uangMuka.isEmpty() || tenor.isEmpty() || bunga.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else {
                    tambahPaket(kode, uangMuka, tenor, bunga)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showEditDialog(item: Paket) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
        val inputUangMuka = EditText(this).apply {
            hint = "Uang Muka"
            setText(item.uangMuka)
        }
        val inputTenor = EditText(this).apply {
            hint = "Tenor"
            setText(item.tenor)
        }
        val inputBunga = EditText(this).apply {
            hint = "Bunga Cicilan"
            setText(item.bunga)
        }
        container.addView(inputUangMuka)
        container.addView(inputTenor)
        container.addView(inputBunga)

        AlertDialog.Builder(this)
            .setTitle("Edit Paket (${item.kode})")
            .setView(container)
            .setPositiveButton("Simpan") { _, _ ->
                val uangMuka = inputUangMuka.text.toString().trim()
                val tenor = inputTenor.text.toString().trim()
                val bunga = inputBunga.text.toString().trim()
                if (uangMuka.isEmpty() || tenor.isEmpty() || bunga.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else {
                    updatePaket(item.kode, uangMuka, tenor, bunga)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun konfirmasiHapus(item: Paket) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Paket")
            .setMessage("Hapus data kode ${item.kode}?")
            .setPositiveButton("Hapus") { _, _ -> hapusPaket(item.kode) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tambahPaket(kode: String, uangMuka: String, tenor: String, bunga: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/TambahPaketV2.php",
            { response -> handleMutasiResponse(response, "Data paket berhasil ditambahkan") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "kode_paket" to kode,
                    "uang_muka" to uangMuka,
                    "tenor" to tenor,
                    "bunga_cicilan" to bunga
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun updatePaket(kode: String, uangMuka: String, tenor: String, bunga: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/EditPaket.php",
            { response -> handleMutasiResponse(response, "Data paket berhasil diupdate") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "kode_paket" to kode,
                    "uang_muka" to uangMuka,
                    "tenor" to tenor,
                    "bunga_cicilan" to bunga
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun hapusPaket(kode: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/HapusPaket.php",
            { response -> handleMutasiResponse(response, "Data paket berhasil dihapus") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("kode_paket" to kode)
            }
        }
        Volley.newRequestQueue(this).add(request)
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
            if (success) loadData()
        } catch (_: Exception) {
            Toast.makeText(this, "Response server tidak valid", Toast.LENGTH_SHORT).show()
        }
    }
}
