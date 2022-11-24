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
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.ListenBuilder
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.google.android.gms.vision.barcode.Barcode
import com.softbankrobotics.dx.peppercodescanner.BarcodeReaderActivity
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_result.view.*
import java.lang.Math.abs
import com.softbankrobotics.dx.peppercodescannersample.MainActivity
class ResultActivity : AppCompatActivity(), RobotLifecycleCallbacks {

    companion object {
        private const val KEY_MESSAGE = "key_message"
        private const val PRECIO_ORIGINAL = "precio_original"
        private const val TAG = "ResultActivity"
        private const val RESTART_TIME = 10000L
        private const val BARCODE_READER_ACTIVITY_REQUEST = 1208
        private var precioPagar:String=""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
        setContentView(R.layout.activity_result)
        val message = intent.getStringExtra(KEY_MESSAGE)
        precioPagar = message
        textViewResult.text = "Tu precio a pagar es: "+message
        resultLayout.backButton.setOnClickListener{
            val launchIntent = Intent(this, MainActivity::class.java)
            startActivity(launchIntent)
        }
        resultLayout.confirmPayButton.setOnClickListener{
            val launchIntent = Intent(this, BarcodeReaderActivity::class.java)
            startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST)
//
//            val msg=generarMensajeVuelto(1000)
//            val launchIntent = Intent(this, TransactionActivity::class.java)
//            launchIntent.putExtra(KEY_MESSAGE, msg)
//            launchIntent.putExtra(PRECIO_ORIGINAL, message)
//
//            startActivity(launchIntent)
        }
    }
    //Clean code papá, copiando metodo de otra clase
    fun speak(phrase:String,qiContext: QiContext ) {
        val say: Say = SayBuilder.with(qiContext) // Create the builder with the context.
            .withLocale(MainActivity.locale)
            .withText(phrase) // Set the text to say.
            .build() // Build the say action.
        say.run()
    }
    override fun onRobotFocusGained(qiContext: QiContext) {
        speak("Tu precio a pagar corresponde a "+ precioPagar+"Quieres proceder con el pago?",qiContext)
//        speak("Quieres proceder con el pago?",qiContext)
//        val phraseSetBooleano = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
//            .withTexts("Sí","No") // Add the phrases Pepper will listen to.
//            .build() // Build the PhraseSet.
//        val listen = ListenBuilder.with(qiContext) // Create the builder with the QiContext.
//            .withLocale(MainActivity.locale)
//            .withPhraseSets(phraseSetBooleano) // Set the PhraseSets to listen to.
//            .build() // Build the listen action.
//        val listenResult = listen.run()
//        val humanText = listenResult.heardPhrase.text
//        if(humanText=="Si"){
//            val launchIntent = Intent(this, BarcodeReaderActivity::class.java)
//            startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST)
//        }
//        else if(humanText=="No"){
//            val launchIntent = Intent(this, MainActivity::class.java)
//            startActivity(launchIntent)
//        }
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
            Log.i("Precio escaneado",scannedPrice)
            var mensajeVuelto:String = generarMensajeVuelto((scannedPrice?.toInt() ?: -1) - precioPagar.toInt())
            Log.i("Vuelto: ",mensajeVuelto)
            MainActivity.resetTotal()
            val launchIntent = Intent(this, TransactionActivity::class.java)
            launchIntent.putExtra(KEY_MESSAGE, mensajeVuelto)
            startActivity(launchIntent)
        }
    }
    fun generarMensajeVuelto(vuelto:Int):String{
        var mensaje:String;
        if(vuelto>0){
            mensaje="Tu vuelto corresponde a: "+vuelto;
        }
        else if(vuelto==0){
            mensaje="Gracias por pagar exacto!";
        }
        else{
            mensaje = "Aún debes pagar "+ abs(vuelto)
        }
        return mensaje;
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
