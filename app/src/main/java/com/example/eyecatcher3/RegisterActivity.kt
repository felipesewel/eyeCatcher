package com.example.eyecatcher3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val registerEmailEditText: EditText = findViewById(R.id.registerEmailEditText)
        val registerPasswordEditText: EditText = findViewById(R.id.registerPasswordEditText)
        val registerButton: Button = findViewById(R.id.registerButton)
        val loginRedirectTextView: TextView = findViewById(R.id.loginRedirectTextView)

        // Configurar el listener para redirigir a la actividad de inicio de sesión
        loginRedirectTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        registerButton.setOnClickListener {
            val email = registerEmailEditText.text.toString()
            val password = registerPasswordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Crear usuario en Firebase Auth
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                // Guardar el correo en Firestore junto con el userId
                                val userData = hashMapOf(
                                    "email" to email
                                )
                                db.collection("users").document(userId).set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            // Mostrar mensaje de error
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

