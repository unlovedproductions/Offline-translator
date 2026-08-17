# Puente — Play Store listing (draft)

**Status:** Draft only. The app is **not** on Google Play. Do not paste store URLs into other marketing. Submit only after a signed Play AAB, privacy policy URL, and Data safety form exist.

Platform: **Google Play** (no iOS). Limits from Play Console, not App Store Connect.

---

## Metadata (character-counted)

| Field | Limit | Copy | Count |
|-------|-------|------|-------|
| Title | 50 | `Puente: Offline Conversation Translator` | 39 |
| Short description | 80 | `Offline voice translator for 1 shared tablet. Speak, hear it, keep the log.` | 75 |

**Title formula:** Brand — primary keyword + secondary keyword.  
Indexed terms in title: `offline`, `conversation`, `translator`. Do not repeat those in a keyword field (Play has none).

---

## Keyword map

People search “offline translator,” not “shared-device conversational interpreter.” Title/short description catch search; body sells the actual job.

| Priority | Keyword | Placement |
|----------|---------|-----------|
| Primary | offline translator | Title + first line of full description |
| Primary | conversation translator | Title |
| Secondary | voice translator | Short description + body |
| Secondary | English Spanish translator | Body (Auto mode) |
| Tertiary | interpreting, transcript, push to talk | Body only |
| Do not use | Google Translate, Pocketalk, iTranslate, HIPAA, air-gap | Competitor brands / false claims |

---

## Full description (4,000 max)

```
Need an offline translator for a shared counter — not another phone in a tourist’s pocket?

Puente is a conversation translator for one Android tablet. Two people take turns speaking. Puente shows and speaks the translation, then keeps a transcript you can export. Speech recognition and translation run on the device after you download language packs once over Wi-Fi.

WHY A SHARED TABLET
Guest and staff should not pass two phones across the desk. Puente is built for turn-taking on one device: speaker A/B labels, start-language swap, optional push-to-talk, and confidence on each turn.

OFFLINE AFTER SETUP
• Speech models for English, Spanish, French, and German ship with the app
• Translation packs download once on Wi-Fi, then run on-device
• Spoken playback uses on-device text-to-speech
• Not an air-gapped product: Google ML Kit may collect utilization metrics

LANGUAGES (v1.0)
• Auto English ↔ Spanish
• Manual English ↔ Spanish, English ↔ French, English ↔ German

KEEP THE RECORD
Search the conversation, star important lines, export TXT, PDF, or CSV. Exports leave the device only if you share them.

NOT INCLUDED
Not 100 languages. Not simultaneous earbud hardware. Not certified medical or legal interpreting. Not iOS.

Download Puente, complete the one-time setup, and put the tablet on the counter.
```

**Approx. length:** ~1,200 characters. Room to add screenshots captions in Console, not here.

**Hook check:** Paragraph 1 names the pain, states the job, includes **offline translator** and **conversation**.

---

## Screenshot captions (benefit, not UI chrome)

1. Never pass 2 phones across the counter
2. Speak, hear the translation, keep going
3. Offline after one Wi-Fi download
4. Auto English ↔ Spanish on one tablet
5. Export the bilingual log (TXT, PDF, CSV)
6. Push-to-talk when the lobby is loud

---

## Store listing experiments (later)

| Element | Why test | Not now |
|---------|----------|---------|
| Icon | Highest conversion lift | Need a final icon |
| Screenshot 1 | 15–35% lift possible | Need device frames |
| Title | 5–15% | Lock after first index |
| Short description | 5–10% | After traffic exists |

Do not run experiments until the listing is live and you have baseline conversion.

---

## Console checklist (blockers)

- [ ] Privacy policy HTTPS URL (required)
- [ ] Data safety form (mic, on-device processing, ML Kit metrics)
- [ ] App category: **Tools** (secondary: Communication)
- [ ] Content rating questionnaire
- [ ] Signed **AAB**, not the Gumroad APK if signatures differ — one signing key story
- [ ] Pricing: paid app **or** free + no IAP. Gumroad $29 does not transfer. Decide Play price inside $19–$39 independently; Play’s 15% fee is not Gumroad’s.
- [ ] Do not claim “100% offline” in any graphic
- [ ] Support email: support@unlovedproductions.com
- [ ] Marketing URL: https://puente.unlovedproductions.com/
- [ ] Target API / Play policy current at submit time

---

## What’s New (first Play release, 500 max)

```
First Play listing. Shared-device conversation translator for Android: Auto English ↔ Spanish, manual French and German pairs, on-device speech after setup, exportable transcript.
```

---

## Honesty vs Gumroad

Play buyers will not sideload. If you publish here, update [`GUMROAD_LISTING.md`](GUMROAD_LISTING.md) and [`landing.html`](landing.html) so they no longer say “not a Play Store install.” Until then, this file is inventory for a future listing — not a live URL.
