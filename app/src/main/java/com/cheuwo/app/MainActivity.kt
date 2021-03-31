package com.cheuwo.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException
import com.amazonaws.services.cognitoidentityprovider.model.UserNotFoundException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import java.lang.NullPointerException

class MainActivity : AppCompatActivity()
{
    companion object // Only initialized once
    {
        var arePluginsInitialized = false
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear

        // All the needed AWS plugins go here
        if(!arePluginsInitialized)
        {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.addPlugin(AWSApiPlugin())
            val config = AmplifyConfiguration.builder(applicationContext).devMenuEnabled(false).build()
            Amplify.configure(config, applicationContext)
            arePluginsInitialized = true
        }

        // Auto-login
        Amplify.Auth.fetchAuthSession(
            {result ->
                if(result.isSignedIn)
                {
                    try
                    {
                        val firstName = AWSMobileClient.getInstance().userAttributes["name"] // Using AWSMobileClient as Amplify currently doesn't support user attribute getting
                        val lastName = AWSMobileClient.getInstance().userAttributes["family_name"]
                        CheuwoSettings.allowNotifications = AWSMobileClient.getInstance().userAttributes[CheuwoConstants.ATTRIBUTE_NOTIFICATIONS]!!.toBoolean()
                        CheuwoSettings.radius = AWSMobileClient.getInstance().userAttributes[CheuwoConstants.ATTRIBUTE_RADIUS]!!.toInt()
                        val user = User(Amplify.Auth.currentUser.username, firstName, lastName, null)
                        val intent = Intent(this, OfferListActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                        finish()
                    }
                    catch (e: UserNotFoundException){}
                    catch (e: NotAuthorizedException)
                    {
                        Amplify.Auth.signOut({}, {})
                    }
                    catch (e: NullPointerException) // TODO: Remove after OAuth is fully finished
                    {
                        Amplify.Auth.signOut({}, {})
                    }
                    catch(e: Exception)
                    {
                        Amplify.Auth.signOut({}, {})
                    }
                }
            }, {}
        )
    }

    /**
     * Switches from the intro screen to the login screen
     */
    fun switchToLogin(view: View)
    {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    /**
     * Switches from the intro screen to the register screen
     */
    fun switchToRegister(view: View)
    {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}
