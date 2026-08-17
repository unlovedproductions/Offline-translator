# Internal Rebuild Guide — Puente Android App

Blueprint if the tree is deleted. Day-to-day restore steps: [RECOVERY.md](RECOVERY.md). **Do not rebuild from scratch unless recovery from git failed.**

---

## 1) Product Summary

Conversational translator for **English ↔ Spanish** (auto + manual) with optional **English ↔ French** and **English ↔ German**.

- Speech: **Vosk** (bundled models)
- Translation: **ML Kit** (download on Wi‑Fi, then on-device)
- TTS: Android **TextToSpeech** (on-device voices only)
- Transcript: dialog (`dialog_transcript.xml`) with search, favorites, confidence
- Exports: TXT / PDF / CSV under `files/exports/` via FileProvider
- Model Manager, phrasebook, PTT, noise reduction, mic meter

Application id: `com.puente.offline.translator`  
Namespace: `com.example.offlinetranslator`

---

## 2) Tech Stack & Tooling

- **Language:** Kotlin (JVM 17)
- **minSdk 23**, compile/target SDK **37**
- **AGP** 9.3.1, **Gradle** 9.7.0
- **UI:** XML layouts + Material
- **Speech:** `com.alphacephei:vosk-android:0.3.75`
- **ML Kit:** language-id 17.0.6, translate 17.0.3
- **Tests:** JUnit 4; Espresso + AndroidX Test

---

## 3) Directory Structure (Required)

```
app/
├── build.gradle.kts
├── proguard-rules.pro
└── src/
    ├── main/
    │   ├── AndroidManifest.xml
    │   ├── java/com/example/offlinetranslator/
    │   │   ├── MainActivity.kt
    │   │   └── AppUtils.kt
    │   ├── res/
    │   │   ├── layout/
    │   │   │   ├── activity_main.xml
    │   │   │   ├── dialog_transcript.xml
    │   │   │   ├── dialog_onboarding_language.xml
    │   │   │   ├── dialog_model_manager.xml
    │   │   │   ├── item_message.xml
    │   │   │   └── item_model_entry.xml
    │   │   ├── xml/
    │   │   │   ├── file_paths.xml
    │   │   │   ├── backup_rules.xml
    │   │   │   └── data_extraction_rules.xml
    │   │   ├── values/, drawable/, mipmap-*/
    │   └── assets/model-en/, model-es/, model-fr/, model-de/   # each needs uuid
    ├── test/.../AppUtilsTest.kt
    └── androidTest/.../MainActivityUiTest.kt, MainActivityE2ETest.kt
build.gradle.kts
settings.gradle.kts
gradle/wrapper/
LICENSE
NOTICE
keystore.properties.example
```

---

## 4) Required Dependencies

From current `app/build.gradle.kts`:

```kotlin
implementation("androidx.core:core-ktx:1.19.0")
implementation("androidx.appcompat:appcompat:1.7.1")
implementation("com.google.android.material:material:1.14.0")
implementation("androidx.constraintlayout:constraintlayout:2.2.2")
implementation("androidx.recyclerview:recyclerview:1.4.0")
implementation("com.alphacephei:vosk-android:0.3.75")
implementation("com.google.mlkit:language-id:17.0.6")
implementation("com.google.mlkit:translate:17.0.3")

testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test:core:1.7.0")
androidTestImplementation("androidx.test.ext:junit:1.3.0")
androidTestImplementation("androidx.test:rules:1.7.0")
androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
```

Release: `isMinifyEnabled = true`, `isShrinkResources = true`, `app/proguard-rules.pro`.  
Signing: optional `keystore.properties` at repo root (gitignored). Debug suffix: `.debug`.

---

## 5) Android Manifest (Critical)

- Permissions: `RECORD_AUDIO`, `WAKE_LOCK`
- `android:allowBackup="false"`
- `android:fullBackupContent="@xml/backup_rules"`
- `android:dataExtractionRules="@xml/data_extraction_rules"`
- FileProvider: `android:authorities="${applicationId}.fileprovider"`, `exported=false`, `grantUriPermissions=true`
- `res/xml/file_paths.xml`: `<files-path name="exports" path="exports/" />`

---

## 6) Speech Models (Vosk) — Required Assets

Under `app/src/main/assets/`: `model-en/`, `model-es/`, `model-fr/`, `model-de/`.

Each directory **must** include a `uuid` marker file (required by `StorageService.unpack()`).

Recommended upstream zips (unpack and rename folders):

- EN: https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip
- ES: https://alphacephei.com/vosk/models/vosk-model-small-es-0.42.zip
- FR: https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip
- DE: https://alphacephei.com/vosk/models/vosk-model-small-de-0.15.zip

Do not compress `fst`, `mdl`, `ie`, `mat`, `dubm` in `androidResources.noCompress`.

---

## 7) Core Features (MainActivity.kt)

1. AudioRecord → Vosk Recognizer → JSON
2. Confidence threshold ~0.7; retry or translate anyway
3. Mic meter (RMS, dB)
4. Noise reduction (NS/AEC/AGC)
5. ML Kit translators EN↔ES / EN↔FR / EN↔DE; `requireWifi()` for downloads
6. Transcript dialog RecyclerView (search, favorites, confidence)
7. Export TXT/PDF/CSV to `files/exports/` + FileProvider share; delete on clear
8. Model Manager (Vosk + advanced ML Kit) with privacy copy
9. Phrasebook EN/ES
10. Push-to-talk; wake lock while listening
11. On-device TTS voices only

---

## 8) Tests

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
```

- Unit: `AppUtilsTest.kt`
- UI/E2E: `MainActivityUiTest.kt`, `MainActivityE2ETest.kt` (transcript dialog)

---

## 9) Build Steps (From Scratch)

1. Android Studio + SDK **API 37**
2. Restore structure above + Vosk assets with `uuid`
3. `./gradlew :app:assembleDebug`
4. For Gumroad: keystore + `./gradlew :app:assembleRelease` ([RECOVERY.md](RECOVERY.md))

---

## 10) Known Runtime Dependencies

- Microphone permission
- First run: ML Kit packs over **Wi‑Fi**; Google may collect ML Kit utilization metrics
- Storage for Vosk unpack + translation models (~2GB recommended)

---

## 11) Validation Checklist

- Start Listening; EN↔ES turn-taking
- Transcript dialog: search, empty state, close
- Low-confidence prompt
- Export TXT/PDF/CSV (non-empty conversation)
- Model Manager + privacy text
- Phrasebook; favorites filter
- Edge-to-edge: title and bottom buttons clear system bars

---

## 12) Files You Must Preserve

- `MainActivity.kt`, `AppUtils.kt`, `proguard-rules.pro`
- Layouts including `dialog_transcript.xml` and `dialog_onboarding_language.xml`
- Manifest + `file_paths.xml` + backup XML
- Strings, colors, themes
- Vosk assets **including uuid**
- `LICENSE`, `NOTICE`, `keystore.properties.example`
