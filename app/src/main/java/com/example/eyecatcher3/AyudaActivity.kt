package com.example.eyecatcher3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AyudaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayuda)

        // Referencia al botón "Volver al inicio de sesión"
        val backToLoginButton: Button = findViewById(R.id.backToLoginButton)

        // Al hacer clic en el botón, vuelve al LoginActivity
        backToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Cierra la actividad actual
        }
    }
}
