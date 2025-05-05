package com.example.casehive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casehive.R
import com.example.casehive.models.Vivienda

class MisViviendasAdapter(
    private val lista: List<Vivienda>,
    val onEditar: (Vivienda) -> Unit,
    val onEliminar: (Vivienda) -> Unit
) : RecyclerView.Adapter<MisViviendasAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tipo: TextView = view.findViewById(R.id.textTipo)
        val ubicacion: TextView = view.findViewById(R.id.textUbicacion)
        val precio: TextView = view.findViewById(R.id.textPrecio)
        val imagen: ImageView = view.findViewById(R.id.imageVivienda)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mis_viviendas, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vivienda = lista[position]
        holder.tipo.text = vivienda.tipo
        holder.ubicacion.text = vivienda.ubicación
        holder.precio.text = "€ ${vivienda.precio}"

        Glide.with(holder.itemView.context).load(vivienda.imágenes.firstOrNull())
            .into(holder.imagen)

        holder.btnEditar.setOnClickListener { onEditar(vivienda) }
        holder.btnEliminar.setOnClickListener { onEliminar(vivienda) }
    }

    override fun getItemCount() = lista.size
}
