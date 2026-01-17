package com.example.freshkeeper_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class home_fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_fragment_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listview)
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "Inicia sesión para ver tus productos", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = currentUser.uid

        db.collection("usuarios")
            .document(uid)
            .collection("productos")
            .get()
            .addOnSuccessListener { querySnapshot ->

                if (!isAdded) return@addOnSuccessListener

                val listaProductos = mutableListOf<Producto>()

                for (document in querySnapshot) {
                    val categoria = document.getString("categoria") ?: ""
                    val nombre = document.getString("nombre") ?: ""
                    val fechaCompra = document.getString("fechaCompra") ?: ""
                    val fechaVencimiento = document.getString("fechaVencimiento") ?: ""
                    val docId = document.id

                    val producto = Producto(
                        categoria = categoria,
                        nombre = nombre,
                        fechaVencimiento = fechaVencimiento,
                        fechaCompra = fechaCompra,
                        docId = docId
                    )
                    listaProductos.add(producto)
                }

                val adapter = ProductoAdapter(requireContext(), listaProductos)
                listView.adapter = adapter

                listView.setOnItemClickListener { _, _, position, _ ->
                    val productoSeleccionado = listaProductos[position]
                    val fragment = verproducto_fragment().apply {
                        arguments = Bundle().apply {
                            putString("docId", productoSeleccionado.docId)
                            putString("nombre", productoSeleccionado.nombre)
                            putString("categoria", productoSeleccionado.categoria)
                            putString("fecha_compra", productoSeleccionado.fechaCompra)
                            putString("fecha_vencimiento", productoSeleccionado.fechaVencimiento)
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al obtener productos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
