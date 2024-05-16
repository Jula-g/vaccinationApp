package com.example.vaccinationapp.ui.history

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vaccinationapp.R

class EditRecord : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_record)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<AutoCompleteTextView>(R.id.vaccineNameAdd)
        val dose = findViewById<EditText>(R.id.doseNumberAdd)
        val date = findViewById<Button>(R.id.dateAdd)
        val cancel = findViewById<Button>(R.id.cancelAdd)
        val confirm = findViewById<Button>(R.id.confirmAdd)

    }
}