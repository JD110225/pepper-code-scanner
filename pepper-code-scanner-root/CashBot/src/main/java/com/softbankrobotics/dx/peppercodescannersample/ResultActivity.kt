package com.softbankrobotics.dx.peppercodescannersample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.barcode.Barcode
import com.softbankrobotics.dx.peppercodescanner.BarcodeReaderActivity
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_result.view.*

class ResultActivity : AppCompatActivity() {

    companion object {
        private const val KEY_MESSAGE = "key_message"
        private const val RESTART_TIME = 10000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val message = intent.getStringExtra(KEY_MESSAGE)
        textViewResult.text = message
        resultLayout.backButton.setOnClickListener{
            val launchIntent = Intent(this, MainActivity::class.java)
            startActivity(launchIntent)
        }
        resultLayout.confirmPayButton.setOnClickListener{
            val launchIntent = Intent(this, BarcodeReaderActivity::class.java)
            startActivity(launchIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()

        Handler().postDelayed({
            finish()
        }, RESTART_TIME)
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
