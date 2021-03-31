package com.cheuwo.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient
import com.amazonaws.services.cognitoidentityprovider.model.AdminGetUserRequest
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.OfferGQL
import kotlinx.android.synthetic.main.fragment_offers.*

class OffersFragment : Fragment(), LifecycleObserver
{
    private var offerList: ArrayList<Offer> = arrayListOf() // The ArrayList used for the ListView adapter
    private var user: User? = null
    private var savedView: View? = null // For the reopen event
    private var currentImageUrl: String = "" // For the for loop when retrieving image URLs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this) // Enables the onAppStop() and onAppStart() events
        return inflater.inflate(R.layout.fragment_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        savedView = view // For the reopen event
        user = arguments?.getParcelable("user")

        recyclerViewOffers.layoutManager = LinearLayoutManager(activity)
        populateListView(view)

        btnAddOffer.setOnClickListener { // Switches the activity to the new offer form
            val intent = Intent(it.context, NewOfferActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }

        pullToRefresh.setOnRefreshListener {
            populateListView(view)
        }
    }

    /**
     * Adds the offers to the ListView and updates it
     */
    private fun populateListView(view: View)
    {
        offerList.clear()
        Amplify.API.query(ModelQuery.list(OfferGQL::class.java),
            { result ->
                for(item in result.data)
                {
                    retrieveImageUrlFromCloud(item)
                    while (currentImageUrl.isEmpty()){} // We await the result of Amplify.Storage.getUrl()
                    val newOffer = Offer(item.name, item.description, item.address, item.price, item.ownerUsername, currentImageUrl)
                    offerList.add(newOffer)
                    currentImageUrl = "" // Reset for the next iteration
                }
                activity?.runOnUiThread {
                    pullToRefresh.isRefreshing = false
                    val adapter = OfferAdapterRV(offerList){
                        val intent = Intent(activity, OfferDetailActivity::class.java)
                        intent.putExtra("offer", it)
                        intent.putExtra("user", user)
                        activity?.startActivity(intent)
                    }
                    recyclerViewOffers.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            },
            { error ->
                println(error.message)
                println(error.localizedMessage)
                println(error.cause)
                println(error.recoverySuggestion)
            })
    }

    /**
     * Gets the image URL from the S3
     */
    private fun retrieveImageUrlFromCloud(offer: OfferGQL)
    {
        Amplify.Storage.getUrl(offer.imageUrl,
            { result ->
                currentImageUrl = result.url.toString()
            },
            { error ->
                println(error.message)
                println(error.localizedMessage)
                println(error.cause)
                println(error.recoverySuggestion)
                currentImageUrl = "null"
            })
    }

    /**
     * Is triggered when app is reopened
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppStart()
    {
        savedView?.let { populateListView(it) }
    }
}