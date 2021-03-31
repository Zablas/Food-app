package com.cheuwo.app

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

/**
 * Represents front-end logic of a single item in the RecyclerView
 */
class OfferViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_offer, parent, false))
{
    private var thumbnail: ImageView? = itemView.findViewById(R.id.offerListThumbnail)
    private var title: TextView? = itemView.findViewById(R.id.offerListTitle)
    private var subtitle: TextView? = itemView.findViewById(R.id.offerListSubtitle)
    private var detail: TextView? = itemView.findViewById(R.id.offerListDetail)

    fun bind(offer: Offer)
    {
        title?.text = offer.name
        subtitle?.text = offer.description
        detail?.text = offer.address
        Picasso.get().load(offer.imageUrl).placeholder(R.drawable.ic_launcher_foreground).into(thumbnail)
    }
}