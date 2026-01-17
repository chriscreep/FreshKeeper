package com.example.freshkeeper_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class usuario_fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_usuario_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtGmail = view.findViewById<TextView>(R.id.txtgmial)
        val btnCerrarSesion = view.findViewById<Button>(R.id.btncerrarsesion)

        val user = FirebaseAuth.getInstance().currentUser
        txtGmail.text = user?.email ?: "Usuario desconocido"

        btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()


            cancelarTodasLasAlarmas()

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

            activity?.finish()
        }
    }


    private fun cancelarTodasLasAlarmas() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificacionReceiver::class.java)

        val db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("productos")

        productosRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val id = document.getString("Id_nombre")?.hashCode() ?: continue
                val pendingIntent = PendingIntent.getBroadcast(
                    requireContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
            }

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al cancelar alarmas", Toast.LENGTH_SHORT).show()
        }
    }
}
