package com.example.freshkeeper_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ProductoAdapter(
    private val context: Context,
    private var productos: MutableList<Producto>
) : BaseAdapter() {

    override fun getCount(): Int = productos.size

    override fun getItem(position: Int): Any = productos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_producto, parent, false)

        val imgIcono = view.findViewById<ImageView>(R.id.imgIcono)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtFechaVencimiento = view.findViewById<TextView>(R.id.txtFechaVencimiento)
        val imgEstado = view.findViewById<ImageView>(R.id.imgEstado) // El reloj

        val producto = productos[position]

        txtNombre.text = producto.nombre
        txtFechaVencimiento.text = "Vence el: ${producto.fechaVencimiento}"
        imgIcono.setImageResource(obtenerIcono(producto.categoria))

        // Lógica del Reloj de Estado
        val diasRestantes = calcularDiasRestantes(producto.fechaVencimiento)

        when {
            diasRestantes > 7 -> {
                // Más de una semana -> VERDE
                imgEstado.setImageResource(R.drawable.ic_clock_green)
            }
            diasRestantes in 0..7 -> {
                // Una semana o menos -> NARANJA
                imgEstado.setImageResource(R.drawable.ic_clock_orange)
            }
            else -> {
                // Vencido (negativo) -> ROJO
                imgEstado.setImageResource(R.drawable.ic_clock_red)
            }
        }

        return view
    }

    private fun calcularDiasRestantes(fechaVencimientoStr: String): Long {
        return try {
            // Asegúrate de que el formato coincida con el que guardas en Firebase (ej: 15/01/2025)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fechaVencimiento = LocalDate.parse(fechaVencimientoStr, formatter)
            val hoy = LocalDate.now()

            ChronoUnit.DAYS.between(hoy, fechaVencimiento)
        } catch (e: Exception) {
            0L // Si hay error de formato, lo trata como hoy
        }
    }
    private fun obtenerIcono(categoria: String): Int {
        return when (categoria.trim().lowercase()) {

            "carnes y proteinas" -> R.drawable.ic_proteina
            "aceites y condimentos" -> R.drawable.ic_condimentos
            "granos y cereales" -> R.drawable.ic_granos
            "enlatados" -> R.drawable.ic_enlatados
            "salsas y aderezos" -> R.drawable.ic_aderezos
            "frutas y verduras" -> R.drawable.ic_vegetales
            "snacks" -> R.drawable.ic_snacks
            "otros..." -> R.drawable.ic_otros

            else -> R.drawable.ic_todos
        }
    }


    fun updateList(newList: List<Producto>) {
        productos.clear()
        productos.addAll(newList)
        notifyDataSetChanged()
    }
}



// Este adaptador es exclusivo para el RecyclerView de la Home
class ProductoRecyclerAdapter(
    private val productos: List<Producto>,
    private val onItemClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoRecyclerAdapter.ProductoViewHolder>() {

    // ViewHolder para gestionar la vista de cada item
    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.itemName)
        val txtFechaVencimiento: TextView = view.findViewById(R.id.itemDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        // Inflamos el layout item_vencimiento que diseñaste para la Home
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vencimiento, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        holder.txtNombre.text = producto.nombre
        holder.txtFechaVencimiento.text = "Vence el: ${producto.fechaVencimiento}"

        // Manejo del clic para navegar al detalle del producto
        holder.itemView.setOnClickListener { onItemClick(producto) }
    }

    override fun getItemCount(): Int = productos.size
}