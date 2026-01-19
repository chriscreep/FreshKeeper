package com.example.freshkeeper_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtFechaVencimiento = view.findViewById<TextView>(R.id.txtFechaVencimiento)
        val txtFechaCompra = view.findViewById<TextView>(R.id.txtFechaCompra)

        val producto = productos[position]

        txtNombre.text = producto.nombre
        txtFechaVencimiento.text = "Día de vencimiento: ${producto.fechaVencimiento}"
        txtFechaCompra.text = "Día de compra: ${producto.fechaCompra}"

        return view
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