# Feature Overview — Puente

Puente is an Android application for face-to-face conversational translation that runs on-device. After the first ML Kit model download, speech recognition, translation, and spoken playback work without an active internet connection.

Pipeline: **Vosk STT → ML Kit language ID + translate → Android TTS**.

## Key Features

- **Conversational turn-taking**: Speak, get text + audio translation, continue the exchange without cloud chat.
- **On-device processing**: Recognition and translation run locally for privacy and offline reliability.
- **Offline speech recognition**: Vosk models for English, Spanish, French, and German.
- **Neural machine translation**: Google ML Kit offline translation models.
- **Integrated TTS**: Translations are spoken aloud; per-language voice preferences are supported.
- **Conversation transcript**: Chat-style history with search, timestamps, favorites, and confidence indicators.
- **Push-to-talk and silence control**: Optional PTT mode, silence auto-stop, mic level (dB), and noise reduction.
- **Export and share**: Save conversations as TXT, PDF, or CSV with export history and system share.
- **Offline phrasebook**: Quick EN/ES phrases for repeated workflows.
- **Model Manager**: Simple (Vosk) or advanced (Vosk + ML Kit) model controls.
- **Speaker profiles**: Speaker A/B labeling for shared-device interpreting.
- **Onboarding**: First-launch language and default mode setup.

## Supported Languages and Modes

| Mode | Direction | Notes |
| :--- | :--- | :--- |
| **Auto** | English ↔ Spanish | Automatic language detection between turns |
| **EN ↔ ES** | Bidirectional | Manual pair; start language + quick swap |
| **EN ↔ FR** | Bidirectional | Manual pair; start language + quick swap |
| **EN ↔ DE** | Bidirectional | Manual pair; start language + quick swap |

### Speech models (Vosk)

- English (`model-en`)
- Spanish (`model-es`)
- French (`model-fr`)
- German (`model-de`)

### Translation models (ML Kit)

Downloaded on first use (internet required once), then cached for offline use:

- English ↔ Spanish
- English ↔ French
- English ↔ German

## Conversation UX

- RecyclerView transcript with styled bubbles and timestamps
- Search filtering across the current conversation
- Favorites for important turns
- Confidence scores with low-confidence retry or translate-anyway
- Start-language dropdown and quick language swap
- Wake-lock while listening so the session stays active

## Productivity and Ops

- Export conversation to **TXT**, **PDF**, or **CSV**
- Export history and share via FileProvider
- Offline phrasebook (English / Spanish)
- Model Manager for inspecting and managing local models
- Per-language TTS voice selection

## Privacy and Offline Behavior

1. First launch may download ML Kit translation models (internet required).
2. Vosk speech models ship in app assets and unpack on device.
3. After setup, recognition, translation, and TTS work without network.
4. Conversation and export data stay on device unless the user explicitly shares an export.

## Technical Stack

- **Vosk Android** — offline speech recognition
- **Google ML Kit Translate** — offline translation
- **Google ML Kit Language ID** — language detection (Auto mode)
- **Android TTS** — spoken playback
- **Material Design** — single-activity Kotlin UI (`MainActivity`)

## Related Docs

- [README](README.md) — setup and developer overview
- [Changelog](CHANGELOG.md) — release history
- [Product overview (website copy)](marketing/PRODUCT_OVERVIEW.md) — sales-facing summary
- [Setup Guide](SETUP_GUIDE.md) — build and device setup
