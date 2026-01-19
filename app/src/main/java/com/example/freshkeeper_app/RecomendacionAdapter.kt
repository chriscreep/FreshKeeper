package com.example.freshkeeper_app.com.example.freshkeeper_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshkeeper_app.R
import com.example.freshkeeper_app.Recomendacion

class RecomendacionAdapter(private val lista: List<Recomendacion>) :
    RecyclerView.Adapter<RecomendacionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFrase: TextView = view.findViewById(R.id.tvFrase)
        val ivImagen: ImageView = view.findViewById(R.id.ivRecomendacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recomendacion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.tvFrase.text = item.texto

        // Carga la imagen desde los recursos (drawable)
        holder.ivImagen.setImageResource(item.imagenResId)
    }

    override fun getItemCount(): Int = lista.size
}