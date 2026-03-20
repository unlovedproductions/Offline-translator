# Offline Translator Android App

A conversational English-Spanish translation Android application that works completely offline. The app uses speech recognition to detect spoken language, translates it to the target language, and speaks the translation aloud, creating a seamless conversation experience.


## Features

- **Offline Speech Recognition**: Uses Vosk speech recognition library for offline voice input
- **Offline Translation**: Utilizes Google ML Kit Translation API for offline English-Spanish translation
- **Text-to-Speech**: Built-in Android TTS for speaking translations
- **Language Detection**: Automatic detection of spoken language (English or Spanish)
- **Conversational Flow**: Automatically switches between languages for natural conversation
- **Modern UI**: Material Design 3 interface with intuitive controls

## Architecture

The application is built using the following components:

### Core Libraries
- **Vosk Android**: Offline speech recognition
- **Google ML Kit Translation**: Offline translation engine
- **Google ML Kit Language ID**: Language detection
- **Android TTS**: Text-to-speech synthesis

### Key Components
1. **MainActivity**: Main activity handling UI and orchestrating all components
2. **Speech Recognition**: Vosk-based offline speech recognition
3. **Translation Engine**: ML Kit translation with offline models
4. **Language Detection**: ML Kit language identification
5. **Text-to-Speech**: Android native TTS with multiple language support

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
cd OfflineTranslator
```

### 2. Open in Android Studio
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the OfflineTranslator directory
4. Click "OK"

### 3. Sync Project
Android Studio will automatically sync the project and download dependencies.

### 4. Speech Model Setup
1. English and Spanish Vosk models are already included at `app/src/main/assets/model-en/` and `app/src/main/assets/model-es/`.
2. If you want to replace them, download newer models and swap those folders.

### 5. Build and Run
1. Connect an Android device or start an emulator
2. Click "Run" or press Shift+F10
3. The app will install and launch on your device

## Usage

### First Launch
1. Grant microphone permission when prompted
2. Wait for the app to download translation models (requires internet for first time)
3. The Vosk speech recognition model will be unpacked automatically

### Starting a Conversation
1. Tap "Start Listening" to begin
2. Speak in either English or Spanish
3. The app will:
   - Recognize your speech
   - Detect the language
   - Translate to the other language
   - Speak the translation aloud
   - Automatically start listening for a response

### Controls
- **Start/Stop Listening**: Toggle voice recognition
- **Clear Conversation**: Clear the conversation history

## Technical Details

### Speech Recognition
- Uses Vosk English model (assets/model-en) and Spanish model (assets/model-es) for speech recognition
- Supports continuous speech recognition
- Processes audio at 16kHz sample rate

### Translation
- Google ML Kit Translation API
- Offline models for English ↔ Spanish
- Models are downloaded on first use and cached locally

### Text-to-Speech
- Android native TTS engine
- Supports both English (US) and Spanish locales
- Automatic language switching based on translation

## File Structure

```
OfflineTranslator/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/offlinetranslator/
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── drawable/
│   │   │   │       ├── ic_mic.xml
│   │   │   │       └── ic_clear.xml
│   │   │   ├── assets/
│   │   │   │   └── model-en/, model-es/ (Vosk model files)
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── build.gradle.kts
├── gradle/
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
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

1. **Additional Languages**: Support for more language pairs
2. **Conversation History**: Save and review past conversations
3. **Custom Vocabulary**: Add domain-specific terms
4. **Voice Training**: Improve recognition accuracy for specific users
5. **Offline Language Packs**: Pre-bundled language models

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is open source and available under the MIT License.

## Support

For issues, questions, or contributions, please refer to the project repository or contact the development team.

