package com.example.casehive.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casehive.Activitys.ChatActivity
import com.example.casehive.Activitys.DetalleViviendaActivity
import com.example.casehive.R
import com.example.casehive.models.Vivienda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViviendaAdapter(private val lista: List<Vivienda>) :
    RecyclerView.Adapter<ViviendaAdapter.ViviendaViewHolder>() {

    inner class ViviendaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tipo: TextView = itemView.findViewById(R.id.textTipo)
        val ubicacion: TextView = itemView.findViewById(R.id.textUbicacion)
        val precio: TextView = itemView.findViewById(R.id.textPrecio)
        val image: ImageView = itemView.findViewById(R.id.imageVivienda)
        val chatButton: Button = itemView.findViewById(R.id.chatButton)
        val llamarButton: Button = itemView.findViewById(R.id.llamarButton)
        val btnFavorito: ImageButton = itemView.findViewById(R.id.btnFavorito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViviendaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vivienda, parent, false)
        return ViviendaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViviendaViewHolder, position: Int) {
        val vivienda = lista[position]
        val context = holder.itemView.context
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val favRef = FirebaseDatabase.getInstance().getReference("favoritos").child(currentUserId)
        val viviendaId = "v${position + 1}"

        holder.tipo.text = vivienda.tipo
        holder.ubicacion.text = vivienda.ubicación
        holder.precio.text = "€ ${vivienda.precio}"
        Glide.with(context).load(vivienda.imágenes.firstOrNull()).into(holder.image)

        if (vivienda.creadorId == currentUserId) {
            holder.chatButton.visibility = View.GONE
            holder.llamarButton.visibility = View.GONE
        } else {
            holder.chatButton.visibility = View.VISIBLE
            holder.llamarButton.visibility = View.VISIBLE

            holder.chatButton.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("receptorId", vivienda.creadorId)
                context.startActivity(intent)
            }

            holder.llamarButton.setOnClickListener {
                val context = holder.itemView.context
                val db = FirebaseDatabase.getInstance().getReference("usuarios").child(vivienda.creadorId)

                db.child("telefono").addListenerForSingleValueEvent(object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val telefono = snapshot.getValue(String::class.java)
                        if (!telefono.isNullOrBlank()) {
                            val intent = Intent(Intent.ACTION_CALL).apply {
                                data = Uri.parse("tel:$telefono")
                            }

                             if (context.checkSelfPermission(android.Manifest.permission.CALL_PHONE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Permiso CALL_PHONE no concedido", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Teléfono no disponible", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalleViviendaActivity::class.java)
            intent.putExtra("tipo", vivienda.tipo)
            intent.putExtra("ubicacion", vivienda.ubicación)
            intent.putExtra("descripcion", vivienda.descripción)
            intent.putExtra("habitaciones", vivienda.habitaciones)
            intent.putExtra("baños", vivienda.baños)
            intent.putExtra("superficie", vivienda.superficie_m2)
            intent.putExtra("plantas", vivienda.plantas)
            intent.putExtra("precio", vivienda.precio)
            intent.putExtra("año_construccion", vivienda.año_construcción)
            intent.putExtra("estado", vivienda.estado)
            intent.putExtra("certificacion", vivienda.certificación_energética)
            intent.putExtra("extras", ArrayList(vivienda.extras))
            intent.putExtra("imagenes", ArrayList(vivienda.imágenes)) // 🔥 IMPORTANTE
            context.startActivity(intent)
        }

        favRef.child(viviendaId).get().addOnSuccessListener {
            val esFavorita = it.exists()
            holder.btnFavorito.setImageResource(
                if (esFavorita) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
            )

            holder.btnFavorito.setOnClickListener {
                if (esFavorita) {
                    favRef.child(viviendaId).removeValue()
                    holder.btnFavorito.setImageResource(android.R.drawable.btn_star_big_off)
                } else {
                    favRef.child(viviendaId).setValue(true)
                    holder.btnFavorito.setImageResource(android.R.drawable.btn_star_big_on)
                }
            }
        }
    }

    override fun getItemCount(): Int = lista.size
}
