package com.example.eyecatcher3

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var distanceEditText: EditText
    private lateinit var confirmDistanceButton: Button
    private lateinit var notificationSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        distanceEditText = findViewById(R.id.distanceEditText)
        confirmDistanceButton = findViewById(R.id.confirmDistanceButton)
        notificationSwitch = findViewById(R.id.notificationSwitch)
        val logoutButton: Button = findViewById(R.id.logoutButton)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Recuperar datos guardados en Firestore desde la colección "eventos"
            db.collection("eventos").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val notificationState = document.getBoolean("notificationState") ?: false
                        val distance = document.getLong("distance")?.toInt() ?: 0
                        notificationSwitch.isChecked = notificationState
                        distanceEditText.setText(distance.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar configuración: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Guardar el estado del switch "Notificaciones"
            notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                db.collection("eventos").document(userId)
                    .update("notificationState", isChecked)
                    .addOnSuccessListener {
                        val message = if (isChecked) "Notificaciones activadas" else "Notificaciones desactivadas"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar estado de notificaciones: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            // Guardar la distancia al hacer clic en Confirmar Distancia
            confirmDistanceButton.setOnClickListener {
                val distanceText = distanceEditText.text.toString()
                val distance = distanceText.toIntOrNull()

                if (distance != null && distance in 1..300) {
                    db.collection("eventos").document(userId)
                        .update("distance", distance)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Distancia guardada", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar la distancia: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Por favor, ingresa una distancia válida (1-300 cm)", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }

        // Configurar el botón de cerrar sesión
        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // Manejar el evento de la flecha de "Atrás"
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
