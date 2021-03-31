package com.cheuwo.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import kotlinx.android.synthetic.main.activity_confirmation.*

class ConfirmationActivity : AppCompatActivity()
{
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear

        user = intent.getParcelableExtra("user") as User? // Saving values for using them in the later profile fragment
        textViewEmail.text = user?.email
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
     * Confirms the registration and switches to the offer list
     */
    fun switchToOfferList(view: View)
    {
        Amplify.Auth.confirmSignUp(user?.email.toString(), inputConfirmationCode.text.toString(), // Confirming the user
            { result ->
                if(result.isSignUpComplete)
                {
                    Amplify.Auth.signIn(user?.email.toString(), intent.getStringExtra("password"), // Auto sign-in after a successful confirmation
                        { signinResult ->
                            if(signinResult.isSignInComplete)
                            {
                                val intent = Intent(this, RegistrationPhotoActivity::class.java)
                                intent.putExtra("user", user)
                                startActivity(intent)
                                finish()
                            }
                        },
                        {
                            runOnUiThread {
                                Toast.makeText(this, "User confirmed but failed to login. Check your password and try again.", Toast.LENGTH_LONG).show()
                            } // Only works on UI thread
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        })
                }
            },
            {
                runOnUiThread { inputConfirmationCode.error = "Invalid confirmation code" } // Only works on UI thread
            })
    }

    /**
     * Resends the activation code in case that is requested
     */
    fun resendActivationCode(view: View)
    {
        Amplify.Auth.resendSignUpCode(user?.email.toString(),
            { result ->
                if(result.isSignUpComplete)
                {
                    runOnUiThread { Toast.makeText(this, "Activation code has been sent", Toast.LENGTH_LONG).show() }
                }
            }, {})
    }
}