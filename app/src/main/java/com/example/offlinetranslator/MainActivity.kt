package com.example.offlinetranslator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var resultTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var toggleListeningButton: MaterialButton
    private lateinit var clearButton: MaterialButton
    private var voskModel: org.vosk.Model? = null
    private var speechService: SpeechService? = null
    private var tts: TextToSpeech? = null
    private lateinit var languageIdentifier: LanguageIdentifier
    private var englishSpanishTranslator: Translator? = null
    private var spanishEnglishTranslator: Translator? = null

    private var currentListeningLanguage: String = "en"
    private var isListening: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.result_text_view)
        statusTextView = findViewById(R.id.status_text)
        toggleListeningButton = findViewById(R.id.toggle_listening_button)
        clearButton = findViewById(R.id.clear_button)

        toggleListeningButton.setOnClickListener { toggleListening() }
        clearButton.setOnClickListener { clearConversation() }

        LibVosk.setLogLevel(LogLevel.INFO)

        tts = TextToSpeech(this, this)

        languageIdentifier = LanguageIdentification.getClient()

        // Initialize translators
        val englishSpanishOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
        englishSpanishTranslator = Translation.getClient(englishSpanishOptions)

        val spanishEnglishOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.SPANISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        spanishEnglishTranslator = Translation.getClient(spanishEnglishOptions)

        // Download translation models
        downloadTranslationModels()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_CODE)
        } else {
            initVoskModel()
        }
    }

    private fun clearConversation() {
        resultTextView.text = "Press the microphone button to start a conversation"
        statusTextView.text = "Ready to listen"
        currentListeningLanguage = "en"
    }

    private fun downloadTranslationModels() {
        statusTextView.text = "Downloading translation models..."
        
        englishSpanishTranslator?.downloadModelIfNeeded()
            ?.addOnSuccessListener { 
                Log.d(TAG, "English-Spanish model downloaded")
                updateStatusIfReady()
            }
            ?.addOnFailureListener { exception -> 
                Log.e(TAG, "Error downloading English-Spanish model", exception)
                statusTextView.text = "Error downloading models"
            }

        spanishEnglishTranslator?.downloadModelIfNeeded()
            ?.addOnSuccessListener { 
                Log.d(TAG, "Spanish-English model downloaded")
                updateStatusIfReady()
            }
            ?.addOnFailureListener { exception -> 
                Log.e(TAG, "Error downloading Spanish-English model", exception)
                statusTextView.text = "Error downloading models"
            }
    }

    private fun updateStatusIfReady() {
        if (voskModel != null) {
            statusTextView.text = "Ready to listen"
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS language not supported or missing data")
            }
        } else {
            Log.e(TAG, "TTS initialization failed")
        }
    }

    private fun speakOut(text: String, language: Locale) {
        tts?.language = language
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun initVoskModel() {
        statusTextView.text = "Loading speech recognition model..."
        StorageService.unpack(this, "model-en-us", "model",
            { unpackedModel ->
                voskModel = unpackedModel
                setUiState(true)
                updateStatusIfReady()
            },
            { exception ->
                setErrorState("Failed to unpack the Vosk model: " + exception.message)
            })
    }

    private fun toggleListening() {
        if (isListening) {
            stopListening()
        } else {
            startListening(currentListeningLanguage)
        }
    }

    private fun startListening(languageCode: String) {
        try {
            val rec = Recognizer(voskModel, 16000.0f)
            speechService = SpeechService(rec, 16000.0f) { hypothesis ->
                val recognizedText = hypothesis.result.replace("\n", "").replace("\r", "").trim()
                if (recognizedText.isNotEmpty()) {
                    runOnUiThread {
                        resultTextView.append("\n\n${if (languageCode == "en") "🇺🇸" else "🇪🇸"} You: $recognizedText")
                        identifyAndTranslate(recognizedText)
                    }
                }
            }
            isListening = true
            setUiState(false)
            statusTextView.text = "Listening... Speak now"
        } catch (e: IOException) {
            setErrorState(e.message ?: "Error starting speech service")
        }
    }

    private fun stopListening() {
        speechService?.cancel()
        speechService?.shutdown()
        speechService = null
        isListening = false
        setUiState(true)
        statusTextView.text = "Ready to listen"
    }

    private fun identifyAndTranslate(text: String) {
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                Log.d(TAG, "Detected language: $languageCode")
                if (languageCode == "en") {
                    englishSpanishTranslator?.translate(text)
                        ?.addOnSuccessListener { translatedText ->
                            runOnUiThread {
                                resultTextView.append("\n🇪🇸 Translation: $translatedText")
                                speakOut(translatedText, Locale("es"))
                                currentListeningLanguage = "es"
                                statusTextView.text = "Speaking translation... Please wait"
                                // Restart listening after a delay
                                toggleListeningButton.postDelayed({ 
                                    if (!isListening) {
                                        startListening(currentListeningLanguage)
                                    }
                                }, 3000)
                            }
                        }
                        ?.addOnFailureListener { exception -> 
                            Log.e(TAG, "Error translating English to Spanish", exception)
                            runOnUiThread { statusTextView.text = "Translation error" }
                        }
                } else if (languageCode == "es") {
                    spanishEnglishTranslator?.translate(text)
                        ?.addOnSuccessListener { translatedText ->
                            runOnUiThread {
                                resultTextView.append("\n🇺🇸 Translation: $translatedText")
                                speakOut(translatedText, Locale.US)
                                currentListeningLanguage = "en"
                                statusTextView.text = "Speaking translation... Please wait"
                                // Restart listening after a delay
                                toggleListeningButton.postDelayed({ 
                                    if (!isListening) {
                                        startListening(currentListeningLanguage)
                                    }
                                }, 3000)
                            }
                        }
                        ?.addOnFailureListener { exception -> 
                            Log.e(TAG, "Error translating Spanish to English", exception)
                            runOnUiThread { statusTextView.text = "Translation error" }
                        }
                } else {
                    runOnUiThread {
                        resultTextView.append("\n⚠️ Unsupported language: $languageCode")
                        statusTextView.text = "Unsupported language detected"
                    }
                }
            }
            .addOnFailureListener { exception -> 
                Log.e(TAG, "Error identifying language", exception)
                runOnUiThread { statusTextView.text = "Language detection error" }
            }
    }

    private fun setUiState(enabled: Boolean) {
        toggleListeningButton.isEnabled = enabled && voskModel != null
        if (isListening) {
            toggleListeningButton.text = "Stop Listening"
            toggleListeningButton.setIconResource(R.drawable.ic_clear)
        } else {
            toggleListeningButton.text = "Start Listening"
            toggleListeningButton.setIconResource(R.drawable.ic_mic)
        }
    }

    private fun setErrorState(message: String) {
        resultTextView.text = "Error: $message"
        statusTextView.text = "Error occurred"
        toggleListeningButton.isEnabled = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initVoskModel()
            } else {
                setErrorState("Record audio permission denied")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechService?.cancel()
        speechService?.shutdown()
        voskModel?.close()
        tts?.stop()
        tts?.shutdown()
        englishSpanishTranslator?.close()
        spanishEnglishTranslator?.close()
    }

    companion object {
        private const val RECORD_AUDIO_PERMISSION_CODE = 1
        private const val TAG = "OfflineTranslatorApp"
    }
}

