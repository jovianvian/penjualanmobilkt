package com.example.mobileku

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DataMobilActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private val listData = ArrayList<String>()
    private val listMobil = ArrayList<Mobil>()
    private val baseUrl = "http://192.168.0.22/penjualanmobil"
    private var imagePickCallback: ((Uri) -> Unit)? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imagePickCallback?.invoke(uri)
        }
    }

    data class Mobil(
        val kode: String,
        val merk: String,
        val type: String,
        val warna: String,
        val harga: String,
        val foto: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_mobil)
        listView = findViewById(R.id.listmobil)
        val tombolTambah = findViewById<Button>(R.id.tomboltambah)

        tombolTambah.setOnClickListener { showTambahDialog() }
        listView.setOnItemClickListener { _, _, position, _ -> showAksiDialog(position) }

        loadData()
    }

    private fun loadData() {
        val request = JsonArrayRequest(
            "$baseUrl/TampilMobil.php",
            { response ->
                listData.clear()
                listMobil.clear()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val item = Mobil(
                        kode = obj.optString("kode_mobil", "-"),
                        merk = obj.optString("merk", "-"),
                        type = obj.optString("type", "-"),
                        warna = obj.optString("warna", "-"),
                        harga = obj.optString("harga", "0"),
                        foto = obj.optString("foto_mobil", "")
                    )
                    listMobil.add(item)
                    listData.add(
                        "Kode: ${item.kode}\nMerk: ${item.merk}\nType: ${item.type}\nWarna: ${item.warna}\nHarga: ${item.harga}\nFoto: ${if (item.foto.isNotEmpty()) "Ada" else "Belum"}"
                    )
                }
                listView.adapter = ArrayAdapter(this, R.layout.row_data_item, R.id.tvDataItem, listData)
            },
            {
                Toast.makeText(this, "Gagal ambil data mobil", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun showAksiDialog(position: Int) {
        val item = listMobil[position]
        AlertDialog.Builder(this)
            .setTitle("Aksi Data Mobil")
            .setItems(arrayOf("Lihat Foto", "Edit", "Hapus")) { _, which ->
                when (which) {
                    0 -> showFotoDialog(item.foto, "Foto Mobil ${item.kode}")
                    1 -> showEditDialog(item)
                    else -> konfirmasiHapus(item)
                }
            }
            .show()
    }

    private fun showTambahDialog() {
        val container = buildFormContainer()
        val inputKode = EditText(this).apply { hint = "Kode Mobil" }
        val inputMerk = EditText(this).apply { hint = "Merk" }
        val inputType = EditText(this).apply { hint = "Type" }
        val inputWarna = EditText(this).apply { hint = "Warna" }
        val inputHarga = EditText(this).apply { hint = "Harga" }
        val preview = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                320
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        val tombolPilihFoto = Button(this).apply { text = "Pilih Foto Mobil" }
        var fotoUri: Uri? = null

        tombolPilihFoto.setOnClickListener {
            imagePickCallback = { uri ->
                fotoUri = uri
                preview.setImageURI(uri)
            }
            imagePicker.launch("image/*")
        }

        container.addView(inputKode)
        container.addView(inputMerk)
        container.addView(inputType)
        container.addView(inputWarna)
        container.addView(inputHarga)
        container.addView(tombolPilihFoto)
        container.addView(preview)

        AlertDialog.Builder(this)
            .setTitle("Tambah Data Mobil")
            .setView(wrapInScroll(container))
            .setPositiveButton("Simpan") { _, _ ->
                val kode = inputKode.text.toString().trim()
                val merk = inputMerk.text.toString().trim()
                val type = inputType.text.toString().trim()
                val warna = inputWarna.text.toString().trim()
                val harga = inputHarga.text.toString().trim()
                val fotoBase64 = fotoUri?.let { uriToBase64(it) }
                if (kode.isEmpty() || merk.isEmpty() || type.isEmpty() || warna.isEmpty() || harga.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else if (fotoBase64.isNullOrEmpty()) {
                    Toast.makeText(this, "Foto mobil wajib dipilih", Toast.LENGTH_SHORT).show()
                } else {
                    tambahMobil(kode, merk, type, warna, harga, fotoBase64)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showEditDialog(item: Mobil) {
        val container = buildFormContainer()
        val inputMerk = EditText(this).apply {
            hint = "Merk"
            setText(item.merk)
        }
        val inputType = EditText(this).apply {
            hint = "Type"
            setText(item.type)
        }
        val inputWarna = EditText(this).apply {
            hint = "Warna"
            setText(item.warna)
        }
        val inputHarga = EditText(this).apply {
            hint = "Harga"
            setText(item.harga)
        }
        val preview = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                320
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        val tombolPilihFoto = Button(this).apply { text = "Ganti Foto Mobil" }
        var fotoUri: Uri? = null

        if (item.foto.isNotEmpty()) {
            loadImageInto(preview, item.foto)
        }

        tombolPilihFoto.setOnClickListener {
            imagePickCallback = { uri ->
                fotoUri = uri
                preview.setImageURI(uri)
            }
            imagePicker.launch("image/*")
        }

        container.addView(inputMerk)
        container.addView(inputType)
        container.addView(inputWarna)
        container.addView(inputHarga)
        container.addView(tombolPilihFoto)
        container.addView(preview)

        AlertDialog.Builder(this)
            .setTitle("Edit Mobil (${item.kode})")
            .setView(wrapInScroll(container))
            .setPositiveButton("Simpan") { _, _ ->
                val merk = inputMerk.text.toString().trim()
                val type = inputType.text.toString().trim()
                val warna = inputWarna.text.toString().trim()
                val harga = inputHarga.text.toString().trim()
                val fotoBase64 = fotoUri?.let { uriToBase64(it) }
                if (merk.isEmpty() || type.isEmpty() || warna.isEmpty() || harga.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else {
                    updateMobil(item.kode, merk, type, warna, harga, fotoBase64)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun konfirmasiHapus(item: Mobil) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Mobil")
            .setMessage("Hapus data kode ${item.kode}?")
            .setPositiveButton("Hapus") { _, _ -> hapusMobil(item.kode) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tambahMobil(
        kode: String,
        merk: String,
        type: String,
        warna: String,
        harga: String,
        fotoBase64: String
    ) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/TambahMobil.php",
            { response -> handleMutasiResponse(response, "Data mobil berhasil ditambahkan") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "kode_mobil" to kode,
                    "merk" to merk,
                    "type" to type,
                    "warna" to warna,
                    "harga" to harga,
                    "foto_mobil_base64" to fotoBase64
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun updateMobil(
        kode: String,
        merk: String,
        type: String,
        warna: String,
        harga: String,
        fotoBase64: String?
    ) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/EditMobil.php",
            { response -> handleMutasiResponse(response, "Data mobil berhasil diupdate") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = hashMapOf(
                    "kode_mobil" to kode,
                    "merk" to merk,
                    "type" to type,
                    "warna" to warna,
                    "harga" to harga
                )
                if (!fotoBase64.isNullOrEmpty()) {
                    params["foto_mobil_base64"] = fotoBase64
                }
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun hapusMobil(kode: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/HapusMobil.php",
            { response -> handleMutasiResponse(response, "Data mobil berhasil dihapus") },
            { Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("kode_mobil" to kode)
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun buildFormContainer(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
    }

    private fun wrapInScroll(view: LinearLayout): ScrollView {
        return ScrollView(this).apply { addView(view) }
    }

    private fun showFotoDialog(fotoPath: String, title: String) {
        if (fotoPath.isEmpty()) {
            Toast.makeText(this, "Foto belum tersedia", Toast.LENGTH_SHORT).show()
            return
        }
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                700
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        loadImageInto(imageView, fotoPath)
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(imageView)
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun loadImageInto(imageView: ImageView, fotoPath: String) {
        val imageUrl = buildImageUrl(fotoPath)
        val imageRequest = ImageRequest(
            imageUrl,
            { bitmap -> imageView.setImageBitmap(bitmap) },
            0,
            0,
            ImageView.ScaleType.CENTER_CROP,
            null,
            { imageView.setImageResource(android.R.drawable.ic_menu_report_image) }
        )
        Volley.newRequestQueue(this).add(imageRequest)
    }

    private fun buildImageUrl(path: String): String {
        val cleanPath = path.trimStart('/')
        return "$baseUrl/$cleanPath"
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val input = contentResolver.openInputStream(uri) ?: return null
            val bytes = input.readBytes()
            input.close()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (_: Exception) {
            null
        }
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
