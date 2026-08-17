# Puente — Pricing

Value-based pricing for a **one-time Gumroad APK**, not SaaS seats. No WTP survey (N=0). Van Westendorp skipped on purpose — a sub-30 sample would be noise.

**Human picks the number.** This file recommends a **model + range + test point**.

Confidence: 🟡 competitor list prices verified from public pages (2026) · 🔴 willingness-to-pay · 🟢 product cost structure (no server, no seat metering).

---

## Bottom line

| Decision | Choice |
|----------|--------|
| **Model** | One-time license to the signed Android APK |
| **Value metric** | Per device (the tablet on the counter), not per seat or per minute |
| **Acceptable range** | **$19–$39** |
| **Gumroad test price** | **$29** |
| **Secondary SKU** | **5-device workplace pack — $99** (not “Enterprise / Contact us”) |
| **Do not ship** | Subscription, freemium, Play IAP (app is not on Play), HIPAA/clinic premium |

Primary offer is the **$29 APK**. The 5-pack exists so a café with five tablets does not pay $145 and so you are not inventing a sales team.

---

## Current state

- No published price, no conversion data, no ARPU, no churn.
- Channel today: Gumroad sideload. Not Play Store.
- Cost to serve one more buyer: near zero (bandwidth + support email). Marginal cost is not the price floor; **positioning is**.
- License: **Apache-2.0**. Selling the binary is legal. You are not selling exclusivity. Price like a convenience + support product, not like locked IP.

---

## Value metric

What makes a buyer pay more: **another shared device that needs the same workflow**, not more users on one tablet (the product is one conversation on one screen).

| Candidate | Verdict |
|-----------|---------|
| Per seat | Reject. One tablet, two speakers. Seats do not map to value. |
| Per minute / usage | Reject. No metering, no backend, Apache-2.0. |
| Subscription | Reject for v1. No hosted service, no promised update cadence, buyers will compare to free Google Translate. |
| **Flat one-time per APK install rights** | **Accept.** Matches Gumroad, matches “buy the tool for this tablet.” |
| 5-device pack | Accept as volume SKU for the same binary. Same file, license-to-install on five devices. Honor-system; you cannot DRM this APK cheaply. |

---

## Value sandwich

```
[Do nothing / bilingual staff] … [Google Translate $0] … [iTranslate ~$40/yr] … [YOUR PRICE] … [Pocketalk/Timekettle ~$280–$350] … [Interpreter $50–$150/hr]
```

**Perceived job:** finish the 2-minute counter conversation on the tablet you already own, with a transcript, when Wi‑Fi is unreliable.

**Not the job:** 100 languages, simultaneous earbuds, certified medical interpreting.

Heuristic (10–20% of documented value): one avoided interpreter callout at $75 × 0.5 hour = $37.50; 20% ≈ $7 — too low, and you have no documented ROI yet. Better anchor: **software substitute for $300 hardware on a tablet they already bought.** 8–12% of $349 ≈ **$29–$42**. Floor vs annual iTranslate (~$40/yr) so a one-time $29 does not look insane.

**Next-best alternative for the ICP:** Google Translate on a personal phone (free, wrong workflow) or a Pocketalk (right-ish workflow, 10× the price). Price sits in the gap.

---

## Competitor table

| Alternative | Public price (2026) | What you get | Vs Puente |
|-------------|---------------------|--------------|-----------|
| Google Translate | $0 | Conversation, offline packs, 100+ languages | Wins on languages and distribution. Loses on shared-tablet + export log. |
| Microsoft Translator | $0 | Multi-person, each on their own phone | Different job (group, BYOD). |
| SayHi | $0 + ads | Conversation-first, cloud | Needs network. No operational export story. |
| iTranslate Pro | ~$5.99/mo or ~$39.99/yr | Voice + offline as Pro | Travel app. Recurring. |
| Timekettle WT2 / W4 | **$349** list (street often ~$280) | Simultaneous earbuds | Hardware, many languages, extra device. |
| Pocketalk S2-class | **~$350** (edu bundles with cellular) | Handheld + many languages | Hardware. |
| On-call interpreter | **$50–$150/hr** typical, often a minimum | Certified humans | Correct for clinical/legal. Overkill for desk small-talk. |

Sources: vendor list pages and 2026 roundups. Treat as directional, not a live price feed.

---

## Packaging (two SKUs, not Good/Better/Best SaaS)

Three fake SaaS tiers would be an anti-pattern: there is no upgrade trigger, no hosted feature gate, and Apache-2.0.

| SKU | Price | What they get | Who |
|-----|-------|---------------|-----|
| **Puente APK** | **$29** one-time | Signed release APK, EN↔ES auto + EN↔FR/DE manual, transcript/export, email support | One counter tablet / one owner |
| **Workplace 5-pack** | **$99** one-time | Same APK; permission to install on up to 5 devices; same support inbox | Small hotel / café / shop |

Same binary. Do not feature-gate languages or export — that is a support nightmare and a trust hit.

**Out of packaging:** Enterprise SSO, BAA, SLA, “Contact sales.” You do not have that motion.

---

## Recommended test point: $29

| Signal | Read |
|--------|------|
| Below $19 | Looks like a hobby APK; still loses to free Translate; Gumroad fees eat margin. |
| **$29** | Under one month of iTranslate Pro; ~8% of Pocketalk list; easy “try it on the desk tablet.” |
| $39 | Top of range. Use only after $29 converts and refunds stay low. |
| $49+ | Needs proof (reviews, named desks, Play listing). You have none. Apache-2.0 makes $99+ for a single APK look extractive. |

**Gumroad:** show **$29**. Optional launch discount $19 for 14 days is allowed; do not start at $9 — that trains “this is junk.”

**Public price page:** https://puente.unlovedproductions.com/ (subdomain of unlovedproductions.com). CTA there must point at the live Gumroad URL once it exists.

**Refunds:** Gumroad default refund window. State it. Sideload friction will cause some refunds; price in a 10–20% refund rate until the install doc is airtight.

---

## Price increase later

If Play Store ships or you have 30+ named workplace buyers: raise **new** sales to $39; grandfather Gumroad buyers. Do not run a SaaS-style increase on a one-time APK.

---

## What copy must say

- One-time. Not a subscription.
- Sideload. Not Play (until it is).
- Offline **after** the first ML Kit download.
- Apache-2.0: they are buying the build and support, not exclusive rights.
- No HIPAA.

---

## What you still decide

1. Lock **$29** or pick another number inside **$19–$39**.
2. Whether the 5-pack is live on day one or added after the first workplace email.
3. Whether Apache-2.0 stays. If you want a defensible $79+ single-device price, that is a license conversation, not a copy tweak.
