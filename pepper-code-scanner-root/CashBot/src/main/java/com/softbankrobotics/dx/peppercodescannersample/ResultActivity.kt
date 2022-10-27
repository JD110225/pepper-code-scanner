package com.softbankrobotics.dx.peppercodescannersample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.barcode.Barcode
import com.softbankrobotics.dx.peppercodescanner.BarcodeReaderActivity
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_result.view.*

class ResultActivity : AppCompatActivity() {

    companion object {
        private var totalPagar = ""
        private const val KEY_MESSAGE = "key_message"
        private const val TAG = "ResultActivity"
        private const val RESTART_TIME = 10000L
        private const val BARCODE_READER_ACTIVITY_REQUEST = 1208
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val message = intent.getStringExtra(KEY_MESSAGE)
        totalPagar=message
       // val price = intent.getStringExtra(TOTAL_PRICE)
       // Log.d("Precio desde result",price)
        textViewResult.text = "Tu precio a pagar es: "+message
        resultLayout.backButton.setOnClickListener{
            val launchIntent = Intent(this, MainActivity::class.java)
            startActivity(launchIntent)
        }
        resultLayout.confirmPayButton.setOnClickListener{
            val launchIntent = Intent(this, BarcodeReaderActivity::class.java)
            startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST)
        }
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()

        Handler().postDelayed({
            finish()
        }, RESTART_TIME)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "Scan error")
            return
        }

        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
            val barcode: Barcode? =
                data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE)
            var scannedPrice= barcode?.rawValue
            Log.d("Precio escaneado",scannedPrice)
            var vuelto = scannedPrice-(totalPagar.toInt())
            Log.d("Vuelto a pagar: "+ vuelto))
            val launchIntent = Intent(this, TransactionActivity::class.java)
            launchIntent.putExtra(KEY_MESSAGE, scannedPrice)
            startActivity(launchIntent)
        }
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
