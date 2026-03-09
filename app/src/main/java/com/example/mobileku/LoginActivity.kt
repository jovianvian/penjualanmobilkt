package com.example.mobileku

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private val baseUrl = "http://192.168.0.22/penjualanmobil"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SessionManager.isLoggedIn(this)) {
            bukaDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        val inputUsername = findViewById<EditText>(R.id.inputUsername)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val tombolLogin = findViewById<Button>(R.id.tombolLogin)

        tombolLogin.setOnClickListener {
            val username = inputUsername.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                login(username, password)
            }
        }
    }

    private fun login(username: String, password: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/Login.php",
            { response ->
                try {
                    val json = JSONObject(response)
                    val success = json.optBoolean("success", false)
                    val message = json.optString("message", "Login gagal")
                    if (success) {
                        val level = json.optInt("level", 1)
                        val role = if (level == 2) "manager" else "staff"
                        SessionManager.saveLogin(this, username, role)
                        Toast.makeText(this, "Login berhasil sebagai $role", Toast.LENGTH_SHORT).show()
                        bukaDashboard()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (_: Exception) {
                    Toast.makeText(this, "Response login tidak valid", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "username" to username,
                    "password" to password
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun bukaDashboard() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
