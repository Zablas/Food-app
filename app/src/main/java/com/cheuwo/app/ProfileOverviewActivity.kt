package com.cheuwo.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.amplifyframework.core.Amplify
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_overview.*

class ProfileOverviewActivity : AppCompatActivity()
{
    private var user: User? = null
    private var otherUser: User? = null // The user whose profile is being watched
    private var offer: Offer? = null // Used when returning back to the overview

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_overview)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear

        user = intent.getParcelableExtra("user") as User?
        otherUser = intent.getParcelableExtra("otherUser") as User?
        offer = intent.getParcelableExtra("offer") as Offer?

        // Fill out the front-end
        valueProfileNamePO.text = "${otherUser?.firstName} ${otherUser?.lastName}"
        Amplify.Storage.getUrl("pfp/" + otherUser?.email,
            { result ->
                println("COCK: " + result.url.toString())
                runOnUiThread { Picasso.get().load(result.url.toString()).placeholder(R.drawable.ic_launcher_foreground).into(profilePicturePO) }
            },
            { error ->
                println(error.message)
                println(error.localizedMessage)
                println(error.cause)
                println(error.recoverySuggestion)
            })
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        val intent = Intent(this, OfferDetailActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("offer", offer)
        startActivity(intent)
        finish()
    }

    /**
     * Switches back to the offer list
     */
    fun switchBack(view: View) = onBackPressed()
}