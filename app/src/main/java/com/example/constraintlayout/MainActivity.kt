package com.example.constraintlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.content.Intent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, TextToSpeech.OnInitListener {

    private lateinit var amountEditText: EditText
    private lateinit var peopleEditText: EditText
    private lateinit var shareButton: ImageButton
    private lateinit var speakButton: ImageButton
    private lateinit var resultTextView: TextView
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing components variables
        amountEditText = findViewById(R.id.amountEditText)
        peopleEditText = findViewById(R.id.peopleEditText)
        shareButton = findViewById(R.id.shareButton)
        speakButton = findViewById(R.id.speakButton)
        resultTextView = findViewById(R.id.resultTextView)

        // Adding TextWatchers
        amountEditText.addTextChangedListener(textWatcher)
        peopleEditText.addTextChangedListener(textWatcher)

        shareButton.setOnClickListener(this)
        speakButton.setOnClickListener(this)

        // Initializing the tts
        tts = TextToSpeech(this, this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.shareButton -> shareResult()
            R.id.speakButton -> speakResult()
        }
    }

    private fun calculateResult() {
        val amount = amountEditText.text.toString().toDoubleOrNull()
        val people = peopleEditText.text.toString().toIntOrNull()

        if (amount != null && people != null && people != 0) {
            val result = amount / people
            updateResultText(result)
        } else {
            resultTextView.text = ""
        }
    }

    private fun shareResult() {
        val resultText = resultTextView.text.toString()
        if (resultText.isNotEmpty()) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, resultText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Compartilhar resultado"))
        } else {
            showLocalizedToast(R.string.toast_mensagem_compartilhar)
        }
    }


    private fun speakResult() {
        val resultText = resultTextView.text.toString()
        if (resultText.isNotEmpty()) {
            speakOut(resultText)
        } else {
            showLocalizedToast(R.string.toast_mensagem_falar)
        }
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun updateResultText(result: Double) {
        val locale = Locale.getDefault()
        val resultText: String = when (locale.language) {
            "fr" -> getString(R.string.result_text, result)
            "es" -> getString(R.string.result_text, result)
            else -> getString(R.string.result_text, result)
        }
        resultTextView.text = resultText
    }

    private fun showLocalizedToast(messageResId: Int) {
        val message = getString(messageResId)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale.getDefault()
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Falha ao inicializar Text-to-Speech", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "Falha ao inicializar Text-to-Speech", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            calculateResult()
        }
    }
}

