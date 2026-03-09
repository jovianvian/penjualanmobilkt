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

class DataPembeliActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private val listData = ArrayList<String>()
    private val listPembeli = ArrayList<Pembeli>()
    private val baseUrl = "http://192.168.0.22/penjualanmobil"
    private var imagePickCallback: ((Uri) -> Unit)? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imagePickCallback?.invoke(uri)
        }
    }

    data class Pembeli(
        val ktp: String,
        val nama: String,
        val alamat: String,
        val telp: String,
        val fotoKtp: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pembeli)
        listView = findViewById(R.id.listpembeli)
        val tombolTambah = findViewById<Button>(R.id.tomboltambah)
        tombolTambah.setOnClickListener { showTambahDialog() }
        listView.setOnItemClickListener { _, _, position, _ -> showAksiDialog(position) }
        loadData()
    }

    private fun loadData() {
        val request = JsonArrayRequest(
            "$baseUrl/Tampilpembeli.php",
            { response ->
                listData.clear()
                listPembeli.clear()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val item = Pembeli(
                        ktp = obj.optString("ktp", "-"),
                        nama = obj.optString("nama_pembeli", "-"),
                        alamat = obj.optString("alamat_pembeli", "-"),
                        telp = obj.optString("telp_pembeli", obj.optString("tlp_pembeli", "-")),
                        fotoKtp = obj.optString("foto_ktp", "")
                    )
                    listPembeli.add(item)
                    listData.add(
                        "KTP: ${item.ktp}\nNama: ${item.nama}\nAlamat: ${item.alamat}\nTelepon: ${item.telp}\nFoto KTP: ${if (item.fotoKtp.isNotEmpty()) "Ada" else "Belum"}"
                    )
                }

                listView.adapter = ArrayAdapter(this, R.layout.row_data_item, R.id.tvDataItem, listData)
            },
            {
                it.printStackTrace()
                Toast.makeText(this, "Gagal ambil data pembeli", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun showAksiDialog(position: Int) {
        val item = listPembeli[position]
        val pilihan = arrayOf("Lihat Foto KTP", "Edit", "Hapus")
        AlertDialog.Builder(this)
            .setTitle("Aksi Data Pembeli")
            .setItems(pilihan) { _, which ->
                when (which) {
                    0 -> showFotoDialog(item.fotoKtp, "Foto KTP ${item.ktp}")
                    1 -> showEditDialog(item)
                    else -> konfirmasiHapus(item)
                }
            }
            .show()
    }

    private fun showEditDialog(item: Pembeli) {
        val container = buildFormContainer()

        val inputNama = EditText(this).apply {
            hint = "Nama"
            setText(item.nama)
        }
        val inputAlamat = EditText(this).apply {
            hint = "Alamat"
            setText(item.alamat)
        }
        val inputTelp = EditText(this).apply {
            hint = "Telepon"
            setText(item.telp)
        }
        val preview = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                320
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        val tombolPilih = Button(this).apply { text = "Ganti Foto KTP" }
        var fotoUri: Uri? = null

        if (item.fotoKtp.isNotEmpty()) {
            loadImageInto(preview, item.fotoKtp)
        }

        tombolPilih.setOnClickListener {
            imagePickCallback = { uri ->
                fotoUri = uri
                preview.setImageURI(uri)
            }
            imagePicker.launch("image/*")
        }

        container.addView(inputNama)
        container.addView(inputAlamat)
        container.addView(inputTelp)
        container.addView(tombolPilih)
        container.addView(preview)

        AlertDialog.Builder(this)
            .setTitle("Edit Pembeli (KTP: ${item.ktp})")
            .setView(wrapInScroll(container))
            .setPositiveButton("Simpan") { _, _ ->
                val namaBaru = inputNama.text.toString().trim()
                val alamatBaru = inputAlamat.text.toString().trim()
                val telpBaru = inputTelp.text.toString().trim()
                val fotoBase64 = fotoUri?.let { uriToBase64(it) }
                if (namaBaru.isEmpty() || alamatBaru.isEmpty() || telpBaru.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else {
                    updatePembeli(item.ktp, namaBaru, alamatBaru, telpBaru, fotoBase64)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showTambahDialog() {
        val container = buildFormContainer()

        val inputKtp = EditText(this).apply { hint = "KTP" }
        val inputNama = EditText(this).apply { hint = "Nama" }
        val inputAlamat = EditText(this).apply { hint = "Alamat" }
        val inputTelp = EditText(this).apply { hint = "Telepon" }
        val preview = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                320
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        val tombolPilih = Button(this).apply { text = "Pilih Foto KTP" }
        var fotoUri: Uri? = null

        tombolPilih.setOnClickListener {
            imagePickCallback = { uri ->
                fotoUri = uri
                preview.setImageURI(uri)
            }
            imagePicker.launch("image/*")
        }

        container.addView(inputKtp)
        container.addView(inputNama)
        container.addView(inputAlamat)
        container.addView(inputTelp)
        container.addView(tombolPilih)
        container.addView(preview)

        AlertDialog.Builder(this)
            .setTitle("Tambah Data Pembeli")
            .setView(wrapInScroll(container))
            .setPositiveButton("Simpan") { _, _ ->
                val ktp = inputKtp.text.toString().trim()
                val nama = inputNama.text.toString().trim()
                val alamat = inputAlamat.text.toString().trim()
                val telp = inputTelp.text.toString().trim()
                val fotoBase64 = fotoUri?.let { uriToBase64(it) }
                if (ktp.isEmpty() || nama.isEmpty() || alamat.isEmpty() || telp.isEmpty()) {
                    Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                } else if (fotoBase64.isNullOrEmpty()) {
                    Toast.makeText(this, "Foto KTP wajib dipilih", Toast.LENGTH_SHORT).show()
                } else {
                    tambahPembeli(ktp, nama, alamat, telp, fotoBase64)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tambahPembeli(ktp: String, nama: String, alamat: String, telp: String, fotoBase64: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/Tambahpembeli.php",
            { response ->
                handleMutasiResponse(response, "Data pembeli berhasil ditambahkan")
            },
            {
                Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "ktp" to ktp,
                    "nama_pembeli" to nama,
                    "alamat_pembeli" to alamat,
                    "telp_pembeli" to telp,
                    "foto_ktp_base64" to fotoBase64
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun updatePembeli(ktp: String, nama: String, alamat: String, telp: String, fotoBase64: String?) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/Editpembeli.php",
            { response ->
                handleMutasiResponse(response, "Data pembeli berhasil diupdate")
            },
            {
                Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = hashMapOf(
                    "ktp" to ktp,
                    "nama_pembeli" to nama,
                    "alamat_pembeli" to alamat,
                    "telp_pembeli" to telp
                )
                if (!fotoBase64.isNullOrEmpty()) {
                    params["foto_ktp_base64"] = fotoBase64
                }
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun konfirmasiHapus(item: Pembeli) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Pembeli")
            .setMessage("Hapus data KTP ${item.ktp}?")
            .setPositiveButton("Hapus") { _, _ ->
                hapusPembeli(item.ktp)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusPembeli(ktp: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/Hapuspembeli.php",
            { response ->
                handleMutasiResponse(response, "Data pembeli berhasil dihapus")
            },
            {
                Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("ktp" to ktp)
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
            Toast.makeText(this, "Foto KTP belum tersedia", Toast.LENGTH_SHORT).show()
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
            if (success) {
                loadData()
            }
        } catch (_: Exception) {
            Toast.makeText(this, "Response server tidak valid", Toast.LENGTH_SHORT).show()
        }
    }
}
