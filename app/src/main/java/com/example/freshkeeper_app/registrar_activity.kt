package com.example.freshkeeper_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class registrar_activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var correoEditText: EditText
    private lateinit var contraseñaEditText: EditText
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)


        auth = FirebaseAuth.getInstance()


        correoEditText = findViewById(R.id.txtcorreoelectronico)
        contraseñaEditText = findViewById(R.id.edtContraseña)
        btnRegistrar = findViewById(R.id.button2)


        btnRegistrar.setOnClickListener {
            val correo = correoEditText.text.toString().trim()
            val contraseña = contraseñaEditText.text.toString().trim()

            if (correo.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "debe llenar todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                registrarUsuario(correo, contraseña)
            }
        }
    }

    private fun registrarUsuario(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, login_activity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
