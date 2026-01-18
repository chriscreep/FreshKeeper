package com.example.freshkeeper_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class productos_fragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var searchEditText: EditText
    private lateinit var adapter: ProductoAdapter
    private var listaProductos = mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_productos_fragment, container, false)
    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val btnCerrarSesion = view.findViewById<ImageView>(R.id.btniragregar)


        btnCerrarSesion.setOnClickListener {
            val fragment = Agregarproducto_fragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit()
        }


        listView = view.findViewById(R.id.listview)
        searchEditText = view.findViewById(R.id.txtcorreoelectronico2)
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

                listaProductos.clear()
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

                adapter = ProductoAdapter(requireContext(), listaProductos)
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

        setupSearch()



    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList = listaProductos.filter {
                    it.nombre.contains(s.toString(), ignoreCase = true) ||
                            it.categoria.contains(s.toString(), ignoreCase = true)
                }
                adapter.updateList(filteredList)
            }
        })
    }
}
