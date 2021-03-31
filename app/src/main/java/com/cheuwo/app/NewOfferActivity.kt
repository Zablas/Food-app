package com.cheuwo.app

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.ModelSchema
import com.amplifyframework.datastore.generated.model.OfferGQL
import kotlinx.android.synthetic.main.activity_new_offer.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern

class NewOfferActivity : AppCompatActivity()
{
    private var user: User? = null
    private var photoBitmap: Bitmap? = null
    private var shouldDelete: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_offer)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear

        user = intent.getParcelableExtra("user") as User? // Saving values for using them in the later profile fragment
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
                    foodPhotoToAdd.setImageBitmap(photoBitmap)
                }
            }
            1 ->
            {
                if(resultCode == Activity.RESULT_OK)
                {
                    shouldDelete = false
                    photoBitmap = MediaStore.Images.Media.getBitmap(contentResolver, data?.data)
                    foodPhotoToAdd.setImageURI(data?.data)
                }
            }
        }
    }

    /**
     * Switches back to the Offer List Activity
     */
    fun switchBack(view: View) = onBackPressed()

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
     * Sends the new offer to the back-end
     */
    fun addOffer(view: View)
    {
        // Save data
        val offerName = textFieldOfferName.text.toString()
        val offerDescription = textFieldOfferDescription.text.toString()
        val offerAddress = textFieldOfferAddress.text.toString()
        val offerPrice = textFieldOfferPrice.text.toString()

        // Check for validity
        var isValid = true
        if(offerName.isEmpty())
        {
            textFieldOfferNameLayout.error = "Field must not be empty"
            isValid = false
        }
        if(offerDescription.isEmpty())
        {
            textFieldOfferDescriptionLayout.error = "Field must not be empty"
            isValid = false
        }
        if(offerAddress.isEmpty())
        {
            textFieldOfferAddressLayout.error = "Field must not be empty"
            isValid = false
        }
        if(offerPrice.isEmpty())
        {
            textFieldOfferPriceLayout.error = "Field must not be empty"
            isValid = false
        }
        if(!Pattern.matches("^[0-9]+\\.[0-9][0-9]$", offerPrice))
        {
            textFieldOfferPriceLayout.error = "Example of a valid price: 5.99"
            isValid = false
        }

        // Create a new offer object
        if(isValid)
        {
            val newOffer = OfferGQL.builder()
                .name(offerName)
                .description(offerDescription)
                .address(offerAddress)
                .price(offerPrice.toFloat())
                .imageUrl("offer/" + user?.email + "/" + offerName)
                .ownerUsername(user?.email)
                .build()

            if(photoBitmap != null) uploadBitmapToCloud(newOffer) // Calls uploadOfferToDatabase() from within
            else uploadOfferToDatabase(newOffer)
        }
    }

    /**
     * Uploads the photo to cloud storage (converting it from a bitmap)
     */
    private fun uploadBitmapToCloud(offer: OfferGQL)
    {
        // Prepare the byte stream for upload
        val file = File(getExternalFilesDir(null).toString() + File.separator + offer.name + ".png")
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
        Amplify.Storage.uploadFile(offer.imageUrl, file,
            {
                uploadOfferToDatabase(offer)
                if(shouldDelete) file.delete() // Delete temporary file
            },
            { error ->
                println(error.message)
                println(error.localizedMessage)
                println(error.cause)
                println(error.recoverySuggestion)
            })
    }

    /**
     * Uploads the new offer to DynamoDB
     */
    private fun uploadOfferToDatabase(offer: OfferGQL)
    {
        Amplify.API.mutate(ModelMutation.create(offer),
            {
                runOnUiThread {
                    Toast.makeText(this, "Offer added successfully", Toast.LENGTH_LONG).show()
                }
                val intent = Intent(this, OfferListActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
                finish()
            },
            { error ->
                println(error.message)
                println(error.localizedMessage)
                println(error.cause)
                println(error.recoverySuggestion)
            })
    }
}