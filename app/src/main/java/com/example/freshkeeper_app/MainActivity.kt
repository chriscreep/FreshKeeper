package com.example.freshkeeper_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (FirebaseAuth.getInstance().currentUser != null) {

            startActivity(Intent(this, home_activity::class.java))
            finish()
            return
        }


        setContentView(R.layout.activity_main)


        val btn_iniciarsesion = findViewById<ConstraintLayout>(R.id.constraintLayout)
        btn_iniciarsesion.setOnClickListener {
            val intent = Intent(this, login_activity::class.java)
            startActivity(intent)
        }

        val btn_registrarse = findViewById<ConstraintLayout>(R.id.btn_registrarse)
        btn_registrarse.setOnClickListener {
            val intent = Intent(this, registrar_activity::class.java)
            startActivity(intent)
        }
    }
}
