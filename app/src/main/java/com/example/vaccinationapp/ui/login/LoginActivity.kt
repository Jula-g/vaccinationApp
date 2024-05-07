package com.example.vaccinationapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.vaccinationapp.R
import com.example.vaccinationapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val buttonStart = findViewById<Button>(R.id.startButton)
        val registerButton = findViewById<TextView>(R.id.RegisterButton)
        val inputPassword = findViewById<EditText>(R.id.userPassword)
        val inputMail = findViewById<EditText>(R.id.userEmail)

        auth = Firebase.auth

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        buttonStart.setOnClickListener {
            val email = inputMail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            userVerification(email, password)

        }
    }

    private fun userVerification(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { signInTask ->
                if (signInTask.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()
                    intent.putExtra("email", email)
                    startActivity(intent)
                    this.finish()
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed. Check your email and password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
