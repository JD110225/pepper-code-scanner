package com.softbankrobotics.dx.peppercodescannersample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.google.android.gms.vision.barcode.Barcode
import com.softbankrobotics.dx.peppercodescanner.BarcodeReaderActivity
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_result.textViewResult
import kotlinx.android.synthetic.main.activity_result.view.*
import kotlinx.android.synthetic.main.transaccion.*
import kotlinx.android.synthetic.main.transaccion.view.*

class TransactionActivity : AppCompatActivity(), RobotLifecycleCallbacks {

    companion object {
        private const val KEY_MESSAGE = "key_message"
        private const val TAG = "ResultActivity"
        private const val RESTART_TIME = 10000L
        private const val BARCODE_READER_ACTIVITY_REQUEST = 1208
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
        setContentView(R.layout.transaccion)
        transactionLayout.scanAgainButton.setVisibility(View.GONE);
        val mensajeVuelto = intent.getStringExtra(KEY_MESSAGE)
        textViewResult.text = mensajeVuelto
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()

        Handler().postDelayed({
            finish()
        }, RESTART_TIME)
    }
    override fun onRobotFocusGained(qiContext: QiContext) {
        val mensajeVuelto = intent.getStringExtra(KEY_MESSAGE)
        speak(mensajeVuelto,qiContext)
    }
    override fun onRobotFocusLost() {
        // The robot focus is lost.
    }
    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }
    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
    }
        //Clean code papá, copiando metodo de otra clase
    fun speak(phrase:String,qiContext: QiContext) {
        val say: Say = SayBuilder.with(qiContext) // Create the builder with the context.
            .withLocale(MainActivity.locale)
            .withText(phrase) // Set the text to say.
            .build() // Build the say action.
        say.run()
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
            val launchIntent = Intent(this, TransactionActivity::class.java)
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
