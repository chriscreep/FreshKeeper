package com.example.freshkeeper_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class recuperacioncontra : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var btnEnviarCorreo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperacioncontra)




        auth = FirebaseAuth.getInstance()


        emailEditText = findViewById(R.id.txtcorreoelectronico)
        btnEnviarCorreo = findViewById(R.id.btn_enviarcorreo)

        btnEnviarCorreo.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Se ha enviado un correo de recuperación", Toast.LENGTH_SHORT).show()
                        } else {

                            val errorMessage = if (task.exception is com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                                "Correo no vinculado a FreshKeeper"
                            } else {
                                "Error: ${task.exception?.message}"
                            }
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

    }
}
