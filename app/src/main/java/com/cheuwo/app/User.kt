package com.cheuwo.app

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize // Required in order to transfer data between fragments
data class User(var email: String? = null,
                var firstName: String? = null,
                var lastName: String? = null,
                var photo: String? = null) : Parcelable
