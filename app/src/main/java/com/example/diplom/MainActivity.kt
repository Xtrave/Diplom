package com.example.diplom

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CompanyListFragment())
                .commit()
        }


        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        val rootView = findViewById<View>(R.id.fragment_container)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())

            view.updatePadding(top = cutout.top)

            insets
        }
    }

}
