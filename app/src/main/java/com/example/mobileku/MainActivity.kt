package com.example.mobileku

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SessionManager.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val tvSubtitle = findViewById<TextView>(R.id.tvSubtitle)
        val tombolMobil = findViewById<Button>(R.id.tombolmobil)
        val tombolPembeli = findViewById<Button>(R.id.tombolpembeli)
        val tombolPaket = findViewById<Button>(R.id.tombolpaket)
        val tombolCash = findViewById<Button>(R.id.tombolbeli)
        val tombolKredit = findViewById<Button>(R.id.tombolkredit)
        val tombolCicil = findViewById<Button>(R.id.tombolcicil)
        val tombolLaporan = findViewById<Button>(R.id.tombollaporan)
        val tombolLogout = findViewById<Button>(R.id.tombollogout)
        val role = SessionManager.getRole(this)
        val username = SessionManager.getUsername(this)

        tombolMobil.setOnClickListener {
            startActivity(Intent(this, DataMobilActivity::class.java))
        }
        tombolPembeli.setOnClickListener {
            startActivity(Intent(this, DataPembeliActivity::class.java))
        }
        tombolPaket.setOnClickListener {
            startActivity(Intent(this, DataPaketActivity::class.java))
        }
        tombolCash.setOnClickListener {
            startActivity(Intent(this, DataCashActivity::class.java))
        }
        tombolKredit.setOnClickListener {
            startActivity(Intent(this, DataKreditActivity::class.java))
        }
        tombolCicil.setOnClickListener {
            startActivity(Intent(this, DataCicilActivity::class.java))
        }
        tombolLaporan.setOnClickListener {
            startActivity(Intent(this, LaporanKeuanganActivity::class.java))
        }
        tombolLogout.setOnClickListener {
            SessionManager.clear(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        if (role == "manager") {
            tvSubtitle.text = "Halo $username (Manager) - akses laporan keuangan"
            tombolMobil.visibility = View.GONE
            tombolPembeli.visibility = View.GONE
            tombolPaket.visibility = View.GONE
            tombolCash.visibility = View.GONE
            tombolKredit.visibility = View.GONE
            tombolCicil.visibility = View.GONE
            tombolLaporan.visibility = View.VISIBLE
        } else {
            tvSubtitle.text = "Halo $username (Staff) - kelola data operasional"
            tombolLaporan.visibility = View.GONE
        }
    }
}
