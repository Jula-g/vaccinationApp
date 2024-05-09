package com.example.vaccinationapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.DB.entities.Users
import com.example.vaccinationapp.DB.queries.UsersQueries
import com.example.vaccinationapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Activity for the register screen.
 */
class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var lastNameInput: EditText

    /**
     * Creates the view for the register screen.
     * @param savedInstanceState The saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailInput = findViewById(R.id.emailInput)
        nameInput = findViewById(R.id.NameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        val loginButton = findViewById<TextView>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.SignUpButton)

        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val repeatedPasswordInput = findViewById<EditText>(R.id.repeatPasswordInput)

        auth = Firebase.auth

        // Login button clicked action
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Sign up button clicked action
        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val name = nameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val repeatedPassword = repeatedPasswordInput.text.toString().trim()

            if (verifyData(email, name, lastName, password, repeatedPassword)) {
                createUser(email, password)
            }
        }
    }

    /**
     * Verifies the data entered by the user.
     * @param email The user's email.
     * @param name The user's name.
     * @param lastName The user's last name.
     * @param password The user's password.
     * @param repeatedPassword The user's repeated password.
     * @return True if the data is correct, false otherwise.
     */
    private fun verifyData(
        email: String, name: String, lastName: String, password: String, repeatedPassword: String
    ): Boolean {
        var result = true
        if (email.isEmpty() || name.isEmpty() || lastName.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            result = false
        }
        if (password != repeatedPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            result = false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT)
                .show()
            result = false
        }
        if (!email.contains("@")) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
            result = false
        }
        var hasDigit = false
        var hasLetter = false
        var hasSpecialChar = false
        val specialChars =
            charArrayOf('-', '_', '.', '@', '!', '#', '$', '%', '^', '&', '*', '(', ')')

        for (char in password) {
            if (specialChars.contains(char)) hasSpecialChar = true
            if (char.isDigit()) hasDigit = true
            if (char.isLetter()) hasLetter = true
        }
        if (!hasDigit || !hasLetter) {
            Toast.makeText(
                this,
                "Password must contain at least one letter and one digit",
                Toast.LENGTH_SHORT
            ).show()
            result = false
        }
        if (!hasSpecialChar) {
            Toast.makeText(
                this,
                "Password must contain at least one special character",
                Toast.LENGTH_SHORT
            ).show()
            result = false
        }
        return result
    }

    /**
     * Creates a user with the given email and password.
     * @param email The user's email.
     * @param password The user's password.
     */
    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    runBlocking {
                        launch(Dispatchers.IO) {
                            addUserToDatabase()
                        }
                    }

                    Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    this.finish()
                } else {
                    Toast.makeText(this, "User with this email already exists.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    /**
     * Adds the user to the database.
     */
    private suspend fun addUserToDatabase() {
        return withContext(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val user = Users(
                nameInput.text.toString(),
                lastNameInput.text.toString(),
                emailInput.text.toString()
            )
            val userQueries = UsersQueries(connection)
            userQueries.addUser(user)
        }
    }
}
