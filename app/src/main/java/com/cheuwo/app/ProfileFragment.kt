package com.cheuwo.app

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amplifyframework.core.Amplify
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File

class ProfileFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        val userData = arguments?.getParcelable<User>("user")
        valueProfileName.text = "${userData?.firstName} ${userData?.lastName}"
        valueEmail?.text = userData?.email

        // Setup the photo
        if(userData?.photo == null)
        {
            Amplify.Storage.downloadFile(
                "pfp/" + userData?.email.toString(),
                File(activity?.applicationContext?.filesDir.toString() + File.separator + "pfp.png"),
                { result ->
                    userData?.photo = result.file.path
                    val bitmap = BitmapFactory.decodeFile(result.file.path)
                    profilePicture.setImageBitmap(bitmap)
                },
                { error ->
                    println(error.message)
                    println(error.localizedMessage)
                    println(error.cause)
                    println(error.recoverySuggestion)
                }
            )
        }
        else
        {
            println(userData.photo)
            val bitmap = BitmapFactory.decodeFile(userData.photo)
            profilePicture.setImageBitmap(bitmap)
        }
    }
}