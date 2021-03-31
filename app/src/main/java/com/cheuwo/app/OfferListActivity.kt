package com.cheuwo.app

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.OfferGQL
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File

class OfferListActivity : AppCompatActivity()
{
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_list)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelected) // Bind the logic to the object

        user = intent.getParcelableExtra("user") as User?
        if(user?.lastName == null) setupMissingAttributes() // In a conditional to prevent the download of the old photo TODO: Change after S3 photo cleanup is setup
        val offersFragment = OffersFragment() // Initial fragment to set
        offersFragment.arguments = createBundle()
        supportFragmentManager.beginTransaction().replace(R.id.container, offersFragment, offersFragment.javaClass.simpleName).commit() // Initial scene
    }

    /**
     * Nav bar view switch logic
     */
    private val mOnNavigationItemSelected = BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId)
        {
            R.id.navProfile ->
            {
                val profileFragment = ProfileFragment()
                profileFragment.arguments = createBundle() // Transfer the user data to the profile fragment
                supportFragmentManager.beginTransaction().replace(R.id.container, profileFragment, profileFragment.javaClass.simpleName).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navSettings ->
            {
                val settingsFragment = SettingsFragment()
                settingsFragment.arguments = createBundle()
                supportFragmentManager.beginTransaction().replace(R.id.container, settingsFragment, settingsFragment.javaClass.simpleName).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navOffers ->
            {
                val offersFragment = OffersFragment()
                offersFragment.arguments = createBundle()
                supportFragmentManager.beginTransaction().replace(R.id.container, offersFragment, offersFragment.javaClass.simpleName).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    /**
     * Creates the bundle that is to be assigned to fragment arguments
     */
    private fun createBundle(): Bundle
    {
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        return bundle
    }

    /**
     * Fills out missing user attributes if there are any (text fields, photos, etc.)
     */
    private fun setupMissingAttributes()
    {
        // Text fields
        if(user?.firstName == null) user?.firstName = AWSMobileClient.getInstance().userAttributes["name"]
        if(user?.lastName == null) user?.lastName = AWSMobileClient.getInstance().userAttributes["family_name"]

        // Photo
        if(user?.photo == null)
        {
            Amplify.Storage.downloadFile(
                "pfp/" + user?.email.toString(),
                File(applicationContext?.filesDir.toString() + File.separator + "pfp.png"),
                { result ->
                    user?.photo = result.file.path
                },
                { error ->
                    println(error.message)
                    println(error.localizedMessage)
                    println(error.cause)
                    println(error.recoverySuggestion)
                }
            )
        }
    }
}