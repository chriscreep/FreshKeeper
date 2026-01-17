package com.example.freshkeeper_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class verproductosporcategoria_fragment : Fragment() {
    private var category: String? = null
    private lateinit var listView: ListView
    private lateinit var adapter: ProductoAdapter
    private val db = FirebaseFirestore.getInstance()
    private val productosList = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_verproductosporcategoria_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtNombreCategoria = view.findViewById<TextView>(R.id.txtnombrecategoria)
        txtNombreCategoria.text = category ?: "Categoría"

        listView = view.findViewById(R.id.listview)
        adapter = ProductoAdapter(requireContext(), productosList)
        listView.adapter = adapter

        obtenerProductosDesdeFirebase()
    }

    private fun obtenerProductosDesdeFirebase() {
        if (category == null) return

        db.collection("productos")
            .whereEqualTo("categoria", category)
            .get()
            .addOnSuccessListener { documents ->
                productosList.clear()
                for (document in documents) {
                    val categoria = document.getString("categoria") ?: ""
                    val nombre = document.getString("nombre") ?: ""
                    val fechaVencimiento = document.getString("fechaVencimiento") ?: ""
                    val fechaCompra = document.getString("fechaCompra") ?: ""

                    android.util.Log.d("Firebase", "Producto encontrado: $nombre - $categoria")

                    val producto = Producto(categoria, nombre, fechaVencimiento, fechaCompra, document.id)
                    productosList.add(producto)
                }

                android.util.Log.d("Firebase", "Total productos después de agregar: ${productosList.size}")

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("Firebase", "Error al obtener productos", exception)
            }

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            verproductosporcategoria_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

















}
