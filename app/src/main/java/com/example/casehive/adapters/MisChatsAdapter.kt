package com.example.casehive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.casehive.R

class MisChatsAdapter(
    private val listaUsuarios: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<MisChatsAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUsuario: TextView = itemView.findViewById(R.id.textUsuario)

        init {
            itemView.setOnClickListener {
                val id = listaUsuarios[adapterPosition]
                onClick(id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_usuario, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.textUsuario.text = "Conversación con: ${listaUsuarios[position]}"
    }

    override fun getItemCount(): Int = listaUsuarios.size
}
