package com.example.freshkeeper_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class login_activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var correoEditText: EditText
    private lateinit var contraseñaEditText: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnRegistrarse: TextView
    private lateinit var txtolvidosucontraseña: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        correoEditText = findViewById(R.id.txtcorreoelectronico)
        contraseñaEditText = findViewById(R.id.edtContraseña)
        btnIniciarSesion = findViewById(R.id.btn_inicarsesion)
        btnRegistrarse = findViewById(R.id.textView7)
        txtolvidosucontraseña = findViewById(R.id.txtolvidosucontraseña)

        btnIniciarSesion.setOnClickListener {
            val correo = correoEditText.text.toString().trim()
            val contraseña = contraseñaEditText.text.toString().trim()

            if (correo.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "debe llenar todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                iniciarSesion(correo, contraseña)
            }
        }

        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, registrar_activity::class.java)
            startActivity(intent)
        }

        txtolvidosucontraseña.setOnClickListener {
            val intent = Intent(this, recuperacioncontra::class.java)
            startActivity(intent)
        }
    }

    private fun iniciarSesion(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()


                    programarAlarmasDesdeFirestore()

                    startActivity(Intent(this, home_activity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun programarAlarmasDesdeFirestore() {
        val db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("productos")

        productosRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val nombreProducto = document.getString("Nombre") ?: ""
                val fechaVencimiento = document.getTimestamp("Fecha_registro")?.toDate()

                if (fechaVencimiento != null) {
                    programarNotificacion(nombreProducto, fechaVencimiento.toString(), this)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener productos", Toast.LENGTH_SHORT).show()
        }
    }

}
