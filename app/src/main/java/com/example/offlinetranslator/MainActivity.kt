package com.example.offlinetranslator

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
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
import org.vosk.android.StorageService
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var subtitleTextView: TextView
    private lateinit var conversationRecyclerView: RecyclerView
    private lateinit var emptyStateTextView: TextView
    private lateinit var searchInput: TextInputEditText
    private lateinit var statusTextView: TextView
    private lateinit var listeningIndicatorIcon: ImageView
    private lateinit var micLevelMeter: ProgressBar
    private lateinit var micLevelValueText: TextView
    private lateinit var modelDownloadProgress: ProgressBar
    private lateinit var modelSizeTextView: TextView
    private lateinit var toggleListeningButton: MaterialButton
    private lateinit var clearButton: MaterialButton
    private lateinit var languageToggleButton: MaterialButton
    private lateinit var swapLanguageButton: MaterialButton
    private lateinit var exportButton: MaterialButton
    private lateinit var historyButton: MaterialButton
    private lateinit var startLanguageSpinner: Spinner
    private lateinit var speakerSpinner: Spinner
    private lateinit var silenceToggleSwitch: SwitchMaterial
    private lateinit var silenceTimeoutSeekBar: SeekBar
    private lateinit var silenceTimeoutValueText: TextView
    private lateinit var noiseReductionSwitch: SwitchMaterial
    private lateinit var voiceEnglishSpinner: Spinner
    private lateinit var voiceSpanishSpinner: Spinner
    private lateinit var voiceFrenchSpinner: Spinner
    private lateinit var voiceGermanSpinner: Spinner
    private lateinit var conversationAdapter: ConversationAdapter
    private val conversationMessages = mutableListOf<ConversationMessage>()
    private val filteredMessages = mutableListOf<ConversationMessage>()
    private var currentSearchQuery: String = ""
    private var startLanguageOptions: List<LanguageOption> = emptyList()
    private var isUpdatingStartLanguage: Boolean = false
    private var speakerOptions: List<SpeakerOption> = emptyList()
    private var isUpdatingSpeaker: Boolean = false
    private var selectedSpeakerId: String = SPEAKER_A
    private var voiceOptionsByLanguage: Map<String, List<VoiceOption>> = emptyMap()
    private var selectedVoiceByLanguage: MutableMap<String, String> = mutableMapOf()
    private var isUpdatingVoiceSelection: Boolean = false
    private lateinit var preferences: SharedPreferences

    private var englishVoskModel: Model? = null
    private var spanishVoskModel: Model? = null
    private var frenchVoskModel: Model? = null
    private var germanVoskModel: Model? = null
    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var recognizer: Recognizer? = null
    private var noiseSuppressor: NoiseSuppressor? = null
    private var echoCanceler: AcousticEchoCanceler? = null
    private var automaticGainControl: AutomaticGainControl? = null
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
    private var silenceTimeoutMs: Long = DEFAULT_SILENCE_TIMEOUT_MS
    private var isSilenceTimeoutEnabled: Boolean = true
    private var isNoiseReductionEnabled: Boolean = false
    private var smoothedMicLevel: Double = 0.0
    private var peakMicLevel: Double = 0.0
    private var lastPeakTimestamp: Long = 0L

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

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        subtitleTextView = findViewById(R.id.subtitle)
        conversationRecyclerView = findViewById(R.id.conversation_recycler_view)
        emptyStateTextView = findViewById(R.id.empty_state_text)
        searchInput = findViewById(R.id.search_input)
        statusTextView = findViewById(R.id.status_text)
        listeningIndicatorIcon = findViewById(R.id.listening_indicator_icon)
        micLevelMeter = findViewById(R.id.mic_level_meter)
        micLevelMeter.max = 100
        micLevelValueText = findViewById(R.id.mic_level_value)
        modelDownloadProgress = findViewById(R.id.model_download_progress)
        modelSizeTextView = findViewById(R.id.model_size_text)
        toggleListeningButton = findViewById(R.id.toggle_listening_button)
        clearButton = findViewById(R.id.clear_button)
        languageToggleButton = findViewById(R.id.language_toggle_button)
        swapLanguageButton = findViewById(R.id.swap_language_button)
        exportButton = findViewById(R.id.export_button)
        historyButton = findViewById(R.id.history_button)
        startLanguageSpinner = findViewById(R.id.start_language_spinner)
        speakerSpinner = findViewById(R.id.speaker_spinner)
        silenceToggleSwitch = findViewById(R.id.silence_toggle_switch)
        silenceTimeoutSeekBar = findViewById(R.id.silence_timeout_seekbar)
        silenceTimeoutValueText = findViewById(R.id.silence_timeout_value)
        noiseReductionSwitch = findViewById(R.id.noise_reduction_switch)
        voiceEnglishSpinner = findViewById(R.id.voice_english_spinner)
        voiceSpanishSpinner = findViewById(R.id.voice_spanish_spinner)
        voiceFrenchSpinner = findViewById(R.id.voice_french_spinner)
        voiceGermanSpinner = findViewById(R.id.voice_german_spinner)

        conversationAdapter = ConversationAdapter(filteredMessages)
        conversationRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationRecyclerView.adapter = conversationAdapter

        toggleListeningButton.setOnClickListener { toggleListening() }
        clearButton.setOnClickListener { clearConversation() }
        languageToggleButton.setOnClickListener { cycleLanguageMode() }
        swapLanguageButton.setOnClickListener { swapCurrentLanguage() }
        exportButton.setOnClickListener { exportConversation() }
        historyButton.setOnClickListener { showExportHistory() }
        setupStartLanguageSpinner()
        setupSpeakerSpinner()
        setupSearchInput()
        setupSilenceControls()
        setupNoiseReductionToggle()

        LibVosk.setLogLevel(LogLevel.INFO)

        tts = TextToSpeech(this, this)
        languageIdentifier = LanguageIdentification.getClient()

        initializeTranslators()
        downloadTranslationModels()
        updateModeUi()
        updateListeningUi()
        updateEmptyState()
        updateModelDownloadUi()
        updateModelSizeText()
        showOnboardingIfNeeded()

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
                if (position < 0 || position >= startLanguageOptions.size) {
                    return
                }
                val selected = startLanguageOptions[position]
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

    private fun setupSearchInput() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                applySearchFilter(s?.toString() ?: "")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No-op
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No-op
            }
        })
    }

    private fun setupSpeakerSpinner() {
        speakerOptions = listOf(
            SpeakerOption(SPEAKER_A, getString(R.string.speaker_a)),
            SpeakerOption(SPEAKER_B, getString(R.string.speaker_b))
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, speakerOptions.map { it.label })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        speakerSpinner.adapter = adapter
        selectedSpeakerId = preferences.getString(PREF_SPEAKER, SPEAKER_A) ?: SPEAKER_A
        val selectedIndex = speakerOptions.indexOfFirst { it.id == selectedSpeakerId }.takeIf { it >= 0 } ?: 0
        isUpdatingSpeaker = true
        speakerSpinner.setSelection(selectedIndex, false)
        isUpdatingSpeaker = false
        speakerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isUpdatingSpeaker) {
                    return
                }
                if (position < 0 || position >= speakerOptions.size) {
                    return
                }
                selectedSpeakerId = speakerOptions[position].id
                preferences.edit().putString(PREF_SPEAKER, selectedSpeakerId).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No-op
            }
        }
    }

    private fun setupSilenceControls() {
        isSilenceTimeoutEnabled = preferences.getBoolean(PREF_SILENCE_ENABLED, true)
        silenceToggleSwitch.isChecked = isSilenceTimeoutEnabled
        silenceToggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            isSilenceTimeoutEnabled = isChecked
            preferences.edit().putBoolean(PREF_SILENCE_ENABLED, isChecked).apply()
            silenceTimeoutSeekBar.isEnabled = isChecked
            if (!isChecked) {
                silenceHandler.removeCallbacks(silenceTimeoutRunnable)
            } else if (isListening) {
                resetSilenceTimeout()
            }
        }

        silenceTimeoutMs = preferences.getLong(PREF_SILENCE_TIMEOUT_MS, DEFAULT_SILENCE_TIMEOUT_MS)
            .coerceIn(MIN_SILENCE_TIMEOUT_MS, MAX_SILENCE_TIMEOUT_MS)
        silenceTimeoutSeekBar.max = ((MAX_SILENCE_TIMEOUT_MS - MIN_SILENCE_TIMEOUT_MS) / SILENCE_STEP_MS).toInt()
        silenceTimeoutSeekBar.progress = ((silenceTimeoutMs - MIN_SILENCE_TIMEOUT_MS) / SILENCE_STEP_MS).toInt()
        silenceTimeoutValueText.text = formatSilenceTimeout(silenceTimeoutMs)
        silenceTimeoutSeekBar.isEnabled = isSilenceTimeoutEnabled
        silenceTimeoutSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                silenceTimeoutMs = MIN_SILENCE_TIMEOUT_MS + progress * SILENCE_STEP_MS
                silenceTimeoutValueText.text = formatSilenceTimeout(silenceTimeoutMs)
                preferences.edit().putLong(PREF_SILENCE_TIMEOUT_MS, silenceTimeoutMs).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No-op
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No-op
            }
        })
    }

    private fun setupNoiseReductionToggle() {
        val effectsAvailable = NoiseSuppressor.isAvailable() || AcousticEchoCanceler.isAvailable() || AutomaticGainControl.isAvailable()
        if (!effectsAvailable) {
            noiseReductionSwitch.isEnabled = false
            noiseReductionSwitch.isChecked = false
            return
        }
        isNoiseReductionEnabled = preferences.getBoolean(PREF_NOISE_REDUCTION, false)
        noiseReductionSwitch.isChecked = isNoiseReductionEnabled
        noiseReductionSwitch.setOnCheckedChangeListener { _, isChecked ->
            isNoiseReductionEnabled = isChecked
            preferences.edit().putBoolean(PREF_NOISE_REDUCTION, isChecked).apply()
            if (isListening) {
                stopListening()
                startListening()
            }
        }
    }

    private fun swapCurrentLanguage() {
        if (startLanguageOptions.size < 2) {
            return
        }
        val currentIndex = startLanguageOptions.indexOfFirst { it.code == currentListeningLanguage }
        val nextIndex = if (currentIndex == 0) 1 else 0
        currentListeningLanguage = startLanguageOptions.getOrNull(nextIndex)?.code ?: currentListeningLanguage
        syncStartLanguageSelection()
        if (isListening) {
            stopListening()
        }
        statusTextView.text = getString(R.string.status_ready)
    }

    private fun showExportHistory() {
        val history = loadExportHistory()
        if (history.isEmpty()) {
            Toast.makeText(this, getString(R.string.export_history_empty), Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.export_history_title))
            .setMessage(history.joinToString("\n"))
            .setPositiveButton(getString(R.string.close), null)
            .show()
    }

    private fun showOnboardingIfNeeded() {
        if (preferences.getBoolean(PREF_ONBOARDING_SHOWN, false)) {
            return
        }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.onboarding_title))
            .setMessage(getString(R.string.onboarding_message))
            .setPositiveButton(getString(R.string.onboarding_cta)) { _, _ ->
                preferences.edit().putBoolean(PREF_ONBOARDING_SHOWN, true).apply()
            }
            .setCancelable(false)
            .show()
    }

    private fun loadExportHistory(): MutableList<String> {
        val raw = preferences.getString(PREF_EXPORT_HISTORY, "") ?: ""
        if (raw.isBlank()) {
            return mutableListOf()
        }
        return raw.split(EXPORT_HISTORY_SEPARATOR).filter { it.isNotBlank() }.toMutableList()
    }

    private fun saveExportHistory(history: List<String>) {
        val serialized = history.joinToString(EXPORT_HISTORY_SEPARATOR)
        preferences.edit().putString(PREF_EXPORT_HISTORY, serialized).apply()
    }

    private fun addExportHistoryEntry(fileName: String) {
        val history = loadExportHistory()
        val timestamp = SimpleDateFormat("MMM dd, HH:mm", Locale.US).format(Date())
        history.add(0, "$timestamp • $fileName")
        if (history.size > MAX_EXPORT_HISTORY) {
            history.subList(MAX_EXPORT_HISTORY, history.size).clear()
        }
        saveExportHistory(history)
    }

    private fun updateModelDownloadUi() {
        val isLoading = pendingModelDownloads > 0 || pendingSpeechModelLoads > 0
        modelDownloadProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun updateModelSizeText() {
        if (!areSpeechModelsReady()) {
            modelSizeTextView.text = getString(R.string.model_size_loading)
            return
        }
        val entries = listOf(
            Pair("EN", getModelSizeMb(ENGLISH_MODEL_STORAGE_DIR)),
            Pair("ES", getModelSizeMb(SPANISH_MODEL_STORAGE_DIR)),
            Pair("FR", getModelSizeMb(FRENCH_MODEL_STORAGE_DIR)),
            Pair("DE", getModelSizeMb(GERMAN_MODEL_STORAGE_DIR))
        )
        val parts = entries.mapNotNull { (label, size) ->
            size?.let { "$label ${it}MB" }
        }
        modelSizeTextView.text = if (parts.isEmpty()) {
            getString(R.string.model_size_unknown)
        } else {
            getString(R.string.model_size_prefix) + " " + parts.joinToString(" • ")
        }
    }

    private fun getModelSizeMb(directoryName: String): Long? {
        val dir = resolveModelDir(directoryName) ?: return null
        val sizeBytes = calculateDirectorySize(dir)
        return if (sizeBytes > 0) sizeBytes / (1024 * 1024) else null
    }

    private fun resolveModelDir(directoryName: String): File? {
        val candidates = listOf(
            File(filesDir, directoryName),
            getExternalFilesDir(null)?.let { File(it, directoryName) },
            File(cacheDir, directoryName)
        ).filterNotNull()
        return candidates.firstOrNull { it.exists() }
    }

    private fun calculateDirectorySize(directory: File): Long {
        if (!directory.exists()) {
            return 0L
        }
        if (directory.isFile) {
            return directory.length()
        }
        return directory.listFiles()?.sumOf { calculateDirectorySize(it) } ?: 0L
    }

    private fun applySearchFilter(query: String) {
        currentSearchQuery = query.trim()
        filteredMessages.clear()
        if (currentSearchQuery.isBlank()) {
            filteredMessages.addAll(conversationMessages)
        } else {
            filteredMessages.addAll(
                conversationMessages.filter { it.text.contains(currentSearchQuery, ignoreCase = true) }
            )
        }
        conversationAdapter.notifyDataSetChanged()
        updateEmptyState()
        if (filteredMessages.isNotEmpty()) {
            conversationRecyclerView.scrollToPosition(filteredMessages.size - 1)
        }
    }

    private fun formatSilenceTimeout(timeoutMs: Long): String {
        return getString(R.string.silence_timeout_value, timeoutMs / 1000.0)
    }

    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    }

    private fun clearConversation() {
        if (isListening) {
            stopListening()
        }
        conversationMessages.clear()
        filteredMessages.clear()
        conversationAdapter.notifyDataSetChanged()
        currentSearchQuery = ""
        searchInput.setText("")
        updateEmptyState()
        currentListeningLanguage = "en"
        syncStartLanguageSelection()
        statusTextView.text = getString(R.string.status_ready)
    }

    private fun downloadTranslationModels() {
        statusTextView.text = getString(R.string.status_downloading)
        updateModelDownloadUi()
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
        updateModelDownloadUi()
        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                Log.d(TAG, "$label model downloaded")
                pendingModelDownloads -= 1
                updateModelDownloadUi()
                updateStatusIfReady()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error downloading $label model", exception)
                pendingModelDownloads -= 1
                updateModelDownloadUi()
                statusTextView.text = getString(R.string.status_error_models)
                addSystemMessage(getString(R.string.status_error_models))
            }
    }

    private fun updateStatusIfReady() {
        updateModelDownloadUi()
        if (pendingModelDownloads == 0 && pendingSpeechModelLoads == 0 && areSpeechModelsReady() && !isListening) {
            statusTextView.text = getString(R.string.status_ready)
        }
        if (areSpeechModelsReady()) {
            updateModelSizeText()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS language not supported or missing data")
            }
            initializeVoiceSelectors()
        } else {
            Log.e(TAG, "TTS initialization failed")
        }
    }

    private fun initializeVoiceSelectors() {
        val voices = tts?.voices?.toList() ?: emptyList()
        voiceOptionsByLanguage = mapOf(
            "en" to buildVoiceOptions(voices, "en"),
            "es" to buildVoiceOptions(voices, "es"),
            "fr" to buildVoiceOptions(voices, "fr"),
            "de" to buildVoiceOptions(voices, "de")
        )
        setupVoiceSpinner(voiceEnglishSpinner, "en", PREF_VOICE_EN)
        setupVoiceSpinner(voiceSpanishSpinner, "es", PREF_VOICE_ES)
        setupVoiceSpinner(voiceFrenchSpinner, "fr", PREF_VOICE_FR)
        setupVoiceSpinner(voiceGermanSpinner, "de", PREF_VOICE_DE)
    }

    private fun buildVoiceOptions(voices: List<Voice>, languageCode: String): List<VoiceOption> {
        val filtered = voices.filter { it.locale.language == languageCode }
        if (filtered.isEmpty()) {
            return listOf(VoiceOption(VOICE_DEFAULT, getString(R.string.voice_default)))
        }
        return filtered.map { voice ->
            val label = "${voice.locale.displayName} • ${voice.name}"
            VoiceOption(voice.name, label)
        }
    }

    private fun setupVoiceSpinner(spinner: Spinner, languageCode: String, prefKey: String) {
        val options = voiceOptionsByLanguage[languageCode] ?: listOf(VoiceOption(VOICE_DEFAULT, getString(R.string.voice_default)))
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options.map { it.label })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.isEnabled = options.size > 1

        val savedVoice = preferences.getString(prefKey, null)
        val selectedIndex = options.indexOfFirst { it.name == savedVoice }.takeIf { it >= 0 } ?: 0
        isUpdatingVoiceSelection = true
        spinner.setSelection(selectedIndex, false)
        isUpdatingVoiceSelection = false
        selectedVoiceByLanguage[languageCode] = options.getOrNull(selectedIndex)?.name ?: VOICE_DEFAULT

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isUpdatingVoiceSelection) {
                    return
                }
                if (position < 0 || position >= options.size) {
                    return
                }
                val selectedVoice = options[position].name
                selectedVoiceByLanguage[languageCode] = selectedVoice
                preferences.edit().putString(prefKey, selectedVoice).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No-op
            }
        }
    }

    private fun applyVoiceForLanguage(languageCode: String) {
        tts?.language = localeForLanguage(languageCode)
        val selectedVoice = selectedVoiceByLanguage[languageCode] ?: VOICE_DEFAULT
        if (selectedVoice.isNotBlank() && selectedVoice != VOICE_DEFAULT) {
            val voice = tts?.voices?.firstOrNull { it.name == selectedVoice }
            if (voice != null) {
                tts?.voice = voice
            }
        }
    }

    private fun speakOut(text: String, languageCode: String) {
        applyVoiceForLanguage(languageCode)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun initVoskModels() {
        statusTextView.text = getString(R.string.status_loading)
        pendingSpeechModelLoads = 0
        updateModelDownloadUi()
        unpackSpeechModel(ENGLISH_MODEL_ASSET_PATH, ENGLISH_MODEL_STORAGE_DIR, "en")
        unpackSpeechModel(SPANISH_MODEL_ASSET_PATH, SPANISH_MODEL_STORAGE_DIR, "es")
        unpackSpeechModel(FRENCH_MODEL_ASSET_PATH, FRENCH_MODEL_STORAGE_DIR, "fr")
        unpackSpeechModel(GERMAN_MODEL_ASSET_PATH, GERMAN_MODEL_STORAGE_DIR, "de")
    }

    private fun unpackSpeechModel(assetPath: String, destinationDir: String, languageCode: String) {
        pendingSpeechModelLoads += 1
        updateModelDownloadUi()
        StorageService.unpack(this, assetPath, destinationDir,
            { unpackedModel ->
                when (languageCode) {
                    "en" -> englishVoskModel = unpackedModel
                    "es" -> spanishVoskModel = unpackedModel
                    "fr" -> frenchVoskModel = unpackedModel
                    "de" -> germanVoskModel = unpackedModel
                }
                pendingSpeechModelLoads -= 1
                updateModelDownloadUi()
                updateListeningUi()
                updateStatusIfReady()
            },
            { exception ->
                pendingSpeechModelLoads -= 1
                updateModelDownloadUi()
                val languageLabel = getLanguageLabel(languageCode)
                Log.e(TAG, "Failed to unpack $languageLabel model", exception)
                setErrorState(getString(R.string.status_error_speech_model, languageLabel))
            })
    }

    private fun areSpeechModelsReady(): Boolean {
        return englishVoskModel != null && spanishVoskModel != null && frenchVoskModel != null && germanVoskModel != null
    }

    private fun getSpeechModelForLanguage(languageCode: String): Model? {
        return when (languageCode) {
            "es" -> spanishVoskModel
            "fr" -> frenchVoskModel
            "de" -> germanVoskModel
            else -> englishVoskModel
        }
    }

    private fun toggleListening() {
        if (isListening) {
            stopListening()
        } else {
            startListening()
        }
    }

    private fun startListening() {
        val model = getSpeechModelForLanguage(currentListeningLanguage)
        if (model == null) {
            val languageLabel = getLanguageLabel(currentListeningLanguage)
            setErrorState(getString(R.string.status_error_speech_model, languageLabel))
            return
        }
        try {
            isListening = true
            startAudioRecognition(model)
            updateListeningUi()
            statusTextView.text = getString(R.string.status_listening)
            resetSilenceTimeout()
        } catch (e: Exception) {
            isListening = false
            setErrorState(e.message ?: getString(R.string.status_error_occurred))
        }
    }

    private fun startAudioRecognition(model: Model) {
        stopAudioRecognition()
        val sampleRate = 16000
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            throw IOException("Invalid audio buffer size")
        }
        val source = if (isNoiseReductionEnabled) {
            MediaRecorder.AudioSource.VOICE_RECOGNITION
        } else {
            MediaRecorder.AudioSource.MIC
        }
        val recorder = AudioRecord(
            source,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            throw IOException("Audio recorder init failed")
        }
        audioRecord = recorder
        setupAudioEffects(recorder)
        recognizer = Recognizer(model, sampleRate.toFloat())
        recorder.startRecording()
        val buffer = ByteArray(bufferSize)
        recordingThread = Thread {
            while (isListening && !Thread.currentThread().isInterrupted) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read <= 0) {
                    continue
                }
                updateMicLevel(buffer, read)
                val hasFinal = recognizer?.acceptWaveForm(buffer, read) ?: false
                if (hasFinal) {
                    val resultJson = recognizer?.result ?: ""
                    val recognizedText = parseResultText(resultJson)
                    if (recognizedText.isNotBlank()) {
                        runOnUiThread { handleRecognitionText(recognizedText) }
                        break
                    }
                }
            }
        }
        recordingThread?.start()
    }

    private fun stopListening(updateStatus: Boolean = true) {
        isListening = false
        stopAudioRecognition()
        silenceHandler.removeCallbacks(silenceTimeoutRunnable)
        updateListeningUi()
        if (updateStatus) {
            statusTextView.text = getString(R.string.status_ready)
        }
    }

    private fun stopAudioRecognition() {
        recordingThread?.interrupt()
        recordingThread = null
        audioRecord?.let { recorder ->
            try {
                if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    recorder.stop()
                }
            } catch (e: IllegalStateException) {
                Log.w(TAG, "AudioRecord stop error", e)
            }
            recorder.release()
        }
        audioRecord = null
        try {
            recognizer?.close()
        } catch (e: Exception) {
            Log.w(TAG, "Recognizer close error", e)
        }
        recognizer = null
        releaseAudioEffects()
        micLevelMeter.progress = 0
        micLevelMeter.secondaryProgress = 0
        smoothedMicLevel = 0.0
        peakMicLevel = 0.0
        lastPeakTimestamp = 0L
        micLevelValueText.text = getString(R.string.db_placeholder)
    }

    private fun handleRecognitionText(recognizedText: String) {
        stopListening(false)
        statusTextView.text = getString(R.string.status_processing)
        if (currentMode == ConversationMode.AUTO) {
            identifyAndTranslate(recognizedText)
        } else {
            addUserMessage(recognizedText, currentListeningLanguage)
            translateWithOverride(recognizedText, currentListeningLanguage)
        }
    }

    private fun parseResultText(resultJson: String): String {
        return try {
            val json = JSONObject(resultJson)
            json.optString("text", "").trim()
        } catch (e: Exception) {
            ""
        }
    }

    private fun updateMicLevel(buffer: ByteArray, length: Int) {
        val rms = calculateRms(buffer, length)
        val rawLevel = (rms / MAX_PCM_AMPLITUDE * 100.0).coerceIn(0.0, 100.0)
        smoothedMicLevel = if (smoothedMicLevel == 0.0) {
            rawLevel
        } else {
            METER_SMOOTHING_ALPHA * rawLevel + (1 - METER_SMOOTHING_ALPHA) * smoothedMicLevel
        }
        val now = System.currentTimeMillis()
        if (rawLevel > MIC_LEVEL_ACTIVITY_THRESHOLD) {
            resetSilenceTimeout()
        }
        if (rawLevel >= peakMicLevel) {
            peakMicLevel = rawLevel
            lastPeakTimestamp = now
        } else if (now - lastPeakTimestamp > PEAK_HOLD_MS) {
            peakMicLevel = max(rawLevel, peakMicLevel * PEAK_DECAY_RATE)
        }
        val dbValue = if (rms <= 0.0) {
            MIN_DB
        } else {
            (20 * log10(rms / MAX_PCM_AMPLITUDE)).coerceIn(MIN_DB, 0.0)
        }
        runOnUiThread {
            micLevelMeter.progress = smoothedMicLevel.toInt()
            micLevelMeter.secondaryProgress = peakMicLevel.toInt()
            micLevelValueText.text = getString(R.string.db_format, dbValue)
        }
    }

    private fun calculateRms(buffer: ByteArray, length: Int): Double {
        var sum = 0.0
        val sampleCount = length / 2
        if (sampleCount == 0) {
            return 0.0
        }
        var i = 0
        while (i < sampleCount) {
            val index = i * 2
            val low = buffer[index].toInt() and 0xFF
            val high = buffer[index + 1].toInt()
            val sample = (high shl 8) or low
            val value = if (sample > 32767) sample - 65536 else sample
            sum += value.toDouble() * value.toDouble()
            i += 1
        }
        return sqrt(sum / sampleCount)
    }

    private fun setupAudioEffects(recorder: AudioRecord) {
        releaseAudioEffects()
        if (!isNoiseReductionEnabled) {
            return
        }
        val sessionId = recorder.audioSessionId
        if (NoiseSuppressor.isAvailable()) {
            noiseSuppressor = NoiseSuppressor.create(sessionId)
            noiseSuppressor?.enabled = true
        }
        if (AcousticEchoCanceler.isAvailable()) {
            echoCanceler = AcousticEchoCanceler.create(sessionId)
            echoCanceler?.enabled = true
        }
        if (AutomaticGainControl.isAvailable()) {
            automaticGainControl = AutomaticGainControl.create(sessionId)
            automaticGainControl?.enabled = true
        }
    }

    private fun releaseAudioEffects() {
        noiseSuppressor?.release()
        noiseSuppressor = null
        echoCanceler?.release()
        echoCanceler = null
        automaticGainControl?.release()
        automaticGainControl = null
    }

    private fun resetSilenceTimeout() {
        silenceHandler.removeCallbacks(silenceTimeoutRunnable)
        if (!isSilenceTimeoutEnabled) {
            return
        }
        silenceHandler.postDelayed(silenceTimeoutRunnable, silenceTimeoutMs)
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
                    speakOut(translatedText, targetLanguage)
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
        updateSwapLanguageUi()
    }

    private fun updateSwapLanguageUi() {
        swapLanguageButton.isEnabled = !isListening && areSpeechModelsReady() && startLanguageOptions.size > 1
    }

    private fun updateListeningUi() {
        val modelsReady = areSpeechModelsReady()
        toggleListeningButton.isEnabled = modelsReady
        if (isListening) {
            toggleListeningButton.text = getString(R.string.stop_listening)
            toggleListeningButton.setIconResource(R.drawable.ic_clear)
        } else {
            toggleListeningButton.text = getString(R.string.start_listening)
            toggleListeningButton.setIconResource(R.drawable.ic_mic)
        }
        val indicatorColor = if (isListening) R.color.listening_active else R.color.listening_idle
        listeningIndicatorIcon.setColorFilter(ContextCompat.getColor(this, indicatorColor))
        startLanguageSpinner.isEnabled = !isListening && modelsReady
        speakerSpinner.isEnabled = !isListening
        noiseReductionSwitch.isEnabled = !isListening
        val voiceEnabled = !isListening
        voiceEnglishSpinner.isEnabled = voiceEnabled
        voiceSpanishSpinner.isEnabled = voiceEnabled
        voiceFrenchSpinner.isEnabled = voiceEnabled
        voiceGermanSpinner.isEnabled = voiceEnabled
        micLevelMeter.visibility = View.VISIBLE
        micLevelValueText.visibility = View.VISIBLE
        if (!isListening) {
            micLevelMeter.progress = 0
            micLevelMeter.secondaryProgress = 0
            micLevelValueText.text = getString(R.string.db_placeholder)
        }
        updateSwapLanguageUi()
    }

    private fun addUserMessage(text: String, languageCode: String) {
        val speakerLabel = speakerOptions.firstOrNull { it.id == selectedSpeakerId }?.label ?: getString(R.string.speaker_a)
        val message = "${getLanguageFlag(languageCode)} ${getString(R.string.message_speaker, speakerLabel, text)}"
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
        conversationMessages.add(ConversationMessage(text, type, System.currentTimeMillis()))
        applySearchFilter(currentSearchQuery)
    }

    private fun updateEmptyState() {
        val isSearching = currentSearchQuery.isNotBlank()
        val isEmpty = filteredMessages.isEmpty()
        emptyStateTextView.text = if (isSearching) {
            getString(R.string.search_empty_state)
        } else {
            getString(R.string.empty_state)
        }
        if (isEmpty) {
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

    private fun getLanguageLabel(languageCode: String): String {
        return when (languageCode) {
            "es" -> getString(R.string.language_label_spanish)
            "fr" -> getString(R.string.language_label_french)
            "de" -> getString(R.string.language_label_german)
            else -> getString(R.string.language_label_english)
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
        addExportHistoryEntry(fileName)
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
        stopAudioRecognition()
        englishVoskModel?.close()
        spanishVoskModel?.close()
        frenchVoskModel?.close()
        germanVoskModel?.close()
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

    private data class SpeakerOption(val id: String, val label: String)

    private data class VoiceOption(val name: String, val label: String)

    private data class ConversationMessage(val text: String, val type: MessageType, val timestamp: Long)

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

    private inner class ConversationAdapter(
        private val items: List<ConversationMessage>
    ) : RecyclerView.Adapter<ConversationAdapter.MessageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message, parent, false)
            return MessageViewHolder(view)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val item = items[position]
            holder.messageText.text = item.text
            holder.messageTime.text = formatTimestamp(item.timestamp)
            val backgroundRes = when (item.type) {
                MessageType.USER -> R.drawable.bg_message_user
                MessageType.TRANSLATION -> R.drawable.bg_message_translation
                MessageType.SYSTEM -> R.drawable.bg_message_system
            }
            holder.messageText.setBackgroundResource(backgroundRes)
            val textColorRes = when (item.type) {
                MessageType.USER -> R.color.text_color
                MessageType.TRANSLATION -> R.color.text_color
                MessageType.SYSTEM -> R.color.text_secondary
            }
            holder.messageText.setTextColor(ContextCompat.getColor(holder.itemView.context, textColorRes))
        }

        override fun getItemCount(): Int = items.size

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val messageText: TextView = view.findViewById(R.id.message_text)
            val messageTime: TextView = view.findViewById(R.id.message_time)
        }
    }

    companion object {
        private const val RECORD_AUDIO_PERMISSION_CODE = 1
        private const val TAG = "OfflineTranslatorApp"
        private const val DEFAULT_SILENCE_TIMEOUT_MS = 1500L
        private const val MIN_SILENCE_TIMEOUT_MS = 500L
        private const val MAX_SILENCE_TIMEOUT_MS = 4000L
        private const val SILENCE_STEP_MS = 250L
        private const val RESTART_DELAY_MS = 3000L
        private const val ENGLISH_MODEL_ASSET_PATH = "model-en"
        private const val SPANISH_MODEL_ASSET_PATH = "model-es"
        private const val FRENCH_MODEL_ASSET_PATH = "model-fr"
        private const val GERMAN_MODEL_ASSET_PATH = "model-de"
        private const val ENGLISH_MODEL_STORAGE_DIR = "model-en"
        private const val SPANISH_MODEL_STORAGE_DIR = "model-es"
        private const val FRENCH_MODEL_STORAGE_DIR = "model-fr"
        private const val GERMAN_MODEL_STORAGE_DIR = "model-de"
        private const val PREFS_NAME = "offline_translator_prefs"
        private const val PREF_SPEAKER = "pref_speaker"
        private const val PREF_SILENCE_TIMEOUT_MS = "pref_silence_timeout_ms"
        private const val PREF_SILENCE_ENABLED = "pref_silence_enabled"
        private const val PREF_NOISE_REDUCTION = "pref_noise_reduction"
        private const val PREF_EXPORT_HISTORY = "pref_export_history"
        private const val PREF_ONBOARDING_SHOWN = "pref_onboarding_shown"
        private const val PREF_VOICE_EN = "pref_voice_en"
        private const val PREF_VOICE_ES = "pref_voice_es"
        private const val PREF_VOICE_FR = "pref_voice_fr"
        private const val PREF_VOICE_DE = "pref_voice_de"
        private const val EXPORT_HISTORY_SEPARATOR = "||"
        private const val MAX_EXPORT_HISTORY = 10
        private const val MAX_PCM_AMPLITUDE = 32768.0
        private const val MIC_LEVEL_ACTIVITY_THRESHOLD = 6.0
        private const val METER_SMOOTHING_ALPHA = 0.2
        private const val PEAK_HOLD_MS = 2000L
        private const val PEAK_DECAY_RATE = 0.9
        private const val MIN_DB = -60.0
        private const val VOICE_DEFAULT = "default"
        private const val SPEAKER_A = "A"
        private const val SPEAKER_B = "B"
    }
}

