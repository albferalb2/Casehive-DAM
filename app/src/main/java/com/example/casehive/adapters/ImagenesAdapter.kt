package com.example.casehive.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casehive.R

class ImagenesAdapter(private val imagenes: List<Uri>) :
    RecyclerView.Adapter<ImagenesAdapter.ImagenViewHolder>() {

    inner class ImagenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.itemImagen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imagen_miniatura, parent, false)
        return ImagenViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imagenes[position])
            .centerCrop()
            .into(holder.img)
    }

    override fun getItemCount(): Int = imagenes.size
}
