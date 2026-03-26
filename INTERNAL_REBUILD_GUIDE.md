# Internal Rebuild Guide — Offline Translator Android App

This document enables full reconstruction of the project if the repo is corrupted or deleted. **Do not rebuild now** — this is a blueprint for rebuilding.

---

## 1) Product Summary
An offline, conversational translator for **English ↔ Spanish** with optional **English ↔ French** and **English ↔ German** modes.
- Offline speech recognition: **Vosk**
- Offline translation: **ML Kit**
- TTS: Android **TextToSpeech**
- Auto mode (EN↔ES), manual modes (EN↔ES/EN↔FR/EN↔DE)
- Conversation history with search, favorites, confidence indicators
- Exports: TXT / PDF / CSV
- Model Manager (Vosk simple mode + ML Kit advanced mode)
- Push‑to‑talk toggle, noise reduction toggle, mic meter (smoothed + peak hold)

---

## 2) Tech Stack & Tooling
- **Language:** Kotlin
- **Target:** Android (minSdk 21)
- **Build system:** Gradle Kotlin DSL
- **UI:** XML layouts + Material 3
- **Speech recognition:** Vosk Android (com.alphacephei:vosk-android:0.3.75)
- **ML Kit:**
  - language-id: 17.0.6
  - translate: 17.0.3
- **Testing:**
  - Unit: JUnit 4
  - UI: Espresso + AndroidX Test

---

## 3) Directory Structure (Required)
```
/app/
├── app/
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/offlinetranslator/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── AppUtils.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── item_message.xml
│   │   │   │   │   ├── dialog_model_manager.xml
│   │   │   │   │   └── item_model_entry.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_mic.xml, ic_clear.xml, ic_star_outline.xml, ic_star_filled.xml, ic_info.xml
│   │   │   │   │   ├── bg_message_user.xml, bg_message_translation.xml, bg_message_system.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── xml/
│   │   │   │       └── file_paths.xml
│   │   │   ├── assets/
│   │   │   │   ├── model-en/
│   │   │   │   ├── model-es/
│   │   │   │   ├── model-fr/
│   │   │   │   └── model-de/
│   │   ├── test/java/com/example/offlinetranslator/AppUtilsTest.kt
│   │   └── androidTest/java/com/example/offlinetranslator/MainActivityUiTest.kt
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/libs.versions.toml
```

---

## 4) Required Dependencies
**app/build.gradle.kts** dependencies:
```kotlin
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("com.alphacephei:vosk-android:0.3.75")
implementation("com.google.mlkit:language-id:17.0.6")
implementation("com.google.mlkit:translate:17.0.3")

testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test:core:1.5.0")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test:rules:1.5.0")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
```

---

## 5) Android Manifest (Critical)
- Permissions:
  - `android.permission.RECORD_AUDIO`
  - `android.permission.WAKE_LOCK`
- FileProvider configured for exports

**FileProvider required files:**
- `res/xml/file_paths.xml` with `<files-path name="exports" path="." />`

---

## 6) Speech Models (Vosk) — Required Assets
Place the following under `app/src/main/assets/`:
- `model-en/` (English)
- `model-es/` (Spanish)
- `model-fr/` (French)
- `model-de/` (German)

Recommended downloads:
- EN: https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip
- ES: https://alphacephei.com/vosk/models/vosk-model-small-es-0.42.zip
- FR: https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip
- DE: https://alphacephei.com/vosk/models/vosk-model-small-de-0.15.zip

Unzip and rename folders to **model-en/model-es/model-fr/model-de**.

---

## 7) Core Features (MainActivity.kt)
Key systems to re‑implement:
1. **Audio pipeline**: AudioRecord → Vosk Recognizer → JSON parsing
2. **Confidence threshold**: fixed at `0.7`, prompts retry or “translate anyway”
3. **Mic meter**: RMS-based, smoothed + peak hold + dB readout
4. **Noise reduction**: toggle (NoiseSuppressor/AEC/AGC)
5. **Translation**: ML Kit translators for EN↔ES, EN↔FR, EN↔DE
6. **Conversation list**: RecyclerView with favorites + confidence
7. **Export**: TXT/PDF/CSV with FileProvider share
8. **Model Manager**: Vosk simple + ML Kit advanced
9. **Phrasebook**: EN/ES static dialog
10. **Push‑to‑talk**: toggle disables auto‑restart
11. **Wake lock**: active while listening

---

## 8) Tests (Rebuild Targets)
- **Unit tests:** `AppUtilsTest.kt` for RMS, storage size formatting, CSV escaping
- **UI tests:** `MainActivityUiTest.kt` for dialogs/buttons

Run:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

---

## 9) Build Steps (From Scratch)
1. Install Android Studio + SDK (API 34+)
2. Restore folder structure above
3. Add Vosk models into assets
4. Sync Gradle
5. Build/Run on device

---

## 10) Known Runtime Dependencies
- **Microphone permission** required
- **First run** may download ML Kit translation models (needs internet once)
- Device should have enough storage for Vosk models

---

## 11) Validation Checklist
- Start Listening works
- Translation output matches direction
- Confidence prompt appears for low-confidence phrases
- Export dialog creates TXT/PDF/CSV
- Model Manager loads entries and toggles advanced mode
- Phrasebook dialog opens
- Favorites filter works

---

## 12) Files You Must Preserve
- `MainActivity.kt`, `AppUtils.kt`
- All XML layouts (activity_main, item_message, dialog_model_manager, item_model_entry)
- `AndroidManifest.xml` and `file_paths.xml`
- Strings, colors, themes
- Vosk model assets

---

If you want, I can also generate a **step‑by‑step rebuild script** or a **checklist-based runbook** for your team.
