package com.example.eyecatcher3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class HomePageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var alarmSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        alarmSwitch = findViewById(R.id.alarmSwitch)
        val settingsButton: Button = findViewById(R.id.settingsButton)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Recuperar el estado de alarma desde "eventos"
            db.collection("eventos").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("alarmState")) {
                        val alarmState = document.getBoolean("alarmState") ?: false
                        alarmSwitch.isChecked = alarmState
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener el estado de alarma: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Guardar el estado del switch de alarma cuando cambia
            alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                val data = mapOf("alarmState" to isChecked)
                db.collection("eventos").document(userId)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        val message = if (isChecked) "Alarma activada" else "Alarma desactivada"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar el estado: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("FirestoreUpdate", "Error al guardar alarmState en eventos: ${e.message}")
                    }
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }

        // Configuración del botón de ajustes
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
