# Puente Android App

A conversational translation Android app for face-to-face turn-taking. Puente recognizes speech on-device (Vosk), translates with ML Kit, and speaks the result aloud (on-device Android TTS). Translation packs download from Google over **Wi‑Fi** on first use; after that, recognition and translation run locally. This is not an air-gapped product (see privacy below).

Supports **Auto English ↔ Spanish**, plus manual **English ↔ Spanish**, **English ↔ French**, and **English ↔ German**.

## Features

- **Offline speech recognition**: Vosk models for English, Spanish, French, and German
- **Offline translation**: Google ML Kit Translation for EN↔ES, EN↔FR, and EN↔DE
- **Text-to-speech**: Android TTS with per-language voice preferences
- **Language modes**: Auto detect (EN↔ES), or manual pairs with start language and quick swap
- **Conversation transcript**: Dialog (Transcript button) with search, timestamps, favorites, and confidence scores
- **Push-to-talk**: Optional PTT, silence auto-stop, mic level meter, and noise reduction
- **Export**: TXT / PDF / CSV export with share and export history
- **Phrasebook**: Offline English/Spanish phrases
- **Model Manager**: Simple (Vosk) or advanced (Vosk + ML Kit) model controls
- **Speaker profiles**: Speaker A/B labeling for shared-device use
- **Modern UI**: Material Design single-activity interface

For a full capability list, see the [Feature Overview](FEATURE_OVERVIEW.md).  
Website-ready product copy: [marketing/PRODUCT_OVERVIEW.md](marketing/PRODUCT_OVERVIEW.md).  
Gumroad listing: [marketing/GUMROAD_LISTING.md](marketing/GUMROAD_LISTING.md).  
Release history: [CHANGELOG.md](CHANGELOG.md).  
If the tree or keystore is lost: [RECOVERY.md](RECOVERY.md).

## Architecture

### Core Libraries
- **Vosk Android**: Offline speech recognition
- **Google ML Kit Translation**: Offline translation engine
- **Google ML Kit Language ID**: Language detection
- **Android TTS**: Text-to-speech synthesis

### Key Components
1. **MainActivity**: UI and orchestration
2. **Speech Recognition**: Vosk-based offline STT
3. **Translation Engine**: ML Kit translators with offline models
4. **Language Detection**: ML Kit language identification (Auto mode)
5. **Text-to-Speech**: Android native TTS with multi-language support

## Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 23 (Android 6.0) or higher (compile/target SDK 37)
- JDK 17 or later
- Minimum 2GB RAM for optimal performance
- Storage space for offline models (~2GB)

## Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Offline-translator
```

### 2. Open in Android Studio
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the `Offline-translator` directory
4. Click "OK"

### 3. Sync Project
Android Studio will automatically sync the project and download dependencies.

### 4. Speech Model Setup
1. English, Spanish, French, and German Vosk models are already included at:
   - `app/src/main/assets/model-en/`
   - `app/src/main/assets/model-es/`
   - `app/src/main/assets/model-fr/`
   - `app/src/main/assets/model-de/`
2. If you want to replace them, download newer models and swap those folders.

### 5. Build and Run
1. Connect an Android device or start an emulator (API 23+)
2. Click "Run" or `./gradlew :app:assembleDebug`
3. The debug app id is `com.puente.offline.translator.debug`

### Release build (Gumroad)

Do not sell debug or unsigned APKs. See [RECOVERY.md](RECOVERY.md) and `keystore.properties.example`.

```bash
./gradlew :app:assembleRelease
# signed APK: app/build/outputs/apk/release/app-release.apk
```

## Usage

### First Launch
1. Grant microphone permission when prompted
2. Complete onboarding (default language mode)
3. Wait for ML Kit translation models to download (**Wi‑Fi required** once)
4. Vosk speech models unpack automatically from assets (each `model-*` folder needs a `uuid` file)

### Starting a Conversation
1. Choose a language mode (Auto, EN↔ES, EN↔FR, or EN↔DE)
2. Tap "Start Listening" (or use push-to-talk if enabled)
3. Speak in one of the supported languages for that mode
4. The app will recognize speech, translate, speak the result, and continue the turn flow

### Controls
- **Start/Stop Listening**: Toggle voice recognition
- **Transcript**: Open searchable conversation history
- **Push-to-talk**: Hold-to-speak mode when enabled
- **Clear Conversation**: Clear history and delete local export files
- **Export**: Save or share as TXT, PDF, or CSV
- **Phrasebook / Model Manager**: Available from the main UI

## Technical Details

### Speech Recognition
- Uses Vosk speech models for English, Spanish, French, and German (`assets/model-en`, `model-es`, `model-fr`, `model-de`)
- Supports continuous recognition and push-to-talk
- Processes audio at 16kHz sample rate

### Translation
- Google ML Kit Translation API
- Offline models for English ↔ Spanish, English ↔ French, and English ↔ German
- Models download over Wi‑Fi on first use and are cached locally. Google may collect ML Kit utilization metrics ([ML Kit Terms](https://developers.google.com/ml-kit/terms)).

### Text-to-Speech
- Android native TTS engine
- Supports English, Spanish, French, and German locales
- Automatic language switching based on translation; per-language **on-device** voice selection only

## File Structure

```
Offline-translator/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/offlinetranslator/
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── values/
│   │   │   │   └── drawable/
│   │   │   ├── assets/
│   │   │   │   └── model-en/, model-es/, model-fr/, model-de/
│   │   │   └── AndroidManifest.xml
│   │   └── androidTest / test
│   └── build.gradle.kts
├── marketing/          # PRODUCT_OVERVIEW.md, GUMROAD_LISTING.md
├── gradle/
├── FEATURE_OVERVIEW.md
├── CHANGELOG.md
├── RECOVERY.md
├── SETUP_GUIDE.md
├── LICENSE
├── NOTICE
├── README.md
├── gradlew
└── settings.gradle.kts
```

## Dependencies

The app uses the following key dependencies:

```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.alphacephei:vosk-android:0.3.75")
    implementation("com.google.mlkit:language-id:17.0.6")
    implementation("com.google.mlkit:translate:17.0.3")
}
```

## Permissions

The app requires:
- `RECORD_AUDIO`: speech recognition
- `WAKE_LOCK`: keep listening sessions awake

Android backup of app data is disabled.

## Troubleshooting

### Common Issues

1. **Speech Recognition Not Working**
   - Ensure microphone permission is granted
   - Check device microphone functionality
   - Verify Vosk model is properly unpacked

2. **Translation Not Working**
   - Ensure **Wi‑Fi** for the initial ML Kit model download
   - Check device storage space (models require ~100MB each)
   - Verify ML Kit services are available

3. **TTS Not Speaking**
   - Check device volume settings
   - Verify TTS engine is installed and configured
   - Ensure language packs are available

### Performance Optimization

- The app works best on devices with at least 2GB RAM
- First launch requires **Wi‑Fi** for ML Kit translation models
- After that, recognition and translation run on-device (ML Kit may still send utilization metrics)
- Models are cached locally for faster access

## Future Enhancements

Potential improvements for future versions:

1. **More language pairs** beyond EN↔ES / EN↔FR / EN↔DE
2. **Custom / domain vocabulary** for specialized workflows
3. **On-demand model downloads** with clearer per-language progress
4. **Phrasebook search and audio playback**

## Support

For support, contact support@unlovedproductions.com

## License

Apache License 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
Buyers of the Gumroad APK are paying for the signed build and email support, not exclusive rights.
