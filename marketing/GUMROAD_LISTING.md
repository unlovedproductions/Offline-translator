# Puente — Gumroad listing copy

Paste/adapt into Gumroad. Do not claim air-gap privacy or HIPAA.

## Title

Puente — Offline Conversational Translator (Android APK)

## Short tagline

Face-to-face bilingual interpreting on one Android device. Speak, hear the translation, keep the transcript.

## Description

**Puente** is an Android conversational translator for shared-device interpreting.

Two people take turns speaking into one phone or tablet. Puente recognizes speech on the device (Vosk), translates with Google ML Kit on-device models, speaks the result aloud (on-device TTS), and keeps a transcript you can export.

### What you get

- Android APK (`com.puente.offline.translator`)
- English ↔ Spanish (auto + manual)
- English ↔ French and English ↔ German (manual)
- Conversation transcript with search, favorites, confidence
- Export to TXT / PDF / CSV via the system share sheet
- Model Manager for bundled speech models and translation packs

### Requirements

- Android **6.0+** (API 23)
- Microphone access
- Enough free storage for speech + translation models (~2GB recommended)
- Ability to install from unknown sources / sideload an APK

### Network and privacy (read this)

- **Speech recognition** runs on the device with bundled Vosk models. Audio is not sent to a cloud STT API.
- **Translation models** download from Google over **Wi‑Fi** on first use (and when you add packs). After that, translation inference runs on-device.
- Under [ML Kit Terms](https://developers.google.com/ml-kit/terms), Google may collect performance/utilization metrics. This is **not** an air-gapped product.
- **Spoken playback** uses on-device TTS voices only.
- **Exports** leave the device only if you share them.
- Android cloud backup of app data is disabled.

**Not included:** HIPAA/BAA coverage, clinic compliance certification, Play Store install, iOS.

### Install

1. Download the APK from Gumroad.
2. On Android: allow install from this source / unknown apps.
3. Install, grant microphone permission, complete onboarding.
4. Connect to Wi‑Fi once so ML Kit translation models can download.
5. Start listening and take turns speaking.

### Support

support@unlovedproductions.com  
Product: Puente by Unloved Productions

### License / third-party notices

App license: Apache-2.0 (see LICENSE in distribution notes).  
Includes Vosk (Apache-2.0) and Google ML Kit (Google terms). Full attribution in NOTICE.

## Gumroad product settings (checklist)

- [ ] Upload **signed** release APK only (not debug, not `*-unsigned.apk`)
- [ ] Version note matches `versionName` / `versionCode` in the build
- [ ] File size warning: APK is large (~speech models bundled)
- [ ] Refund / support policy filled in
- [ ] No screenshots that claim “100% offline forever” or “HIPAA”
- [ ] After each paid update: same signing key, higher `versionCode`
