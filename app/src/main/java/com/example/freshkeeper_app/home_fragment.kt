package com.example.freshkeeper_app

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.freshkeeper_app.com.example.freshkeeper_app.RecomendacionAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class home_fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Asegúrate de que el nombre del layout coincida (fragment_home_fragment)
        return inflater.inflate(R.layout.fragment_home_fragment_, container, false)
    }


    private lateinit var viewPager: ViewPager2
    private val sliderHandler = android.os.Handler(android.os.Looper.getMainLooper())

    private val listaRecomendaciones = listOf(
        Recomendacion("Guarda las papas y cebollas por separado para evitar que se dañen rápido.", R.drawable.bg_food),
        Recomendacion("Envuelve las hojas verdes en papel absorbente para reducir la humedad." ,R.drawable.bg_food2),
        Recomendacion("No guardes la leche en la puerta del refrigerador por los cambios de temperatura.",R.drawable.bg_food3),
        Recomendacion("Conserva los bananos a temperatura ambiente para evitar que se pongan negros.",R.drawable.bg_food4),
        Recomendacion("Congela el pan en bolsas herméticas para mantener su textura por más tiempo.",R.drawable.bg_food5),
        Recomendacion("Los cítricos duran más tiempo si se guardan en el cajón inferior del refrigerador.",R.drawable.bg_food6),
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicialización de Vistas
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvVencimientos)
        viewPager = view.findViewById<ViewPager2>(R.id.vpRecomendaciones)
        val tabLayout = view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tlIndicador)
        val btnAgregar = view.findViewById<android.widget.Button>(R.id.btnagregar2)

        // 2. Configuración de LayoutManagers y Adaptadores
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Configuración del Carrusel (ViewPager2)
        viewPager.adapter = RecomendacionAdapter(listaRecomendaciones)

        // Vincular los puntos indicadores (dots) con el carrusel
        com.google.android.material.tabs.TabLayoutMediator(tabLayout, viewPager) { tab, _ ->
            tab.text = null // Forzamos a que no haya texto que estire el contenedor
        }.attach()

        // 3. Configuración de Listeners (Eventos)
        btnAgregar.setOnClickListener {
            val fragment = Agregarproducto_fragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        // 4. Lógica de Firebase y Carga de Datos
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
                    val producto = Producto(
                        categoria = document.getString("categoria") ?: "",
                        nombre = document.getString("nombre") ?: "",
                        fechaVencimiento = document.getString("fechaVencimiento") ?: "",
                        fechaCompra = document.getString("fechaCompra") ?: "",
                        docId = document.id
                    )
                    listaProductos.add(producto)
                }

                // Asignar el adaptador de productos
                recyclerView.adapter = ProductoRecyclerAdapter(listaProductos) { productoSeleccionado ->
                    abrirDetalleProducto(productoSeleccionado)
                }

                // Actualizar mensaje dinámico (conteo en naranja)
                actualizarMensajeVencimiento(listaProductos.size)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // 5. Iniciar el carrusel automático
        sliderHandler.postDelayed(sliderRunnable, 10000)
    }
    // Lógica para mover el carrusel automáticamente cada 10 segundos
    private val sliderRunnable = object : Runnable {
        override fun run() {
            if (::viewPager.isInitialized && listaRecomendaciones.isNotEmpty()) {
                val nextItem = (viewPager.currentItem + 1) % listaRecomendaciones.size
                viewPager.setCurrentItem(nextItem, true)
                sliderHandler.postDelayed(this, 10000)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Importante: Esto evita que el carrusel intente moverse
        // cuando el fragmento ya no es visible.
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    private fun actualizarMensajeVencimiento(cantidad: Int) {
        val textoBase = "¡Cocina algo increíble con estos $cantidad alimentos!"
        val spannable = SpannableStringBuilder(textoBase)

        val numeroStr = cantidad.toString()
        val inicio = textoBase.indexOf(numeroStr)
        val fin = inicio + numeroStr.length

        if (inicio != -1) {
            // Color Naranja (#FF9800)
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#FF9800")),
                inicio,
                fin,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // Negrita
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                inicio,
                fin,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Asegúrate de que el ID tvMensajeVencimiento esté en tu XML
        view?.findViewById<TextView>(R.id.tvMensajeVencimiento)?.text = spannable
    }

    private fun abrirDetalleProducto(producto: Producto) {
        val fragment = verproducto_fragment().apply {
            arguments = Bundle().apply {
                putString("docId", producto.docId)
                putString("nombre", producto.nombre)
                putString("categoria", producto.categoria)
                putString("fecha_compra", producto.fechaCompra)
                putString("fecha_vencimiento", producto.fechaVencimiento)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}



// Datos que tendrá cada tarjeta
data class Recomendacion(
    val texto: String,
    val imagenResId: Int // ID del recurso drawable (ej. R.drawable.fruta)
)

