package com.example.freshkeeper_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

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
