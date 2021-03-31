package com.cheuwo.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.cheuwo.app.utils.Validators
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) // Handle the OAuth
            Amplify.Auth.handleWebUISignInResponse(data)
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
     * Logs the user into the application
     */
    fun confirmLogin(view: View)
    {
        var isValid = true
        if(inputEmail.text?.isEmpty()!! || !Validators.isEmailValid(inputEmail.text.toString()))
        {
            inputEmailLayout.error = "Enter an email."
            isValid = false
        }
        if(inputPassword.text?.isEmpty()!!)
        {
            inputPasswordLayout.error = "Enter a password."
            isValid = false
        }
        if(isValid)
        {
            Amplify.Auth.signIn(inputEmail.text.toString(), inputPassword.text.toString(),
                { result ->
                    if(result.isSignInComplete)
                    {
                        val intent = Intent(this, OfferListActivity::class.java)
                        val firstName = AWSMobileClient.getInstance().userAttributes["name"] // Using AWSMobileClient as Amplify currently doesn't support user attribute getting
                        val lastName = AWSMobileClient.getInstance().userAttributes["family_name"]
                        CheuwoSettings.allowNotifications = AWSMobileClient.getInstance().userAttributes[CheuwoConstants.ATTRIBUTE_NOTIFICATIONS]!!.toBoolean()
                        CheuwoSettings.radius = AWSMobileClient.getInstance().userAttributes[CheuwoConstants.ATTRIBUTE_RADIUS]!!.toInt()
                        val user = User(inputEmail.text.toString(), firstName, lastName, null)
                        intent.putExtra("user", user)
                        startActivity(intent)
                        finish()
                    }
                },
                { error ->
                    // Currently checking the error string as I am unable to cast the Exception to the Java version of AWS exceptions
                    // TODO: Change this logic to handle errors differently and do confirmation only when the correct password is entered
                    println(error.message)
                    println(error.localizedMessage)
                    println(error.cause)
                    println(error.recoverySuggestion)
                    val matcher = Pattern.compile("Exception: (.*?) \\(Service:", Pattern.DOTALL)
                        .matcher(error.cause.toString())
                    matcher.find()
                    val errStr = matcher.group(1)
                    when (errStr)
                    {
                        "User does not exist.", "Incorrect username or password." ->
                        {
                            runOnUiThread { // Throws an exception without this
                                inputEmailLayout.error = "Incorrect username or password."
                                inputPasswordLayout.error = ""
                            }
                        }
                        "User is not confirmed." ->
                        {
                            val intent = Intent(this, ConfirmationActivity::class.java)
                            val user = User(inputEmail.text.toString(), null, null, null)
                            intent.putExtra("user", user)
                            intent.putExtra("password", inputPassword.text.toString())
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish()
                        }
                    }
                })
        }
    }

    /**
     * Switches the activity password recovery
     */
    fun switchToRecovery(view: View)
    {
        val intent = Intent(this, RecoveryActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    /**
     * Logs the user in using Facebook
     */
    fun facebookLogin(view: View)
    {
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.facebook(), this,
            { result ->
                if(result.isSignInComplete) transferOauthUser()
            },
            { error ->
                println(error.message)
                println(error.localizedMessage)
                println(error.cause)
                println(error.recoverySuggestion)
            })
    }

    /**
     * Logs the user in using Google
     */
    fun googleLogin(view: View)
    {
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), this,
            { result ->
                if(result.isSignInComplete) transferOauthUser()
            },
            { error ->
                println(error.message)
                println(error.localizedMessage)
                println(error.cause)
                println(error.recoverySuggestion)
            })
    }

    /**
     * Performs remaining login steps after using OAuth
     */
    private fun transferOauthUser()
    {
        val attributeToCheck = AWSMobileClient.getInstance().userAttributes[CheuwoConstants.ATTRIBUTE_RADIUS]
        if(attributeToCheck == null) // Check if the OAuth user is new or not
        {
            val newAttributes = mapOf(Pair(CheuwoConstants.ATTRIBUTE_RADIUS, "20"),
                                        Pair(CheuwoConstants.ATTRIBUTE_NOTIFICATIONS, "true"))
            AWSMobileClient.getInstance().updateUserAttributes(newAttributes)
        }
        val intent = Intent(this, OfferListActivity::class.java)
        startActivity(intent)
        finish()
    }
}