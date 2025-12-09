# Offline Translator Setup Guide

This guide provides step-by-step instructions for setting up and building the Offline Translator Android application.

## System Requirements

### Development Environment
- **Operating System**: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **Android Studio**: Arctic Fox (2020.3.1) or later
- **Java Development Kit**: JDK 17 or later
- **Memory**: Minimum 8GB RAM (16GB recommended)
- **Storage**: At least 10GB free space

### Target Device Requirements
- **Android Version**: API level 21 (Android 5.0) or higher
- **RAM**: Minimum 2GB (4GB recommended for optimal performance)
- **Storage**: 3GB free space for models and app data
- **Microphone**: Required for speech input
- **Speakers/Headphones**: Required for audio output

## Pre-Installation Setup

### 1. Install Android Studio
1. Download Android Studio from https://developer.android.com/studio
2. Run the installer and follow the setup wizard
3. Install the Android SDK and required build tools
4. Configure the Android Virtual Device (AVD) if testing on emulator

### 2. Configure Android SDK
1. Open Android Studio
2. Go to File → Settings (or Android Studio → Preferences on macOS)
3. Navigate to Appearance & Behavior → System Settings → Android SDK
4. Ensure the following are installed:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android SDK Command-line Tools
   - Android Emulator (if using emulator)

### 3. Set Up Device/Emulator
**For Physical Device:**
1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect device via USB
4. Accept debugging permissions

**For Emulator:**
1. Open AVD Manager in Android Studio
2. Create a new virtual device
3. Choose a device with API level 21 or higher
4. Ensure the emulator has sufficient RAM (2GB+)

## Project Setup

### 1. Download Project Files
Extract the provided project files to a directory on your computer, for example:
```
C:\AndroidProjects\OfflineTranslator  (Windows)
~/AndroidProjects/OfflineTranslator   (macOS/Linux)
```

### 2. Open Project in Android Studio
1. Launch Android Studio
2. Click "Open an Existing Project"
3. Navigate to the OfflineTranslator directory
4. Click "OK"
5. Wait for the project to sync (this may take several minutes)

### 3. Download Vosk Model
The Vosk speech recognition model needs to be downloaded and placed in the correct location:

1. Download the English model from: https://alphacephei.com/vosk/models/vosk-model-en-us-0.22.zip
2. Extract the ZIP file
3. Copy the extracted folder to: `app/src/main/assets/model/`
4. The final path should be: `app/src/main/assets/model/vosk-model-en-us-0.22/`

### 4. Sync Project Dependencies
1. In Android Studio, click "Sync Now" if prompted
2. Or go to File → Sync Project with Gradle Files
3. Wait for all dependencies to download

## Building the Application

### 1. Clean and Build
1. In Android Studio, go to Build → Clean Project
2. Wait for cleaning to complete
3. Go to Build → Rebuild Project
4. Wait for build to complete (may take 5-10 minutes on first build)

### 2. Resolve Build Issues
If you encounter build errors:

**Common Solutions:**
- Ensure Java 17 is being used
- Check that all SDK components are installed
- Verify Gradle wrapper is properly configured
- Clear cache: File → Invalidate Caches and Restart

**Dependency Issues:**
- Check internet connection for downloading dependencies
- Verify repository URLs in build.gradle files
- Try Build → Clean Project followed by Build → Rebuild Project

### 3. Generate APK
To create an installable APK file:
1. Go to Build → Build Bundle(s) / APK(s) → Build APK(s)
2. Wait for build to complete
3. APK will be generated in: `app/build/outputs/apk/debug/`

## Installation and Testing

### 1. Install on Device
**Via Android Studio:**
1. Connect your device or start emulator
2. Click the "Run" button (green triangle)
3. Select your target device
4. App will install and launch automatically

**Via APK File:**
1. Transfer the APK file to your Android device
2. Enable "Install from Unknown Sources" in device settings
3. Tap the APK file and follow installation prompts

### 2. Grant Permissions
On first launch:
1. The app will request microphone permission
2. Tap "Allow" to enable speech recognition
3. Wait for initial setup to complete

### 3. Download Translation Models
On first use with internet connection:
1. The app will download ML Kit translation models
2. This requires ~200MB of data
3. Models are cached for offline use

## Verification and Testing

### 1. Test Speech Recognition
1. Tap "Start Listening"
2. Speak clearly in English
3. Verify text appears in the conversation area
4. Check that the app stops listening after speech

### 2. Test Translation
1. Speak an English phrase
2. Verify Spanish translation appears
3. Check that translated text is spoken aloud
4. Confirm app switches to listening for Spanish

### 3. Test Conversation Flow
1. Start with English phrase
2. Wait for Spanish translation and speech
3. Respond in Spanish
4. Verify English translation and speech
5. Confirm continuous conversation flow

## Troubleshooting

### Build Issues

**Gradle Sync Failed:**
```bash
# Clear Gradle cache
./gradlew clean
# Or in Windows
gradlew.bat clean
```

**Out of Memory:**
Add to `gradle.properties`:
```
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
```

**SDK Not Found:**
1. Check Android SDK path in File → Project Structure
2. Verify ANDROID_HOME environment variable
3. Reinstall Android SDK if necessary

### Runtime Issues

**Microphone Permission Denied:**
1. Go to device Settings → Apps → OfflineTranslator → Permissions
2. Enable Microphone permission
3. Restart the app

**Speech Recognition Not Working:**
1. Check device microphone functionality
2. Verify Vosk model is in correct location
3. Check app logs for error messages

**Translation Not Working:**
1. Ensure internet connection for initial model download
2. Check available storage space
3. Verify ML Kit services are installed

**TTS Not Speaking:**
1. Check device volume settings
2. Test TTS in device accessibility settings
3. Install additional language packs if needed

### Performance Issues

**App Slow to Start:**
- Normal on first launch due to model loading
- Subsequent launches should be faster
- Consider device RAM limitations

**High Memory Usage:**
- Expected due to speech recognition and ML models
- Close other apps if device has limited RAM
- Consider using device with more RAM

## Advanced Configuration

### Custom Model Configuration
To use different Vosk models:
1. Download desired model from Vosk website
2. Replace model in `app/src/main/assets/model/`
3. Update model name in MainActivity.kt if necessary

### Build Variants
The project supports debug and release builds:
- Debug: Includes logging and debugging features
- Release: Optimized for production use

### Signing Configuration
For release builds, configure signing:
1. Generate keystore file
2. Add signing configuration to build.gradle
3. Build signed APK for distribution

## Support and Resources

### Documentation
- Android Developer Guide: https://developer.android.com/guide
- Vosk Documentation: https://alphacephei.com/vosk/
- ML Kit Documentation: https://developers.google.com/ml-kit

### Community Support
- Stack Overflow: Tag questions with 'android', 'vosk', 'ml-kit'
- GitHub Issues: Report bugs and feature requests
- Android Developer Community: https://developer.android.com/community

This setup guide should help you successfully build and deploy the Offline Translator application. If you encounter any issues not covered here, please refer to the troubleshooting section or seek community support.

