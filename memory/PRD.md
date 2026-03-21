# Offline Translator App PRD

## Original Problem Statement
- Review the repo and provide summary
- Implement all improvement opportunities in order of importance without regenerating the codebase, only modifying necessary files
- Improvements: UI state consistency, move strings to strings.xml, conversation history list, manual stop indicator, language override toggle, additional languages, conversation export, and silence timeout

## Architecture Decisions
- Android Kotlin single-activity app (MainActivity) remains the orchestrator
- Conversation UI uses RecyclerView with custom message items (bubbles + timestamps) and search filtering
- Language modes: Auto (EN↔ES), EN↔ES, EN↔FR, EN↔DE with start-language dropdown and quick swap
- Translation expanded with ML Kit translators for French and German pairs
- Speech recognition uses Vosk models for EN/ES/FR/DE stored in assets and unpacked at runtime
- Audio capture uses AudioRecord for real-time RMS mic level and optional noise reduction (NoiseSuppressor/AEC/AGC) toggle
- Voice preferences per language stored in SharedPreferences
- Export history and onboarding stored locally via SharedPreferences

## What’s Implemented
- RecyclerView conversation history with search filtering, empty state, and styled bubbles with timestamps
- UI state consistency fixes, real-time mic level meter, noise reduction toggle, and model download progress indicator
- Language mode toggle, start-language dropdown, quick swap, and speaker profiles
- Additional language support (English↔French, English↔German) plus Vosk speech models for EN/ES/FR/DE
- Conversation export (save + share) with export history
- Silence timeout controls (toggle + adjustable timeout)
- Per-language TTS voice selection
- One-time onboarding tips dialog
- All user-facing strings moved to strings.xml

## Prioritized Backlog
- P0: Run real-device/emulator QA for Vosk + ML Kit flow; validate FR/DE speech models and voice selection
- P1: Add mic meter smoothing/peak-hold and per-language noise suppression calibration
- P2: Add more language pairs and model size optimization (on-demand downloads)

## Next Tasks
- Validate on Android device/emulator for EN/ES/FR/DE speech recognition and voice selection
- QA export history and search filtering UX
- Consider model download compression or on-demand download flow
