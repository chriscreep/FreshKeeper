package com.example.freshkeeper_app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

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
                Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                correoEditText.error = "Correo electrónico no válido"
                correoEditText.requestFocus()
                return@setOnClickListener
            }

            if (contraseña.length < 6) {

                contraseñaEditText.error = "La contraseña debe tener al menos 6 caracteres"
                contraseñaEditText.requestFocus()
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarUsuario(correo, contraseña)
        }
    }

    private fun registrarUsuario(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, login_activity::class.java))
                    finish()
                } else {
                    val exception = task.exception

                    val mensaje = when (exception) {
                        is FirebaseAuthWeakPasswordException ->
                            "La contraseña es demasiado débil. Debe tener al menos 6 caracteres."
                        is FirebaseAuthInvalidCredentialsException ->
                            "El correo electrónico no es válido."
                        is FirebaseAuthUserCollisionException ->
                            "Ya existe una cuenta registrada con este correo."
                        else -> {

                            exception?.printStackTrace()
                            "Error al registrar usuario. Intente nuevamente más tarde."
                        }
                    }
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }
            }
    }
}