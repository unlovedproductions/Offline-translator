# Changelog

All notable changes to Puente are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project uses [Semantic Versioning](https://semver.org/).

## [1.0.0] - 2026-08-04

Initial commercial release of the Puente Android offline conversational translator.

### Added

- Offline conversational pipeline: Vosk speech recognition → ML Kit language ID + translation → Android TTS
- Language modes: Auto (English ↔ Spanish), English ↔ Spanish, English ↔ French, English ↔ German
- Start-language selection and quick language swap
- Bundled Vosk speech models for English, Spanish, French, and German
- ML Kit offline translation for EN↔ES, EN↔FR, and EN↔DE (downloaded on first use)
- Conversation transcript with search, timestamps, favorites, and confidence indicators
- Low-confidence handling with retry or translate-anyway
- Speaker A/B profiles for shared-device interpreting
- Push-to-talk mode, silence auto-stop, mic level meter (dB), and noise reduction toggle
- Conversation export to TXT, PDF, and CSV with share support and export history
- Offline English/Spanish phrasebook
- Model Manager (simple Vosk controls; advanced Vosk + ML Kit)
- Per-language TTS voice preferences
- First-launch onboarding for language and default mode
- Wake-lock during listening sessions
- Unit and UI tests covering tooltip, export options, Model Manager, favorites, and phrasebook
