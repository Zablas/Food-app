package com.cheuwo.app.utils

class Validators
{
    companion object
    {
        /**
         * Validates the e-mail
         */
        fun isEmailValid(email: String): Boolean =
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}