package com.example.freshkeeper_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class productos_fragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var searchEditText: EditText
    private lateinit var adapter: ProductoAdapter
    private var listaProductos = mutableListOf<Producto>()
    private var listaActual = mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_productos_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val catProteinas = view.findViewById<LinearLayout>(R.id.catProteinas)
        val catCondimentos = view.findViewById<LinearLayout>(R.id.catCondimentos)
        val catGranos = view.findViewById<LinearLayout>(R.id.catGranos)
        val catEnlatados = view.findViewById<LinearLayout>(R.id.catEnlatados)
        val catAderezos = view.findViewById<LinearLayout>(R.id.catAderezos)
        val catFrutas = view.findViewById<LinearLayout>(R.id.catFrutas)
        val catSnacks = view.findViewById<LinearLayout>(R.id.catSnacks)
        val catOtros = view.findViewById<LinearLayout>(R.id.catOtros)
        val catTodos = view.findViewById<LinearLayout>(R.id.catTodos)

        catTodos.setOnClickListener {
            searchEditText.text.clear()
            marcarCategoriaSeleccionada(catTodos)
            mostrarTodos()
        }

        catProteinas.setOnClickListener {
            searchEditText.text.clear()
            marcarCategoriaSeleccionada(catProteinas)
            filtrarPorCategoria("carnes y proteinas")
        }

        catCondimentos.setOnClickListener {
            searchEditText.text.clear()
            marcarCategoriaSeleccionada(catCondimentos)
            filtrarPorCategoria("Aceites y condimentos")
        }

        catGranos.setOnClickListener {
            searchEditText.text.clear()
            marcarCategoriaSeleccionada(catGranos)
            filtrarPorCategoria("Granos y cereales")
        }

        catEnlatados.setOnClickListener {
            searchEditText.text.clear()
            marcarCategoriaSeleccionada(catEnlatados)
            filtrarPorCategoria("Enlatados")
        }

        catAderezos.setOnClickListener {
            searchEditText.text.clear()
            marcarCategoriaSeleccionada(catAderezos)
            filtrarPorCategoria("Salsa y aderezos")
        }

        catFrutas.setOnClickListener {
            searchEditText.text.clear()
            marcarCategoriaSeleccionada(catFrutas)
            filtrarPorCategoria("Frutas y verduras")
        }

        catSnacks.setOnClickListener {
            searchEditText.text.clear()
            marcarCategoriaSeleccionada(catSnacks)
            filtrarPorCategoria("Snacks")
        }

        catOtros.setOnClickListener {
            searchEditText.text.clear()
            marcarCategoriaSeleccionada(catOtros)
            filtrarPorCategoria("Otros")
        }



        val btnAgregar = view.findViewById<ImageView>(R.id.btniragregar)
        btnAgregar.setOnClickListener {
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

                listaActual = listaProductos.toMutableList()

                adapter = ProductoAdapter(requireContext(), listaActual)
                listView.adapter = adapter

                listView.setOnItemClickListener { _, _, position, _ ->
                    val productoSeleccionado = listaActual[position]
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


    private fun filtrarPorCategoria(categoria: String) {
        val catTrim = categoria.trim()
        listaActual = listaProductos.filter {
            it.categoria.trim().startsWith(catTrim, ignoreCase = true)
        }.toMutableList()

        adapter.updateList(listaActual)
    }


    private var categoriaSeleccionada: LinearLayout? = null

    private fun marcarCategoriaSeleccionada(layout: LinearLayout) {

        val categorias = listOf(
            requireView().findViewById<LinearLayout>(R.id.catTodos),
            requireView().findViewById<LinearLayout>(R.id.catProteinas),
            requireView().findViewById<LinearLayout>(R.id.catCondimentos),
            requireView().findViewById<LinearLayout>(R.id.catGranos),
            requireView().findViewById<LinearLayout>(R.id.catEnlatados),
            requireView().findViewById<LinearLayout>(R.id.catAderezos),
            requireView().findViewById<LinearLayout>(R.id.catFrutas),
            requireView().findViewById<LinearLayout>(R.id.catSnacks),
            requireView().findViewById<LinearLayout>(R.id.catOtros)
        )


        categorias.forEach {
            it.setBackgroundResource(R.drawable.bg_categoria_normal)
        }


        layout.setBackgroundResource(R.drawable.bg_categoria_selected)
        categoriaSeleccionada = layout
    }

    private fun mostrarTodos() {
        listaActual = listaProductos.toMutableList()
        adapter.updateList(listaActual)
    }


    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                listaActual = if (s.isNullOrEmpty()) {
                    listaProductos.toMutableList()
                } else {
                    listaProductos.filter {
                        it.nombre.contains(s.toString(), ignoreCase = true) ||
                                it.categoria.contains(s.toString(), ignoreCase = true)
                    }.toMutableList()
                }

                adapter.updateList(listaActual)
            }
        })
    }
}
