# Offline Translator App PRD

## Original Problem Statement
- Review the repo and provide summary
- Implement all improvement opportunities in order of importance without regenerating the codebase, only modifying necessary files
- Improvements: UI state consistency, move strings to strings.xml, conversation history list, manual stop indicator, language override toggle, additional languages, conversation export, and silence timeout

## Architecture Decisions
- Android Kotlin single-activity app (MainActivity) remains the orchestrator
- Conversation UI migrated from a single TextView to RecyclerView with an empty state
- Language modes implemented via a single toggle (Auto → EN/ES → EN/FR → EN/DE)
- Translation expanded with ML Kit translators for French and German pairs
- Export implemented with internal file save + share intent (text/plain)
- Silence timeout enforced with Handler/Looper

## What’s Implemented
- RecyclerView-based conversation history with empty-state handling
- UI state consistency fixes (start/stop button enabled state, status updates)
- Listening indicator icon color changes for active/idle state
- Language mode toggle and subtitle updates
- Additional language support (English↔French, English↔German)
- Conversation export (save + share)
- Silence timeout (1.5s) for pause detection
- All user-facing strings moved to strings.xml

## Prioritized Backlog
- P0: Run real-device/emulator QA for Vosk + ML Kit flow; verify model downloads and TTS behavior
- P1: Improve message styling (bubbles, timestamps), add persistent transcript list
- P2: Add more language pairs and manual target language selection

## Next Tasks
- Validate on Android device/emulator
- Add UI polish to conversation list and export confirmations
- Consider offline pre-bundled language models for faster first run
