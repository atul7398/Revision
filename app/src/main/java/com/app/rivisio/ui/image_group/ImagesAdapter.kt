package com.app.rivisio.ui.image_group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.rivisio.R
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.model.Image
import javax.inject.Inject

class ImagesAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val images: MutableList<Image> = mutableListOf()

    interface Callback {
        fun onImageClick(image: Image)
    }

    private lateinit var callback: Callback

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun updateItems(images: List<Image>) {
        this.images.clear()
        this.images.addAll(images)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val dietViewHolder = ExerciseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        )

        dietViewHolder.itemView.findViewById<AppCompatImageView>(R.id.image_note)
            .setOnClickListener {
                val position = dietViewHolder.bindingAdapterPosition
                if (this::callback.isInitialized)
                    callback.onImageClick(images[position])
            }

        return dietViewHolder
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ExerciseViewHolder).onBind(images[position])
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(image: Image) {
            Glide
                .with(itemView.context)
                .load(image.path)
                .centerCrop()
                .into(itemView.findViewById<AppCompatImageView>(R.id.image_note))

        }
    }
}