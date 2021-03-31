package com.cheuwo.app

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize // Required in order to transfer data between fragments
data class Offer(val name: String,
                 val description: String,
                 val address: String,
                 val price: Float,
                 val ownerUsername: String,
                 val imageUrl: String? = null) : Parcelable
