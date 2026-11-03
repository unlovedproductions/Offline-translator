# Puente — Gumroad listing copy

Paste into Gumroad. Price: **$29** one-time (test point; range $19–$39). Do not claim air-gap privacy, HIPAA, Play Store, or user counts.

Headline used on the landing page (scored 61/100): **Never pass 2 phones across the counter again.**

---

## Title

Puente — Offline Conversational Translator (Android APK)

## Short tagline

Two people. One Android tablet. Speak, hear the translation, keep the transcript — offline after setup.

## Price

**$29 USD** — one-time. Not a subscription.

Optional second product: **Workplace 5-pack — $99** (same APK; install on up to 5 devices). Honor system. Same support inbox.

## Description

**Puente** is a shared-device conversational interpreter for Android.

Site: **https://puente.unlovedproductions.com/**

A guest who does not share your language is at the counter. You have one tablet. You should not pass two phones back and forth, and you should not need the guest network for the rest of the shift.

Two people take turns speaking into one phone or tablet. Puente recognizes speech on the device (Vosk), translates with Google ML Kit on-device models, speaks the result aloud (on-device TTS), and keeps a transcript you can export.

### What you get

- Signed Android APK (`com.puente.offline.translator`), version **1.0** (`versionCode` 1)
- Auto **English ↔ Spanish** (language detection between turns)
- Manual **English ↔ Spanish**, **English ↔ French**, **English ↔ German**
- Conversation transcript (Transcript button / dialog) with search, favorites, timestamps, and confidence
- Export to **TXT / PDF / CSV** via the system share sheet
- Speaker A/B labels, optional push-to-talk, silence auto-stop
- Offline English/Spanish phrasebook
- Model Manager for bundled speech models and translation packs
- Email support: support@unlovedproductions.com

### What this is not

- Not a tourist camera-translate app with 100 languages
- Not simultaneous earbud hardware (Pocketalk / Timekettle)
- Not certified interpreting, HIPAA, or a BAA
- Not a Play Store install, not iOS
- Not a subscription and not a hosted service

### Requirements

- Android **6.0+** (API 23)
- Microphone access
- Enough free storage for speech + translation models (**~2GB** recommended)
- Ability to install from unknown sources / sideload an APK
- **Wi‑Fi once** for the first ML Kit translation download (and when you add packs)

### Network and privacy (read this)

- **Speech recognition** runs on the device with bundled Vosk models. Audio is not sent to a cloud STT API.
- **Translation models** download from Google over **Wi‑Fi** on first use (and when you add packs). After that, translation inference runs on-device.
- Under [ML Kit Terms](https://developers.google.com/ml-kit/terms), Google may collect performance/utilization metrics. This is **not** an air-gapped product.
- **Spoken playback** uses on-device TTS voices only.
- **Exports** leave the device only if you share them.
- Android cloud backup of app data is disabled.

### Install

1. Pay **$29**. Download the APK from Gumroad.
2. On Android: allow install from this source / unknown apps.
3. Install, grant microphone permission, complete onboarding.
4. Connect to Wi‑Fi once so ML Kit translation models can download.
5. Put the tablet on the counter. Take turns speaking.

### Support

support@unlovedproductions.com  
Product: Puente by Unloved Productions

Need five devices? Ask for the **$99 workplace pack** instead of buying five single licenses.

### License / third-party notices

App license: **Apache-2.0** (see LICENSE). You are buying the signed build and email support, not exclusive rights.  
Includes Vosk (Apache-2.0) and Google ML Kit (Google terms). Full attribution in NOTICE.

## Gumroad product settings (checklist)

- [x] Price **$29** (or your pick inside $19–$39). Currency USD.
- [x] One-time purchase. Disable membership/subscription.
- [x] Upload **signed** release APK only (not debug, not `*-unsigned.apk`) — `Puente-1.0.apk`
- [x] Version note matches `versionName` 1.0 / `versionCode` 1
- [ ] File size warning: APK is large (~388MB speech models bundled) — confirm in Gumroad UI; API returned size null
- [x] Refund policy: 30 days
- [x] No screenshots that claim “100% offline forever” or “HIPAA”
- [ ] After each paid update: same signing key, higher `versionCode`
- [x] Product URL / website: **https://puente.unlovedproductions.com/**
- [x] Live listing: **https://unlovedproductions.gumroad.com/l/puente** (`BUY_URL` in [`landing.html`](landing.html))
- [x] **Cover videos** (pre-purchase carousel — not Content-tab / paywalled files):
  1. Uploaded [`videos/puente-30s.mp4`](videos/puente-30s.mp4) as **first** cover (main)
  2. Uploaded [`videos/puente-60s.mp4`](videos/puente-60s.mp4) as **second** cover
  3. Poster still: [`videos/puente-cover.jpg`](videos/puente-cover.jpg) as third cover
  4. Optional subtitles: [`videos/puente-30s.vtt`](videos/puente-30s.vtt) / [`videos/puente-60s.vtt`](videos/puente-60s.vtt) — attach in Gumroad UI if the file drawer exposes “Add subtitles” (CLI has no VTT attach)
- [x] Cover videos are **branded motion + UI mock** of the Transcript flow — not a live APK screencast. Do not claim otherwise.
- [x] Cover copy stays honest: offline after setup / Wi‑Fi once; no HIPAA, air-gap, or Play Store claims
