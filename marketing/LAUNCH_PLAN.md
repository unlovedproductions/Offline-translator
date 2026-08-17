# Puente — Launch plan

One-person Gumroad launch. No paid ads. Owner: you. Site: **https://puente.unlovedproductions.com/** (subdomain of unlovedproductions.com).

Do not treat this as a Play Store launch. Play listing copy in [`PLAY_STORE_LISTING.md`](PLAY_STORE_LISTING.md) stays draft.

---

## Goal

Ship a paid APK people can find, understand, and install without a sales call.

| Metric | Launch-week target | Why this number |
|--------|--------------------|-----------------|
| Site live on HTTPS | Yes | Owned channel |
| Gumroad product live at $29 | Yes | Primary offer |
| Paid downloads | 1–10 | First proof, not vanity |
| Refunds | Track; investigate if >20% | Sideload friction |
| Named workplace user | 0–1 | ICP is still a hypothesis |

---

## Offer (locked)

- **$29** one-time APK (range $19–$39; you pick the number)
- Optional **$99** 5-pack via email, not a fake enterprise tier
- Headline: Never pass 2 phones across the counter again.
- Claims: no HIPAA, no air-gap, no Play, Apache-2.0

---

## ORB channel map

A launch that only posts on one social account is incomplete. Cover all three.

### Owned (you control reach)

| Asset | Action | When | Owner |
|-------|--------|------|-------|
| **puente.unlovedproductions.com** | Serve [`landing.html`](landing.html) as `index.html` on a static host. DNS: `CNAME puente` → that host (Netlify/Cloudflare/Pages). HTTPS on. | T−7 | You |
| Gumroad product | $29, signed APK, refund policy, website field = the subdomain | T−3 | You |
| `BUY_URL` in landing.html | Replace `#buy` with the live Gumroad URL so the subdomain CTA leaves the site | T−3 | You |
| Support inbox | support@unlovedproductions.com — 24h reply on install failures | T−3 onward | You |
| Update email | If you have any list: one announcement, one “how to sideload” follow-up. If list = 0, skip. | Launch day | You |

### Rented (their rules)

| Channel | Action | When | Do not |
|---------|--------|------|--------|
| Reddit / forums | One post in a community that already talks about front-desk ops, offline tools, or Android sideload — **not** spam in r/androidapps with “best translator.” Lead with the shared-tablet job and the privacy limits. | Launch day | Daily bumping |
| LinkedIn / X | One post from the Unloved Productions account: headline + subdomain link + $29. | Launch day | A 14-day teaser thread with no product |
| Hospitality / small-business groups | Only if you are already a member. One useful post, not an ad. | Launch +2 to +7 | Cold spam |

### Borrowed (other people’s audiences)

| Channel | Action | When |
|---------|--------|------|
| 5 operators you actually know | Text or email: “Put this on the desk tablet, tell me where it dies.” Free or $29, your call. This is ICP validation, not PR. | T−14 to T−7 |
| Indie / APK roundup newsletters | Pitch once: shared-device interpreter, offline after setup, sideload, $29. Include the honesty paragraph. | T−10 (they need lead time) |
| Product Hunt | Optional. Only if you will sit on comments for 12 hours. Listing URL = subdomain. Category: Android / productivity. Skip if you cannot staff it. | Launch day or never |

No paid ads in this plan.

---

## Phases

### Pre-launch (T−14 to T−1)

1. **T−14 — ICP check.** Give the APK to 3–5 people who work a counter or share a tablet. If they say “I’d just use Google Translate,” the landing is wrong or the ICP is wrong. Fix copy before you price-anchor publicly.
2. **T−10 — Signed APK.** Release build, `versionName` 1.0, `versionCode` 1. Sideload on a clean Android 6+ device. Time the first ML Kit download on Wi-Fi.
3. **T−7 — Subdomain.** Point `puente.unlovedproductions.com` at static hosting. Publish `landing.html` as the index. Confirm HTTPS and that the H1 loads.
4. **T−3 — Gumroad.** Upload APK, $29, website = subdomain. Copy from [`GUMROAD_LISTING.md`](GUMROAD_LISTING.md). Set `BUY_URL`. Buy it yourself once (refund if you want) to test the file.
5. **T−1 — Install note.** One paragraph: allow unknown sources → grant mic → Wi-Fi once → ~2GB. Put it on Gumroad and keep it in the email signature.

### Launch day

Time-box. Do not “see how it feels.”

| Time | Action |
|------|--------|
| Morning (your TZ) | Confirm subdomain, Gumroad download, `BUY_URL` |
| +0 | Post owned update if you have a list |
| +30 min | One social post + one community post. Same link: https://puente.unlovedproductions.com/ |
| Day | Answer every install question the same day. If sideload fails, that is a product bug for launch, not a “user error” shrug |
| Evening | Write down: downloads, refunds, questions asked. Those questions become FAQ on the subdomain |

### Post-launch (30 days)

| Day | Action |
|-----|--------|
| 2–7 | Comparison note (not a new site): Puente vs Google Translate vs a $350 gadget — three bullets, honest losses. Add a short section to the landing or a `/compare` later. |
| 7 | If zero sales: do not cut to $9. Fix install friction and ICP outreach first. |
| 14 | Optional $19 launch window **ends** if you used it. Back to $29. |
| 14–30 | One workplace follow-up: 5-pack at $99 only if someone asked for a second tablet. |
| 30 | Decide Play Store (use [`PLAY_STORE_LISTING.md`](PLAY_STORE_LISTING.md)) or stay Gumroad. Do not do both half-way. |

---

## Subdomain runbook

Canonical URL: **https://puente.unlovedproductions.com/**

1. Create a static site whose only public file is this landing (copy `marketing/landing.html` → `index.html`).
2. At the DNS host for `unlovedproductions.com`, add `CNAME puente` → the host’s target (Netlify/Cloudflare/Pages docs for that host).
3. Wait for HTTPS. Do not share `http://`.
4. After Gumroad exists, set `BUY_URL` in the HTML and redeploy.
5. Gumroad “Website” / product URL field: the subdomain, not the GitHub repo.

Do not attach this Android repo’s root as the Netlify publish directory — that would expose Gradle trees. Publish **only** the landing file.

---

## Launch-day checklist

- [ ] Signed release APK on Gumroad, not debug
- [ ] Price $29 (or your pick in $19–$39)
- [ ] https://puente.unlovedproductions.com/ loads, one H1, CTA works
- [ ] `BUY_URL` is the live Gumroad link
- [ ] Privacy limits still on the page (ML Kit metrics, not HIPAA)
- [ ] support@unlovedproductions.com replies
- [ ] One owned + one rented + at least one borrowed action (see ORB)
- [ ] No Play Store URL anywhere
- [ ] No invented testimonials

---

## Copy-edit log (this pass)

Applied across marketing artifacts:

- Dropped “request a demo” / clinic-as-ICP from public copy
- Price and one-time model in every CTA
- Proof: none invented; privacy limits kept
- Public landing no longer tells visitors to “search BUY_URL”
- Canonical URL set to the unlovedproductions.com subdomain

---

## What you still do

1. Pick the final dollar amount inside $19–$39 (recommendation remains **$29**).
2. Create the Gumroad product and paste its URL into `BUY_URL`.
3. Point **puente.unlovedproductions.com** at the landing file and turn on HTTPS.
4. Decide Apache-2.0 vs a tighter license if you later want a higher price.
5. Decide whether Play is a real channel — the ASO file is not a submission.
