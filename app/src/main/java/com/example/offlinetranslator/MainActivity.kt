package com.example.offlinetranslator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var subtitleTextView: TextView
    private lateinit var conversationRecyclerView: RecyclerView
    private lateinit var emptyStateTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var listeningIndicatorIcon: ImageView
    private lateinit var toggleListeningButton: MaterialButton
    private lateinit var clearButton: MaterialButton
    private lateinit var languageToggleButton: MaterialButton
    private lateinit var exportButton: MaterialButton
    private lateinit var startLanguageSpinner: Spinner
    private lateinit var conversationAdapter: ConversationAdapter
    private val conversationMessages = mutableListOf<ConversationMessage>()
    private var startLanguageOptions: List<LanguageOption> = emptyList()
    private var isUpdatingStartLanguage: Boolean = false

    private var englishVoskModel: Model? = null
    private var spanishVoskModel: Model? = null
    private var speechService: SpeechService? = null
    private var tts: TextToSpeech? = null
    private lateinit var languageIdentifier: LanguageIdentifier
    private var englishSpanishTranslator: Translator? = null
    private var spanishEnglishTranslator: Translator? = null
    private var englishFrenchTranslator: Translator? = null
    private var frenchEnglishTranslator: Translator? = null
    private var englishGermanTranslator: Translator? = null
    private var germanEnglishTranslator: Translator? = null

    private var currentMode: ConversationMode = ConversationMode.AUTO
    private var currentListeningLanguage: String = "en"
    private var isListening: Boolean = false
    private var pendingModelDownloads: Int = 0
    private var pendingSpeechModelLoads: Int = 0

    private val silenceHandler = Handler(Looper.getMainLooper())
    private val silenceTimeoutRunnable = Runnable {
        if (isListening) {
            stopListening()
            statusTextView.text = getString(R.string.status_silence_timeout)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        subtitleTextView = findViewById(R.id.subtitle)
        conversationRecyclerView = findViewById(R.id.conversation_recycler_view)
        emptyStateTextView = findViewById(R.id.empty_state_text)
        statusTextView = findViewById(R.id.status_text)
        listeningIndicatorIcon = findViewById(R.id.listening_indicator_icon)
        toggleListeningButton = findViewById(R.id.toggle_listening_button)
        clearButton = findViewById(R.id.clear_button)
        languageToggleButton = findViewById(R.id.language_toggle_button)
        exportButton = findViewById(R.id.export_button)
        startLanguageSpinner = findViewById(R.id.start_language_spinner)

        conversationAdapter = ConversationAdapter(conversationMessages)
        conversationRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationRecyclerView.adapter = conversationAdapter

        toggleListeningButton.setOnClickListener { toggleListening() }
        clearButton.setOnClickListener { clearConversation() }
        languageToggleButton.setOnClickListener { cycleLanguageMode() }
        exportButton.setOnClickListener { exportConversation() }
        setupStartLanguageSpinner()

        LibVosk.setLogLevel(LogLevel.INFO)

        tts = TextToSpeech(this, this)
        languageIdentifier = LanguageIdentification.getClient()

        initializeTranslators()
        downloadTranslationModels()
        updateModeUi()
        updateListeningUi()
        updateEmptyState()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_CODE)
        } else {
            initVoskModels()
        }
    }

    private fun initializeTranslators() {
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

        val englishFrenchOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.FRENCH)
            .build()
        englishFrenchTranslator = Translation.getClient(englishFrenchOptions)

        val frenchEnglishOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.FRENCH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        frenchEnglishTranslator = Translation.getClient(frenchEnglishOptions)

        val englishGermanOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()
        englishGermanTranslator = Translation.getClient(englishGermanOptions)

        val germanEnglishOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.GERMAN)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        germanEnglishTranslator = Translation.getClient(germanEnglishOptions)
    }

    private fun setupStartLanguageSpinner() {
        startLanguageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isUpdatingStartLanguage) {
                    return
                }
                val selected = startLanguageOptions.getOrNull(position) ?: return
                if (currentListeningLanguage != selected.code) {
                    currentListeningLanguage = selected.code
                    if (!isListening) {
                        statusTextView.text = getString(R.string.status_ready)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No-op
            }
        }
    }

    private fun updateStartLanguageOptions() {
        val options = when (currentMode) {
            ConversationMode.AUTO -> listOf(
                LanguageOption("en", getString(R.string.language_option_english)),
                LanguageOption("es", getString(R.string.language_option_spanish))
            )
            ConversationMode.ENGLISH_SPANISH -> listOf(
                LanguageOption("en", getString(R.string.language_option_english)),
                LanguageOption("es", getString(R.string.language_option_spanish))
            )
            ConversationMode.ENGLISH_FRENCH -> listOf(
                LanguageOption("en", getString(R.string.language_option_english)),
                LanguageOption("fr", getString(R.string.language_option_french))
            )
            ConversationMode.ENGLISH_GERMAN -> listOf(
                LanguageOption("en", getString(R.string.language_option_english)),
                LanguageOption("de", getString(R.string.language_option_german))
            )
        }
        startLanguageOptions = options
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options.map { it.label })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startLanguageSpinner.adapter = adapter

        val selectedIndex = options.indexOfFirst { it.code == currentListeningLanguage }.takeIf { it >= 0 } ?: 0
        isUpdatingStartLanguage = true
        startLanguageSpinner.setSelection(selectedIndex, false)
        currentListeningLanguage = options[selectedIndex].code
        isUpdatingStartLanguage = false
    }

    private fun syncStartLanguageSelection() {
        val selectedIndex = startLanguageOptions.indexOfFirst { it.code == currentListeningLanguage }
        if (selectedIndex >= 0) {
            isUpdatingStartLanguage = true
            startLanguageSpinner.setSelection(selectedIndex, false)
            isUpdatingStartLanguage = false
        }
    }

    private fun clearConversation() {
        if (isListening) {
            stopListening()
        }
        conversationMessages.clear()
        conversationAdapter.notifyDataSetChanged()
        updateEmptyState()
        currentListeningLanguage = "en"
        syncStartLanguageSelection()
        statusTextView.text = getString(R.string.status_ready)
    }

    private fun downloadTranslationModels() {
        statusTextView.text = getString(R.string.status_downloading)
        startModelDownload(englishSpanishTranslator, "English-Spanish")
        startModelDownload(spanishEnglishTranslator, "Spanish-English")
        startModelDownload(englishFrenchTranslator, "English-French")
        startModelDownload(frenchEnglishTranslator, "French-English")
        startModelDownload(englishGermanTranslator, "English-German")
        startModelDownload(germanEnglishTranslator, "German-English")
    }

    private fun startModelDownload(translator: Translator?, label: String) {
        if (translator == null) {
            return
        }
        pendingModelDownloads += 1
        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                Log.d(TAG, "$label model downloaded")
                pendingModelDownloads -= 1
                updateStatusIfReady()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error downloading $label model", exception)
                pendingModelDownloads -= 1
                statusTextView.text = getString(R.string.status_error_models)
                addSystemMessage(getString(R.string.status_error_models))
            }
    }

    private fun updateStatusIfReady() {
        if (pendingModelDownloads == 0 && pendingSpeechModelLoads == 0 && areSpeechModelsReady() && !isListening) {
            statusTextView.text = getString(R.string.status_ready)
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

    private fun initVoskModels() {
        statusTextView.text = getString(R.string.status_loading)
        pendingSpeechModelLoads = 0
        unpackSpeechModel(ENGLISH_MODEL_ASSET_PATH, ENGLISH_MODEL_STORAGE_DIR, true)
        unpackSpeechModel(SPANISH_MODEL_ASSET_PATH, SPANISH_MODEL_STORAGE_DIR, false)
    }

    private fun unpackSpeechModel(assetPath: String, destinationDir: String, isEnglish: Boolean) {
        pendingSpeechModelLoads += 1
        StorageService.unpack(this, assetPath, destinationDir,
            { unpackedModel ->
                if (isEnglish) {
                    englishVoskModel = unpackedModel
                } else {
                    spanishVoskModel = unpackedModel
                }
                pendingSpeechModelLoads -= 1
                updateListeningUi()
                updateStatusIfReady()
            },
            { exception ->
                pendingSpeechModelLoads -= 1
                val languageLabel = if (isEnglish) getString(R.string.language_label_english) else getString(R.string.language_label_spanish)
                Log.e(TAG, "Failed to unpack $languageLabel model", exception)
                setErrorState(getString(R.string.status_error_speech_model, languageLabel))
            })
    }

    private fun areSpeechModelsReady(): Boolean {
        return englishVoskModel != null && spanishVoskModel != null
    }

    private fun toggleListening() {
        if (isListening) {
            stopListening()
        } else {
            startListening()
        }
    }

    private fun startListening() {
        val model = if (currentListeningLanguage == "es") spanishVoskModel else englishVoskModel
        if (model == null) {
            val languageLabel = if (currentListeningLanguage == "es") getString(R.string.language_label_spanish) else getString(R.string.language_label_english)
            setErrorState(getString(R.string.status_error_speech_model, languageLabel))
            return
        }
        try {
            val rec = Recognizer(model, 16000.0f)
            speechService = SpeechService(rec, 16000.0f) { hypothesis ->
                resetSilenceTimeout()
                val recognizedText = hypothesis.result.replace("\n", "").replace("\r", "").trim()
                if (recognizedText.isNotEmpty()) {
                    runOnUiThread {
                        stopListening(false)
                        statusTextView.text = getString(R.string.status_processing)
                        if (currentMode == ConversationMode.AUTO) {
                            identifyAndTranslate(recognizedText)
                        } else {
                            addUserMessage(recognizedText, currentListeningLanguage)
                            translateWithOverride(recognizedText, currentListeningLanguage)
                        }
                    }
                }
            }
            isListening = true
            updateListeningUi()
            statusTextView.text = getString(R.string.status_listening)
            resetSilenceTimeout()
        } catch (e: IOException) {
            setErrorState(e.message ?: getString(R.string.status_error_occurred))
        }
    }

    private fun stopListening(updateStatus: Boolean = true) {
        speechService?.cancel()
        speechService?.shutdown()
        speechService = null
        isListening = false
        silenceHandler.removeCallbacks(silenceTimeoutRunnable)
        updateListeningUi()
        if (updateStatus) {
            statusTextView.text = getString(R.string.status_ready)
        }
    }

    private fun resetSilenceTimeout() {
        silenceHandler.removeCallbacks(silenceTimeoutRunnable)
        silenceHandler.postDelayed(silenceTimeoutRunnable, SILENCE_TIMEOUT_MS)
    }

    private fun identifyAndTranslate(text: String) {
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                val normalizedCode = languageCode.lowercase(Locale.US)
                Log.d(TAG, "Detected language: $normalizedCode")
                if (normalizedCode == "en" || normalizedCode == "es") {
                    addUserMessage(text, normalizedCode)
                    val targetLanguage = if (normalizedCode == "en") "es" else "en"
                    translateText(text, normalizedCode, targetLanguage)
                } else {
                    addSystemMessage(getString(R.string.message_unsupported_language, normalizedCode))
                    statusTextView.text = getString(R.string.status_unsupported_language)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error identifying language", exception)
                runOnUiThread { statusTextView.text = getString(R.string.status_language_error) }
            }
    }

    private fun translateWithOverride(text: String, sourceLanguage: String) {
        val targetLanguage = getTargetLanguageForManualMode(sourceLanguage)
        if (targetLanguage == null) {
            addSystemMessage(getString(R.string.message_unsupported_language, sourceLanguage))
            statusTextView.text = getString(R.string.status_unsupported_language)
            return
        }
        translateText(text, sourceLanguage, targetLanguage)
    }

    private fun translateText(text: String, sourceLanguage: String, targetLanguage: String) {
        val translator = getTranslator(sourceLanguage, targetLanguage)
        if (translator == null) {
            addSystemMessage(getString(R.string.message_unsupported_language, sourceLanguage))
            statusTextView.text = getString(R.string.status_unsupported_language)
            return
        }
        statusTextView.text = getString(R.string.status_translating)
        translator.translate(text)
            .addOnSuccessListener { translatedText ->
                runOnUiThread {
                    addTranslationMessage(translatedText, targetLanguage)
                    speakOut(translatedText, localeForLanguage(targetLanguage))
                    currentListeningLanguage = targetLanguage
                    syncStartLanguageSelection()
                    statusTextView.text = getString(R.string.status_speaking)
                    toggleListeningButton.postDelayed({
                        if (!isListening) {
                            startListening()
                        }
                    }, RESTART_DELAY_MS)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error translating $sourceLanguage to $targetLanguage", exception)
                runOnUiThread { statusTextView.text = getString(R.string.status_translation_error) }
            }
    }

    private fun getTranslator(sourceLanguage: String, targetLanguage: String): Translator? {
        return when {
            sourceLanguage == "en" && targetLanguage == "es" -> englishSpanishTranslator
            sourceLanguage == "es" && targetLanguage == "en" -> spanishEnglishTranslator
            sourceLanguage == "en" && targetLanguage == "fr" -> englishFrenchTranslator
            sourceLanguage == "fr" && targetLanguage == "en" -> frenchEnglishTranslator
            sourceLanguage == "en" && targetLanguage == "de" -> englishGermanTranslator
            sourceLanguage == "de" && targetLanguage == "en" -> germanEnglishTranslator
            else -> null
        }
    }

    private fun getTargetLanguageForManualMode(sourceLanguage: String): String? {
        return when (currentMode) {
            ConversationMode.ENGLISH_SPANISH -> if (sourceLanguage == "en") "es" else if (sourceLanguage == "es") "en" else null
            ConversationMode.ENGLISH_FRENCH -> if (sourceLanguage == "en") "fr" else if (sourceLanguage == "fr") "en" else null
            ConversationMode.ENGLISH_GERMAN -> if (sourceLanguage == "en") "de" else if (sourceLanguage == "de") "en" else null
            ConversationMode.AUTO -> null
        }
    }

    private fun localeForLanguage(languageCode: String): Locale {
        return when (languageCode) {
            "es" -> Locale("es")
            "fr" -> Locale.FRENCH
            "de" -> Locale.GERMAN
            else -> Locale.US
        }
    }

    private fun cycleLanguageMode() {
        currentMode = when (currentMode) {
            ConversationMode.AUTO -> ConversationMode.ENGLISH_SPANISH
            ConversationMode.ENGLISH_SPANISH -> ConversationMode.ENGLISH_FRENCH
            ConversationMode.ENGLISH_FRENCH -> ConversationMode.ENGLISH_GERMAN
            ConversationMode.ENGLISH_GERMAN -> ConversationMode.AUTO
        }
        currentListeningLanguage = "en"
        if (isListening) {
            stopListening()
        }
        updateModeUi()
        statusTextView.text = getString(R.string.status_ready)
    }

    private fun updateModeUi() {
        val subtitleRes = when (currentMode) {
            ConversationMode.AUTO -> R.string.subtitle_auto
            ConversationMode.ENGLISH_SPANISH -> R.string.subtitle_en_es
            ConversationMode.ENGLISH_FRENCH -> R.string.subtitle_en_fr
            ConversationMode.ENGLISH_GERMAN -> R.string.subtitle_en_de
        }
        val toggleRes = when (currentMode) {
            ConversationMode.AUTO -> R.string.language_toggle_auto
            ConversationMode.ENGLISH_SPANISH -> R.string.language_toggle_en_es
            ConversationMode.ENGLISH_FRENCH -> R.string.language_toggle_en_fr
            ConversationMode.ENGLISH_GERMAN -> R.string.language_toggle_en_de
        }
        subtitleTextView.text = getString(subtitleRes)
        languageToggleButton.text = getString(toggleRes)
        updateStartLanguageOptions()
    }

    private fun updateListeningUi() {
        toggleListeningButton.isEnabled = areSpeechModelsReady()
        if (isListening) {
            toggleListeningButton.text = getString(R.string.stop_listening)
            toggleListeningButton.setIconResource(R.drawable.ic_clear)
        } else {
            toggleListeningButton.text = getString(R.string.start_listening)
            toggleListeningButton.setIconResource(R.drawable.ic_mic)
        }
        val indicatorColor = if (isListening) R.color.listening_active else R.color.listening_idle
        listeningIndicatorIcon.setColorFilter(ContextCompat.getColor(this, indicatorColor))
        startLanguageSpinner.isEnabled = !isListening && areSpeechModelsReady()
    }

    private fun addUserMessage(text: String, languageCode: String) {
        val message = "${getLanguageFlag(languageCode)} ${getString(R.string.message_you, text)}"
        addMessage(message, MessageType.USER)
    }

    private fun addTranslationMessage(text: String, languageCode: String) {
        val message = "${getLanguageFlag(languageCode)} ${getString(R.string.message_translation, text)}"
        addMessage(message, MessageType.TRANSLATION)
    }

    private fun addSystemMessage(text: String) {
        addMessage(text, MessageType.SYSTEM)
    }

    private fun addMessage(text: String, type: MessageType) {
        conversationMessages.add(ConversationMessage(text, type))
        conversationAdapter.notifyItemInserted(conversationMessages.size - 1)
        conversationRecyclerView.scrollToPosition(conversationMessages.size - 1)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (conversationMessages.isEmpty()) {
            emptyStateTextView.visibility = View.VISIBLE
            conversationRecyclerView.visibility = View.GONE
        } else {
            emptyStateTextView.visibility = View.GONE
            conversationRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun getLanguageFlag(languageCode: String): String {
        return when (languageCode) {
            "en" -> "🇺🇸"
            "es" -> "🇪🇸"
            "fr" -> "🇫🇷"
            "de" -> "🇩🇪"
            else -> "🏳️"
        }
    }

    private fun exportConversation() {
        if (conversationMessages.isEmpty()) {
            Toast.makeText(this, getString(R.string.export_empty), Toast.LENGTH_SHORT).show()
            return
        }
        val transcript = conversationMessages.joinToString("\n") { it.text }
        val fileName = "conversation_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.txt"
        val file = File(filesDir, fileName)
        file.writeText(transcript)
        Toast.makeText(this, getString(R.string.export_ready), Toast.LENGTH_SHORT).show()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, transcript)
            putExtra(Intent.EXTRA_SUBJECT, fileName)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.export_conversation)))
    }

    private fun setErrorState(message: String) {
        addSystemMessage(getString(R.string.error_prefix, message))
        statusTextView.text = getString(R.string.status_error_occurred)
        toggleListeningButton.isEnabled = false
        isListening = false
        updateListeningUi()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initVoskModels()
            } else {
                setErrorState(getString(R.string.permission_denied))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        silenceHandler.removeCallbacks(silenceTimeoutRunnable)
        speechService?.cancel()
        speechService?.shutdown()
        englishVoskModel?.close()
        spanishVoskModel?.close()
        tts?.stop()
        tts?.shutdown()
        englishSpanishTranslator?.close()
        spanishEnglishTranslator?.close()
        englishFrenchTranslator?.close()
        frenchEnglishTranslator?.close()
        englishGermanTranslator?.close()
        germanEnglishTranslator?.close()
    }

    private data class LanguageOption(val code: String, val label: String)

    private data class ConversationMessage(val text: String, val type: MessageType)

    private enum class MessageType {
        USER,
        TRANSLATION,
        SYSTEM
    }

    private enum class ConversationMode {
        AUTO,
        ENGLISH_SPANISH,
        ENGLISH_FRENCH,
        ENGLISH_GERMAN
    }

    private class ConversationAdapter(
        private val items: List<ConversationMessage>
    ) : RecyclerView.Adapter<ConversationAdapter.MessageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            return MessageViewHolder(view)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val item = items[position]
            holder.textView.text = item.text
            val colorRes = when (item.type) {
                MessageType.USER -> R.color.primary_color
                MessageType.TRANSLATION -> R.color.accent_color
                MessageType.SYSTEM -> R.color.secondary_color
            }
            holder.textView.setTextColor(ContextCompat.getColor(holder.textView.context, colorRes))
            holder.textView.textSize = 15f
        }

        override fun getItemCount(): Int = items.size

        class MessageViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }

    companion object {
        private const val RECORD_AUDIO_PERMISSION_CODE = 1
        private const val TAG = "OfflineTranslatorApp"
        private const val SILENCE_TIMEOUT_MS = 1500L
        private const val RESTART_DELAY_MS = 3000L
        private const val ENGLISH_MODEL_ASSET_PATH = "model-en"
        private const val SPANISH_MODEL_ASSET_PATH = "model-es"
        private const val ENGLISH_MODEL_STORAGE_DIR = "model-en"
        private const val SPANISH_MODEL_STORAGE_DIR = "model-es"
    }
}

