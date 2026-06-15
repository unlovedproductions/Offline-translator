# Feature Overview - Offline Translator

Offline Translator is a professional-grade Android application designed for seamless, real-time conversational translation without requiring an active internet connection. By leveraging advanced on-device machine learning, the application ensures privacy, speed, and reliability in any environment.

## Key Features

- **Real-Time Conversational Flow**: The application automatically detects the spoken language and provides instant audio and text translations, allowing for natural back-and-forth communication.
- **On-Device Processing**: All speech recognition and translation tasks are performed locally on the device, ensuring data privacy and functionality in remote areas.
- **High-Accuracy Speech Recognition**: Powered by the Vosk engine, providing robust voice-to-text capabilities even in noisy environments.
- **Neural Machine Translation**: Utilizes Google ML Kit's state-of-the-art translation models for high-quality, context-aware translations.
- **Integrated Text-to-Speech (TTS)**: Translations are automatically spoken aloud using high-quality synthetic voices.

## Supported Languages and Pairs

Currently, the application is optimized for the following language pairs and specific locales:

### Primary Language Pair
| Source Language | Target Language | Direction |
| :--- | :--- | :--- |
| **English (United States)** | **Spanish** | Bidirectional (↔) |

### Supported Locales
- **English**: `en-US` (United States)
- **Spanish**: `es` (Standard Spanish)

## Technical Capabilities

- **Automatic Language Identification**: The system intelligently distinguishes between English and Spanish speakers without manual switching.
- **Offline Model Management**: High-performance translation models are stored locally (~100MB per language) for instant access.
- **Continuous Listening**: Optimized for conversational turn-taking with automated re-activation after translation playback.
