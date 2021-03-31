package com.cheuwo.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.amplifyframework.core.Amplify
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_reset.*
import java.util.regex.Pattern

class ResetActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    /**
     * Switches back to the intro screen
     */
    fun switchBack(view: View) = onBackPressed()

    /**
     * Confirms the password reset
     */
    fun confirmReset(view: View)
    {
        // Retrieve data
        val code = inputRecoveryCode.text.toString()
        val password = inputNewPassword.text.toString()

        // Check validity
        var isValid = true
        if(code.isEmpty())
        {
            inputRecoveryCode.error = "Field must not be empty"
            isValid = false
        }
        if(password.isEmpty())
        {
            inputNewPassword.error = "Field must not be empty"
            isValid = false
        }
        if(!Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$", password) // Check for lowercase, uppercase and numbers
            || password.length < 8)
        {
            inputNewPassword.error = "Password must:\nContain an uppercase letter,\nContain a lowercase letter,\nContain a number\nMust be at least 8 characters long"
            isValid = false
        }

        // Begin the reset
        if(isValid)
        {
            Amplify.Auth.confirmResetPassword(password, code,
                {
                    runOnUiThread { Toast.makeText(this, "Password reset successfully", Toast.LENGTH_LONG).show() }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                { error ->
                    runOnUiThread { inputRecoveryCode.error = "Invalid code" }
                    println(error.message)
                    println(error.localizedMessage)
                    println(error.cause)
                    println(error.recoverySuggestion)
                })
        }
    }
}