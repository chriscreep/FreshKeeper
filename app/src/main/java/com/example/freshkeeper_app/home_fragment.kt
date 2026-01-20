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
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class home_fragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private val sliderHandler = android.os.Handler(android.os.Looper.getMainLooper())

    private val listaRecomendaciones = listOf(
        Recomendacion("Guarda las papas y cebollas por separado para evitar que se dañen rápido.", R.drawable.bg_food),
        Recomendacion("Envuelve las hojas verdes en papel absorbente para reducir la humedad." ,R.drawable.bg_food2),
        Recomendacion("No guardes la leche en la puerta del refrigerador por los cambios de temperatura.",R.drawable.bg_food3),
        Recomendacion("Conserva los bananos a temperatura ambiente para evitar que se pongan negros.",R.drawable.bg_food4),
        Recomendacion("Congela el pan en bolsas herméticas para mantener su textura por más tiempo.",R.drawable.bg_food5),
        Recomendacion("Los cítricos duran más tiempo si se guardan en el cajón inferior del refrigerador.",R.drawable.bg_food6),
        Recomendacion("Mantén la heladera limpia, así evitas que residuos aceleran la descomposición de alimentos.", R.drawable.bg_food7),
        Recomendacion("No guardes tomates en la nevera; mantenlos a temperatura ambiente para que conserven sabor.", R.drawable.bg_food8),
        Recomendacion("Mantén los huevos en su caja original dentro del refrigerador para mayor duración.", R.drawable.bg_food9),
        Recomendacion("Guarda los productos lácteos en la parte central del refrigerador, no en la puerta.", R.drawable.bg_food11),

    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_fragment_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvVencimientos)
        viewPager = view.findViewById(R.id.vpRecomendaciones)
        val tabLayout = view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tlIndicador)
        val btnAgregar = view.findViewById<android.widget.Button>(R.id.btnagregar2)
        val tvMensajeVencimiento = view.findViewById<TextView>(R.id.tvMensajeVencimiento)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Carrusel
        viewPager.adapter = RecomendacionAdapter(listaRecomendaciones)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        btnAgregar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, Agregarproducto_fragment())
                .addToBackStack(null)
                .commit()
        }

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
                    listaProductos.add(
                        Producto(
                            categoria = document.getString("categoria") ?: "",
                            nombre = document.getString("nombre") ?: "",
                            fechaVencimiento = document.getString("fechaVencimiento") ?: "",
                            fechaCompra = document.getString("fechaCompra") ?: "",
                            docId = document.id
                        )
                    )
                }

                // --- NUEVO: FILTRO DE PRODUCTOS POR VENCER ---
                val productosFinal = productosPorVencer(listaProductos)

                if (productosFinal.isEmpty()) {
                    tvMensajeVencimiento.text = "No hay productos por vencer esta semana "
                } else {
                    actualizarMensajeVencimiento(productosFinal.size)
                }

                recyclerView.adapter =
                    ProductoRecyclerAdapter(productosFinal) { producto ->
                        abrirDetalleProducto(producto)
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        sliderHandler.postDelayed(sliderRunnable, 10000)
    }

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
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    private fun actualizarMensajeVencimiento(cantidad: Int) {
        val textoBase = "¡Cocina algo increíble con estos $cantidad alimentos!"
        val spannable = SpannableStringBuilder(textoBase)

        val numeroStr = cantidad.toString()
        val inicio = textoBase.indexOf(numeroStr)
        val fin = inicio + numeroStr.length

        if (inicio != -1) {
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#FF9800")),
                inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

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

private fun productosPorVencer(productos: List<Producto>): List<Producto> {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val hoy = Calendar.getInstance()

    return productos.filter { producto ->
        try {
            val fechaV = Calendar.getInstance().apply {
                time = sdf.parse(producto.fechaVencimiento)!!
            }

            val diff = fechaV.timeInMillis - hoy.timeInMillis
            val dias = diff / (1000 * 60 * 60 * 24)

            dias in 0..7
        } catch (_: Exception) {
            false
        }
    }
}

data class Recomendacion(
    val texto: String,
    val imagenResId: Int
)
