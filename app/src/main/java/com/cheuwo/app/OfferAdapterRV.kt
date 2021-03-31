package com.cheuwo.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Handles data representation in the RecyclerView
 */
class OfferAdapterRV(private val list: List<Offer>,
                     private val listener: (Offer) -> Unit) // Custom lambda based listener
    : RecyclerView.Adapter<OfferViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        return OfferViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int)
    {
        val offer = list[position]
        holder.bind(offer)
        holder.itemView.setOnClickListener { listener(offer) }
    }
}