# iOS Wedge Decision

Date: 2026-07-09

## Decision

Proceed with a conditional-go iOS path using an explicit wedge:

Vertical shared-device interpreter for frontline operations (clinic/hospitality/field desk), focused on offline reliability, fast push-to-talk turns, and exportable bilingual logs.

## Why This Wedge

- Avoids direct "translator #47" consumer competition.
- Aligns with existing Puente strengths: conversation flow, export history, push-to-talk, confidence UX.
- Creates concrete value beyond Apple Translate default app behavior in supervised/device-managed contexts.

## Product Definition for iOS Build

- Primary mode: shared iPad face-to-face interpreting with clear turn ownership.
- Secondary mode: quick phrasebook + saved favorites for repeated workflows.
- Required output: exportable TXT/CSV/PDF interaction logs for operational follow-up.
- Target deployment: managed iPads or known teams, not broad App Store ASO discovery.

## Scope Selection Rule

Build order remains:

1. Scope A MVP implementation first.
2. Expand to parity features needed by the wedge.
3. Add App Store/testflight packaging assets and compliance materials.
