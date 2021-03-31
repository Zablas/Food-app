package com.cheuwo.app

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.AWSCognitoIdentityProvider
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cheuwo.app.utils.Validators
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.inputEmail
import kotlinx.android.synthetic.main.activity_register.inputPassword
import kotlinx.android.synthetic.main.activity_reset.*
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class RegisterActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Makes the upper system tray disappear
        inputDate.keyListener = null
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
     * Registers a new user
     */
    fun switchToPhotoForm(view: View)
    {
        // Extract data
        val firstName = inputFirstName.text.toString()
        val lastName = inputLastName.text.toString()
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()
        val date = inputDate.text.toString()

        // Check validity
        var isValid = true
        if(firstName.isEmpty())
        {
            inputFirstName.error = "Enter your first name"
            isValid = false
        }
        if(lastName.isEmpty())
        {
            inputLastName.error = "Enter your last name"
            isValid = false
        }
        if(email.isEmpty() || !Validators.isEmailValid(email))
        {
            inputEmail.error = "Enter a valid e-mail"
            isValid = false
        }
        if(!Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$", password) // Check for lowercase, uppercase and numbers
            || password.length < 8)
        {
            inputPassword.error = "Password must:\nContain an uppercase letter,\nContain a lowercase letter,\nContain a number\nMust be at least 8 characters long"
            isValid = false
        }
        if(date.isEmpty())
        {
            inputDate.error = "Enter a valid birth date"
            isValid = false
        }

        if(isValid)
        {
            // Prepare the date to a format of at least 10 characters
            val original: DateFormat = SimpleDateFormat("d/M/yyyy", Locale.ENGLISH)
            val target: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            val birthdate: Date = original.parse(date)
            val formattedDate: String = target.format(birthdate)

            // Add the attributes
            val attributes: ArrayList<AuthUserAttribute> = ArrayList()
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.email(), email))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.birthdate(), formattedDate))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.name(), firstName))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.familyName(), lastName))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.custom("custom:notifications"), "true"))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.custom("custom:radius"), "20"))

            // Begin the registration
            Amplify.Auth.signUp(email, password, AuthSignUpOptions.builder().userAttributes(attributes).build(),
                { result ->
                    if(result.isSignUpComplete)
                    {
                        val intent = Intent(this, ConfirmationActivity::class.java)
                        val user = User(email, firstName, lastName, null)
                        intent.putExtra("user", user) // Saving the user for other interactions in the future
                        intent.putExtra("password", password)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    }
                },
                { error ->
                    println(error.message)
                    println(error.localizedMessage)
                    println(error.cause)
                    println(error.recoverySuggestion)
                })
        }
    }

    /**
     * Shows a calendar for GUI input
     */
    fun showDatePicker(view: View)
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            inputDate.setText("$dayOfMonth/$month/$year")
        }, year, month, day)
        dialog.show()
    }
}
