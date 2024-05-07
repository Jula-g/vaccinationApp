//package com.example.vaccinationapp.ui.cancel
//
//import android.R
//import android.os.Bundle
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.LinearLayout
//import android.widget.PopupWindow
//import androidx.appcompat.app.AppCompatActivity
//
//
//class cancelFragment : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }
//
//    fun onButtonShowPopupWindowClick(view: View?) {
//
//        // inflate the layout of the popup window
//        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val popupView: View = inflater.inflate(R.layout.popup_window, null)
//
//        // create the popup window
//        val width = LinearLayout.LayoutParams.WRAP_CONTENT
//        val height = LinearLayout.LayoutParams.WRAP_CONTENT
//        val focusable = true // lets taps outside the popup also dismiss it
//        val popupWindow = PopupWindow(popupView, width, height, focusable)
//
//        // show the popup window
//        // which view you pass in doesn't matter, it is only used for the window tolken
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
//
//        // dismiss the popup window when touched
//        popupView.setOnTouchListener { v, event ->
//            popupWindow.dismiss()
//            true
//        }
//    }
//}