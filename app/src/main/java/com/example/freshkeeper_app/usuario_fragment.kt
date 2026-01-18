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
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class usuario_fragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var tvCountTotal: TextView
    private lateinit var tvCountVencidos: TextView
    private lateinit var tvEmailUsuario: TextView
    private var productosListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_usuario_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar referencias de la UI
        tvEmailUsuario = view.findViewById(R.id.txtgmial)
        tvCountTotal = view.findViewById(R.id.countProducts)
        tvCountVencidos = view.findViewById(R.id.countProdVen)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            tvEmailUsuario.text = currentUser.email
            escucharEstadisticas(currentUser.uid)
        }

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

    private fun escucharEstadisticas(uid: String) {
        productosListener = db.collection("usuarios")
            .document(uid)
            .collection("productos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val total = snapshot.size()
                    var vencidos = 0
                    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val hoy = Calendar.getInstance().time

                    for (documento in snapshot) {
                        val fechaVencStr = documento.getString("fechaVencimiento")
                        if (!fechaVencStr.isNullOrEmpty()) {
                            try {
                                val fechaVencimiento = formatoFecha.parse(fechaVencStr)
                                if (fechaVencimiento != null && fechaVencimiento.before(hoy)) {
                                    vencidos++
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                    }
                    tvCountTotal.text = total.toString()
                    tvCountVencidos.text = vencidos.toString()
                }
            }
    }

    // Es vital detener el listener cuando el fragmento se destruye
    override fun onDestroyView() {
        super.onDestroyView()
        productosListener?.remove()
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
