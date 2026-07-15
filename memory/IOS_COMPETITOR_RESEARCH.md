# iOS Competitor Research Memo

Date: 2026-07-09

## Decision

No-go for a consumer iOS port of Puente's current core pitch (offline EN↔ES conversation).

## Why

- Apple Translate already ships the core loop for free: downloaded offline languages, Conversation mode, Auto Translate, face-to-face UI, and on-device mode.
- Google Translate has broad adoption and offline language packs; even where live conversation is weaker offline, Apple already covers EN↔ES well.
- iTranslate and Speak & Translate dominate subscription-style ASO for translation categories.
- New on-device AI translator apps (NoNetPlay, TranslateOff, Traverba) are already competing in the "private offline AI interpreter" niche.

## Market Wedges That Still Make Sense

- BLE two-device offline conversation
- Dialect/domain Spanish specialization
- True simultaneous interpreter mode
- Workflow product (history exports, audit logs, kiosk mode)
- Vertical/B2B distribution (clinic/hospitality/field operations)

## Go Criteria Before Building iOS

Run iPad bake-off tests in airplane mode against Apple Translate and Google Translate:

1. Quiet face-to-face conversation
2. Noisy environment
3. Dialect stress (Mexican Spanish)
4. Mid-conversation offline reliability
5. Long-turn latency and turn-taking
6. Shared-device usability without coaching

Go only if Puente can beat Apple in target scenarios and has a wedge Apple cannot copy quickly.

## Key Sources

- https://support.apple.com/guide/iphone/translate-text-voice-and-conversations-iphd74cb450f/ios
- https://support.apple.com/guide/iphone/translate-messages-calls-and-conversations-iph22b72984d/ios
- https://apps.apple.com/us/app/translate/id1514844618
- https://apps.apple.com/us/app/google-translate/id414706506
- https://support.google.com/translate/answer/6142473?co=GENIE.Platform%3DiOS&hl=en
- https://support.google.com/translate/answer/6142474?hl=en&co=GENIE.Platform%3DiOS
- https://apps.apple.com/us/app/itranslate-translator/id288113403
- https://apps.apple.com/us/app/speak-translate-translator/id804641004
- https://apps.apple.com/us/app/nonetplay-ai-interpreter/id6761640051
- https://apps.apple.com/us/app/translateoff-local-ai-model/id6762717786
- https://apps.apple.com/us/app/traverba-live-translator/id6773778315
