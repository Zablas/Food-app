package com.cheuwo.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient
import com.amazonaws.services.cognitoidentityprovider.model.AdminGetUserRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_offer_detail.*

class OfferDetailActivity : AppCompatActivity()
{
    private var user: User? = null
    private var offer: Offer? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_detail)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear

        user = intent.getParcelableExtra("user") as User?
        offer = intent.getParcelableExtra("offer") as Offer?

        // Fill out the front-end
        valueName.text = offer?.name
        valueDescription.text = offer?.description
        valuePrice.text = offer?.price.toString()
        valueLabel.text = offer?.address
        Picasso.get().load(offer?.imageUrl).placeholder(R.drawable.ic_launcher_foreground).into(foodPhoto)
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        val intent = Intent(this, OfferListActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
        finish()
    }

    /**
     * Goes back to the offer list
     */
    fun switchBack(view: View) = onBackPressed()

    /**
     * Shows info about the person who created the offer
     */
    fun reviewProfile(view: View)
    {
        btnReviewProfile.isActivated = false // To prevent a spam of threads
        Thread{
            // Get the user
            val request = AdminGetUserRequest().withUserPoolId("eu-central-1_LxeP5ZTDD").withUsername(offer?.ownerUsername)
            val client = AmazonCognitoIdentityProviderClient(AWSMobileClient.getInstance().credentials)
            client.setRegion(Region.getRegion(Regions.EU_CENTRAL_1))
            val foundUser = client.adminGetUser(request)
            val otherUser = User()

            // Fill out the front-end attributes
            for(attribute in foundUser.userAttributes)
            {
                when(attribute.name)
                {
                    "email" -> otherUser.email = attribute.value
                    "name" -> otherUser.firstName = attribute.value
                    "family_name" -> otherUser.lastName = attribute.value
                }
            }

            // Start the activity
            val intent = Intent(this, ProfileOverviewActivity::class.java)
            intent.putExtra("user", user)
            intent.putExtra("otherUser", otherUser)
            intent.putExtra("offer", offer)
            startActivity(intent)
            finish()
        }.start()
    }
}