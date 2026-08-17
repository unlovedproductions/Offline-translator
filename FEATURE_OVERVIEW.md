# Feature Overview — Puente

Puente is an Android application for face-to-face conversational translation. After the first ML Kit download over Wi‑Fi, speech recognition and translation inference run on-device. Spoken playback uses on-device TTS. Google may still collect ML Kit utilization metrics.

Pipeline: **Vosk STT → ML Kit language ID + translate → Android TTS**.

## Key Features

- **Conversational turn-taking**: Speak, get text + audio translation, continue the exchange without cloud chat.
- **On-device processing**: After Wi‑Fi model download, recognition and translation inference run locally. ML Kit may still send utilization metrics.
- **Offline speech recognition**: Vosk models for English, Spanish, French, and German.
- **Neural machine translation**: Google ML Kit offline translation models.
- **Integrated TTS**: Translations are spoken aloud; per-language voice preferences are supported.
- **Conversation transcript**: Dedicated **Transcript** dialog with search, timestamps, favorites, and confidence indicators.
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

Downloaded on first use (**Wi‑Fi** required), then cached for on-device use:

- English ↔ Spanish
- English ↔ French
- English ↔ German

## Conversation UX

- Transcript opens from the **Transcript** button (`dialog_transcript.xml`); not a permanent card on the main screen
- RecyclerView history with styled bubbles and timestamps
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

1. First launch downloads ML Kit translation models over **Wi‑Fi** (`requireWifi()`).
2. Google may collect ML Kit performance/utilization metrics ([terms](https://developers.google.com/ml-kit/terms)). This is not air-gapped.
3. Vosk speech models ship in app assets (each `model-*` needs a `uuid` file) and unpack on device.
4. Spoken playback uses **on-device TTS voices only**.
5. After models are cached, recognition and translation inference run locally (no cloud STT / cloud translate APIs).
6. Exports live under `files/exports/` and leave the device only if the user shares them. Clear conversation deletes those files.
7. Android cloud backup of app data is disabled.

## Technical Stack

- **Vosk Android** — offline speech recognition
- **Google ML Kit Translate** — offline translation
- **Google ML Kit Language ID** — language detection (Auto mode)
- **Android TTS** — spoken playback
- **Material Design** — single-activity Kotlin UI (`MainActivity`)

## Related Docs

- [README](README.md) — setup and developer overview
- [Changelog](CHANGELOG.md) — release history
- [Recovery](RECOVERY.md) — restore source and signed release
- [Product overview (website copy)](marketing/PRODUCT_OVERVIEW.md) — sales-facing summary
- [Gumroad listing](marketing/GUMROAD_LISTING.md) — store copy
- [Setup Guide](SETUP_GUIDE.md) — build and device setup
