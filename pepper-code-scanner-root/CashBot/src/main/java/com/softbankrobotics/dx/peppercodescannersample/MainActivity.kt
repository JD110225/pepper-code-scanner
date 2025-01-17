package com.softbankrobotics.dx.peppercodescannersample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.barcode.Barcode
import com.softbankrobotics.dx.peppercodescanner.BarcodeReaderActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    companion object {
        private var total=500
        private const val TAG = "MainActivity"
        private const val BARCODE_READER_ACTIVITY_REQUEST = 1208
        private const val KEY_MESSAGE = "key_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainLayout.contador.setText(""+total)
        mainLayout.scanButton.setOnClickListener {
            val launchIntent = Intent(this, BarcodeReaderActivity::class.java)
            //val launchIntent = Intent(this, MainActivity::class.java)
            //startActivity(launchIntent)
            startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST)
        }

        mainLayout.cancelButton.setOnClickListener{
            total=0
            val launchIntent = Intent(this, MainActivity::class.java)
            startActivity(launchIntent)
        }

        mainLayout.payButton.setOnClickListener{
            val message = ""+total
            val launchIntent = Intent(this, ResultActivity::class.java)
            launchIntent.putExtra(KEY_MESSAGE, message)
            startActivity(launchIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "Scan error")
            return
        }

        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
            val barcode: Barcode? =
                data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE)
            var articulo= barcode?.rawValue
            var precio=nameToPrice(articulo)
            total+=precio;
            val launchIntent = Intent(this, MainActivity::class.java)
            startActivity(launchIntent)
        }
    }
    fun nameToPrice(productName:String?): Int{
        var price:Int=-1;
        when(productName){
            "Grande"->price=1000;
            "Mediano"->price=100;
            "Pequeno"->price=10;
        }
        return price;
    }
}
