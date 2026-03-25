# Offline Translator App PRD

## Original Problem Statement
- Review the repo and provide summary
- Implement all improvement opportunities in order of importance without regenerating the codebase, only modifying necessary files
- Improvements: UI state consistency, move strings to strings.xml, conversation history list, manual stop indicator, language override toggle, additional languages, conversation export, and silence timeout

## Architecture Decisions
- Android Kotlin single-activity app (MainActivity) remains the orchestrator
- Conversation UI uses RecyclerView with custom message items (bubbles + timestamps + favorites + confidence threshold handling) and search filtering
- Language modes: Auto (EN↔ES), EN↔ES, EN↔FR, EN↔DE with start-language dropdown and quick swap
- Translation expanded with ML Kit translators for French and German pairs
- Speech recognition uses Vosk models for EN/ES/FR/DE stored in assets and unpacked at runtime
- Audio capture uses AudioRecord for real-time RMS mic level (smoothed + peak-hold) and optional noise reduction (NoiseSuppressor/AEC/AGC) toggle
- Export pipeline supports TXT/PDF/CSV via FileProvider share
- Model Manager dialog provides simple (Vosk) or advanced (Vosk + ML Kit) model controls
- Voice preferences per language stored in SharedPreferences
- Export history and onboarding stored locally via SharedPreferences

## What’s Implemented
- Created INTERNAL_REBUILD_GUIDE.md with full reconstruction steps, dependencies, assets, and validation checklist
- RecyclerView conversation history with search filtering, favorites, confidence indicators, and styled bubbles with timestamps
- Added confidence threshold handling (low-confidence prompt with retry/translate) and a transcript header tooltip explaining confidence
- UI state consistency fixes, smoothed mic level meter with dB readout, noise reduction toggle, push-to-talk mode, and model download progress indicator
- Language mode toggle, start-language dropdown, quick swap, and speaker profiles
- Additional language support (English↔French, English↔German) plus Vosk speech models for EN/ES/FR/DE
- Conversation export (TXT/PDF/CSV) with export history and share support via FileProvider
- Model Manager dialog with simple (Vosk) and advanced (Vosk + ML Kit) modes
- Offline phrasebook (EN/ES) and one-time onboarding tips dialog
- Per-language TTS voice selection and wake-lock during listening sessions
- All user-facing strings moved to strings.xml
- Added unit tests (AppUtilsTest) and UI tests (MainActivityUiTest) covering tooltip, export options, model manager, favorites, and phrasebook

## Prioritized Backlog
- P0: Run real-device/emulator QA for Vosk + ML Kit flow, model manager actions, and push-to-talk behavior
- P1: Add model download progress per language and optional confidence thresholds
- P2: Add more language pairs and on-demand model downloads

## Next Tasks
- Validate on Android device/emulator for audio pipeline, PDF/CSV exports, and model manager actions
- QA favorites filter, confidence labels, and phrasebook UX
- Consider adding phrasebook search and audio playback
