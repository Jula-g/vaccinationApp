package com.example.vaccinationapp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.DB.queries.UsersQueries
import com.example.vaccinationapp.R
import com.example.vaccinationapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.example.vaccinationapp.ui.login.LoginActivity


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setHeader()
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_calendar, R.id.nav_history, R.id.nav_manage_records, R.id.nav_reminders
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setHeader() {
        email = intent.getStringExtra("email").toString()
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val emailText = headerView.findViewById<TextView>(R.id.emailView)
        emailText.text = email
        val nameText = headerView.findViewById<TextView>(R.id.nameView)
        runBlocking { launch(Dispatchers.IO) { nameText.text = getUserNameAndSurname() } }

    }

    private suspend fun getUserNameAndSurname(): CharSequence {
        email = intent.getStringExtra("email").toString()
        return withContext(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val userQueries = UsersQueries(connection)
            val user = userQueries.getUserByEmail(email)
            connection.close()
            user?.firstName + " " + user?.lastName
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.delete_account -> {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes") { _: DialogInterface, i: Int ->
                        runBlocking {
                            launch(Dispatchers.IO) {
                                val connection = DBconnection.getConnection()
                                val userQueries = UsersQueries(connection)
                                val user = userQueries.getUserByEmail(email)
                                userQueries.deleteUserByEmail(user?.email.toString())
                                connection.close()
                            }
                        }
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        this.finish()
                    }
                    .setNegativeButton("Cancel") { _: DialogInterface, i: Int ->

                    }
                    .setCancelable(false)
                    .show()
                true
            } R.id.log_out -> {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        this.finish()
                    }
                    .setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                    }
                    .setCancelable(false)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }
}