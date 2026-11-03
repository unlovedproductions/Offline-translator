# Changelog

All notable changes to Puente are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

Post-1.0.0 work on `Emergent`. Shipping APK is still `versionName` **1.0** / `versionCode` **1**.

### Added

- Transcript dialog (search, confidence help, conversation list) opened from a Transcript button
- Privacy notice on first-launch onboarding and in Model Manager
- Apache-2.0 `LICENSE` and third-party `NOTICE`
- Gumroad listing draft (`marketing/GUMROAD_LISTING.md`)
- Release signing wiring via gitignored `keystore.properties` + `keystore.properties.example`
- Operational recovery playbook (`RECOVERY.md`)

### Changed

- AndroidX / Material / AndroidX Test / Espresso to current stables (see `app/build.gradle.kts`)
- AGP **9.3.1**, Gradle **9.7.0**, compile/target SDK **37**, minSdk **23**
- ML Kit translation downloads require Wi‑Fi
- TTS voice list is on-device voices only (`isNetworkConnectionRequired` filtered out)
- Conversation exports write under `files/exports/`
- Debug `applicationId` suffix is `.debug` only (no `PUENTE` suffix on all variants)
- Vosk unpack uses per-model `uuid` markers in assets; edge-to-edge window insets on the main layout

### Fixed

- FileProvider authority (`${applicationId}.fileprovider`) matching share URIs
- Release R8 keep rules for ML Kit `ComponentRegistrar`s (release crash)
- Missing Vosk `uuid` files that blocked model unpack on first run

### Security

- `android:allowBackup="false"` plus backup / data-extraction exclude rules
- Release minify + resource shrink (`proguard-rules.pro`)
- Vosk native log level `WARNINGS`

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
