package com.cheuwo.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.amplifyframework.core.Amplify
import kotlinx.android.synthetic.main.activity_recovery.*

class RecoveryActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    /**
     * Switches back to the intro screen
     */
    fun switchBack(view: View) = onBackPressed()

    /**
     * Sends the password to the specified e-mail
     */
    fun sendPassword(view: View)
    {
        Amplify.Auth.resetPassword(inputEmail.text.toString(),
            {
                val intent = Intent(this, ResetActivity::class.java)
                startActivity(intent)
                finish()
            },
            { error ->
                runOnUiThread { Toast.makeText(this, "Please check the entered the e-mail", Toast.LENGTH_SHORT).show() }
                println(error.message)
                println(error.localizedMessage)
                println(error.cause)
                println(error.recoverySuggestion)
            })
    }
}