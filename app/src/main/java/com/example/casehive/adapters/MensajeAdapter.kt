package com.example.casehive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.casehive.R
import com.example.casehive.models.Mensaje

class MensajeAdapter(
    private val lista: List<Mensaje>,
    private val uidActual: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TIPO_ENVIADO = 1
    private val TIPO_RECIBIDO = 2

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) TIPO_ENVIADO else TIPO_RECIBIDO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = if (viewType == TIPO_ENVIADO) R.layout.item_mensaje_enviado else R.layout.item_mensaje_recibido
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensaje = lista[position]
        (holder as MensajeViewHolder).bind(mensaje)
    }

    override fun getItemCount(): Int = lista.size

    inner class MensajeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mensajeText: TextView = view.findViewById(R.id.textMensaje)
        fun bind(mensaje: Mensaje) {
            mensajeText.text = mensaje.mensaje
        }
    }
}
