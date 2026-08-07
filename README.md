# Puente Android App

A conversational offline translation Android app for face-to-face turn-taking. Puente recognizes speech on-device (Vosk), translates with ML Kit, and speaks the result aloud (Android TTS)—without requiring cloud connectivity after the first model download.

Supports **Auto English ↔ Spanish**, plus manual **English ↔ Spanish**, **English ↔ French**, and **English ↔ German**.

## Features

- **Offline speech recognition**: Vosk models for English, Spanish, French, and German
- **Offline translation**: Google ML Kit Translation for EN↔ES, EN↔FR, and EN↔DE
- **Text-to-speech**: Android TTS with per-language voice preferences
- **Language modes**: Auto detect (EN↔ES), or manual pairs with start language and quick swap
- **Conversation transcript**: Searchable history with timestamps, favorites, and confidence scores
- **Push-to-talk**: Optional PTT, silence auto-stop, mic level meter, and noise reduction
- **Export**: TXT / PDF / CSV export with share and export history
- **Phrasebook**: Offline English/Spanish phrases
- **Model Manager**: Simple (Vosk) or advanced (Vosk + ML Kit) model controls
- **Speaker profiles**: Speaker A/B labeling for shared-device use
- **Modern UI**: Material Design single-activity interface

For a full capability list, see the [Feature Overview](FEATURE_OVERVIEW.md).  
Website-ready product copy: [marketing/PRODUCT_OVERVIEW.md](marketing/PRODUCT_OVERVIEW.md).  
Release history: [CHANGELOG.md](CHANGELOG.md).

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
- Android SDK 21 (Android 5.0) or higher
- Java 17 or Kotlin support
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
1. Connect an Android device or start an emulator
2. Click "Run" or press Shift+F10
3. The app will install and launch on your device

## Usage

### First Launch
1. Grant microphone permission when prompted
2. Complete onboarding (default language mode)
3. Wait for the app to download ML Kit translation models (internet required once)
4. Vosk speech models unpack automatically from assets

### Starting a Conversation
1. Choose a language mode (Auto, EN↔ES, EN↔FR, or EN↔DE)
2. Tap "Start Listening" (or use push-to-talk if enabled)
3. Speak in one of the supported languages for that mode
4. The app will recognize speech, translate, speak the result, and continue the turn flow

### Controls
- **Start/Stop Listening**: Toggle voice recognition
- **Push-to-talk**: Hold-to-speak mode when enabled
- **Clear Conversation**: Clear the conversation history
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
- Models are downloaded on first use and cached locally

### Text-to-Speech
- Android native TTS engine
- Supports English, Spanish, French, and German locales
- Automatic language switching based on translation; optional per-language voice selection

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
├── marketing/
│   └── PRODUCT_OVERVIEW.md
├── gradle/
├── FEATURE_OVERVIEW.md
├── CHANGELOG.md
├── SETUP_GUIDE.md
├── README.md
├── gradlew
└── settings.gradle.kts
```

## Dependencies

The app uses the following key dependencies:

```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.alphacephei:vosk-android:0.3.75")
    implementation("com.google.mlkit:language-id:17.0.6")
    implementation("com.google.mlkit:translate:17.0.3")
}
```

## Permissions

The app requires the following permission:
- `RECORD_AUDIO`: For speech recognition functionality

## Troubleshooting

### Common Issues

1. **Speech Recognition Not Working**
   - Ensure microphone permission is granted
   - Check device microphone functionality
   - Verify Vosk model is properly unpacked

2. **Translation Not Working**
   - Ensure internet connection for initial model download
   - Check device storage space (models require ~100MB each)
   - Verify ML Kit services are available

3. **TTS Not Speaking**
   - Check device volume settings
   - Verify TTS engine is installed and configured
   - Ensure language packs are available

### Performance Optimization

- The app works best on devices with at least 2GB RAM
- First launch requires internet for downloading translation models
- Subsequent uses work completely offline
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

© unlovedproductions. All rights reserved. This application is proprietary software. Unauthorized copying, distribution, or modification is strictly prohibited.
