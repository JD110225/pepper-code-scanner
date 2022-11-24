package com.softbankrobotics.dx.peppercodescannersample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.Chat
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.ListenBuilder
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.google.android.gms.vision.barcode.Barcode
import com.softbankrobotics.dx.peppercodescanner.BarcodeReaderActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
class MainActivity : AppCompatActivity(), RobotLifecycleCallbacks {

    companion object {
        fun resetTotal() {
            total=0
        }

        // Store the Chat action.
        val locale: Locale = Locale(Language.SPANISH, Region.SPAIN)
        var total=0
        private const val TAG = "MainActivity"
        const val BARCODE_READER_ACTIVITY_REQUEST = 1208
        private const val KEY_MESSAGE = "key_message"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
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
    override fun onRobotFocusGained(qiContext: QiContext) {

        val say: Say = SayBuilder.with(qiContext) // Create the builder with the context.
            .withLocale(locale)
            .withText("Bienvenido a Cashbot! Selecciona una opción") // Set the text to say.
            .build() // Build the say action.
        say.run()
        val phraseSetOptions = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
            .withTexts("Escanear","Pagar","Cancelar") // Add the phrases Pepper will listen to.
            .build() // Build the PhraseSet.
        val listen = ListenBuilder.with(qiContext) // Create the builder with the QiContext.
            .withLocale(locale)
            .withPhraseSets(phraseSetOptions) // Set the PhraseSets to listen to.
            .build() // Build the listen action.
        val listenResult = listen.run()

        val humanText = listenResult.heardPhrase.text
        Log.i(TAG, "Heard phrase: $humanText")
        makeDecision(humanText,qiContext)
    }
    fun speak(phrase:String,qiContext: QiContext ){
        val say: Say = SayBuilder.with(qiContext) // Create the builder with the context.
            .withLocale(locale)
            .withText(phrase) // Set the text to say.
            .build() // Build the say action.
        say.run()
    }
    fun makeDecision(heardPhrase:String, qiContext: QiContext){
        if(heardPhrase=="Escanear"){
            speak("Por favor escanea un código",qiContext)
            val launchIntent = Intent(this, BarcodeReaderActivity::class.java)
            startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST)
        }
        else if(heardPhrase=="Cancelar"){
            speak("Tu orden ha sido cancelada",qiContext)
            total=0
            val launchIntent = Intent(this, MainActivity::class.java)
            startActivity(launchIntent)
        }
        else if(heardPhrase=="Pagar"){
            val message = ""+total
            val launchIntent = Intent(this, ResultActivity::class.java)
            launchIntent.putExtra(KEY_MESSAGE, message)
            startActivity(launchIntent)
        }
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
        var price:Int=0;
        when(productName){
            "Grande"->price=1000;
            "Mediano"->price=100;
            "Pequeno"->price=10;
        }
        return price;
    }
}
