# Quarkus CFP — Redesign (static HTML)

Six self-contained pages. Each file has all fonts, images, and scripts inlined,
so it opens in any browser with no server, build step, or network access.

## Pages
- index.html            — Landing / Call for Papers
- cfp-detail.html       — CFP detail + session proposals list (Review buttons)
- proposal-review.html  — Single session-proposal review (Approve / Waitlist / Decline)
- cfp-form.html         — Create / Edit CFP form
- submit.html           — 4-step submission wizard (interactive)
- reviews.html          — Reviewer queue

Pages link to each other by these filenames, so keep them in the same folder.

## Design tokens (for integration)
- Fonts:  Red Hat Display (headings), Red Hat Text (body), Red Hat Mono (labels/code)
- Colors: background #0c1826 · surface #122438 · Quarkus blue #4695eb ·
          light blue #7cb3f4 · Quarkus red #ff004a · open/green #3fe08f
- Logo:   official Quarkus icon (OSS) in the header; lightning-bolt mark in the hero

These are static mockups: form fields and review buttons are visual only (except the
submit wizard's step navigation, which is interactive). Wire them to your CFP API when
integrating.
