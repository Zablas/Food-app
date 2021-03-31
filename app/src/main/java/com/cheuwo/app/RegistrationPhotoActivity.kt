package com.cheuwo.app

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageUploadFileOptions
import kotlinx.android.synthetic.main.activity_registration_photo.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class RegistrationPhotoActivity : AppCompatActivity()
{
    private var user: User? = null
    private var photoBitmap: Bitmap? = null
    private var shouldDelete: Boolean = false
    private var pfpPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_photo)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear

        user = intent.getParcelableExtra("user") as User? // Saving values for using them in the later profile fragment
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
     * Goes back to the registration form
     */
    fun switchBack(view: View) = onBackPressed()

    /**
     * Switches to offer list
     */
    fun confirmPhoto(view: View)
    {
        val intent = Intent(this, OfferListActivity::class.java)
        user?.photo = pfpPath
        intent.putExtra("user", user)
        if(photoBitmap != null) uploadBitmapToCloud()
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    /**
     * Logic behind setting a selected image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
            0 ->
            {
                if(resultCode == Activity.RESULT_OK)
                {
                    shouldDelete = true
                    photoBitmap = data?.extras?.get("data") as Bitmap
                    imageUserPhoto.setImageBitmap(photoBitmap)
                }
            }
            1 ->
            {
                if(resultCode == Activity.RESULT_OK)
                {
                    shouldDelete = false
                    photoBitmap = MediaStore.Images.Media.getBitmap(contentResolver, data?.data)
                    imageUserPhoto.setImageURI(data?.data)
                }
            }
        }
    }

    /**
     * Gets a photo for the offer from the user's camera
     */
    fun getPhotoFromCamera(view: View)
    {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, 0)
    }

    /**
     * Gets a photo for the offer from the user's gallery
     */
    fun getPhotoFromGallery(view: View)
    {
        val takePicture = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(takePicture, 1)
    }

    /**
     * Uploads the photo to cloud storage (converting it from a bitmap)
     */
    private fun uploadBitmapToCloud()
    {
        // Prepare the byte stream for upload
        val file = File(getExternalFilesDir(null).toString() + File.separator + user?.email + ".png")
        file.createNewFile()
        val byteStream = ByteArrayOutputStream()
        photoBitmap?.compress(Bitmap.CompressFormat.PNG, 0, byteStream)
        val bitmapData = byteStream.toByteArray()

        // Write to a temporary file
        val fileStream = FileOutputStream(file)
        fileStream.write(bitmapData)
        fileStream.flush()
        fileStream.close()

        // Upload to cloud
        Amplify.Storage.uploadFile("pfp/" + user?.email.toString(), file,
            {
                //if(shouldDelete) file.delete() // Delete temporary file
                pfpPath = file.absolutePath
                println(pfpPath)
            },
            { error ->
                println(error.message)
                println(error.localizedMessage)
                println(error.cause)
                println(error.recoverySuggestion)
            })
    }
}