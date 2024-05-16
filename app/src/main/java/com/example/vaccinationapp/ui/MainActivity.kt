package com.example.vaccinationapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
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
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.vaccinationapp.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

import com.example.vaccinationapp.ui.managerecords.ManageRecordsFragment


/**
 * MainActivity class is the main activity of the application that displays the main menu and the user's information.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var email: String

    /**
     * onCreate method is called when the activity is starting.
     * @param savedInstanceState The Bundle that contains the data that was most recently supplied in onSaveInstanceState(Bundle)
     */
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setHeader()
        setSupportActionBar(binding.appBarMain.toolbar)

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

        val value = intent.getIntExtra("value", -1)

        when (value){
            1 -> openManageRecordsFragment()
        }

    }

    private fun openManageRecordsFragment() {
        val manageRecordsFragment = ManageRecordsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, manageRecordsFragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * onCreateOptionsMenu method is called to create the options menu.
     * @param menu The options menu in which you place your items
     * @return true if the menu is displayed, false otherwise
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /**
     * onSupportNavigateUp method is called when the user presses the Up button in the action bar.
     * @return true if Up navigation completed successfully and this Activity was finished, false otherwise
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * setHeader method is called to set the header of the navigation view.
     */
    private fun setHeader() {
        email = intent.getStringExtra("email").toString()
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val emailText = headerView.findViewById<TextView>(R.id.emailView)
        emailText.text = email
        val nameText = headerView.findViewById<TextView>(R.id.nameView)
        runBlocking { launch(Dispatchers.IO) { nameText.text = getUserNameAndSurname() } }

    }

    /**
     * getUserNameAndSurname method is called to get the user's name and surname.
     * @return The user's name and surname
     */
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

    /**
     * onOptionsItemSelected method is called when an item in the options menu is selected.
     * @param menuItem The selected item
     * @return true if the item was successfully handled, false otherwise
     */
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
            }

            R.id.log_out -> {

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

//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission(),
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//
//        }
//    }
//
//    private fun askNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            val permission = Manifest.permission.POST_NOTIFICATIONS
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                if (shouldShowRequestPermissionRationale(permission)) {
//                    AlertDialog.Builder(this)
//                        .setTitle("Notification Permission")
//                        .setMessage("Granting notification permission allows you to receive important updates.")
//                        .setPositiveButton("OK") { _, _ ->
//                            requestPermissionLauncher.launch(permission)
//                        }
//                        .setNegativeButton("No thanks") { _, _ ->
//                        }
//                        .show()
//                } else {
//                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                }
//            }
//        }
//    }
}