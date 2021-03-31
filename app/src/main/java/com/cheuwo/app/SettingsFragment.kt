package com.cheuwo.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.amazonaws.mobile.client.AWSMobileClient
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File

class SettingsFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        val userData = arguments?.getParcelable<User>("user")

        // Slider
        distanceSlider.progress = CheuwoSettings.radius
        distanceText.text = "${CheuwoSettings.radius} km"
        distanceSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                distanceText.text = "$progress km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)
            {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?)
            {
                CheuwoSettings.radius = seekBar!!.progress
                Thread {
                    val newAttributes = mapOf(Pair(CheuwoConstants.ATTRIBUTE_RADIUS, seekBar.progress.toString()))
                    AWSMobileClient.getInstance().updateUserAttributes(newAttributes)
                }.start()
            }
        })

        // Logout button
        btnLogout.setOnClickListener {
            Amplify.Auth.signOut(AuthSignOutOptions.builder().globalSignOut(true).build(), {
                if(userData?.photo != null) // Cleaning up
                {
                    val file = File(userData?.photo)
                    if(file.exists()) file.delete()
                }
                val intent = Intent(activity, MainActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            }, {})
        }

        // Switch
        switchNotifications.isChecked = CheuwoSettings.allowNotifications
        switchNotifications.setOnCheckedChangeListener { compoundButton, b ->
            CheuwoSettings.allowNotifications = switchNotifications.isChecked
            Thread{
                val newAttributes = mapOf(Pair("custom:notifications", switchNotifications.isChecked.toString()))
                AWSMobileClient.getInstance().updateUserAttributes(newAttributes)
            }.start()
        }
    }
}