# Frontend Guide

## Prerequisites

- Node.js 18+
- Java 25 + Maven (or the Maven wrapper `./mvnw`)
- Docker (for Quarkus Dev Services / PostgreSQL)

---

## Running the Full Stack

```bash
cd src/main/webui && npm install  # first time only
./mvnw quarkus:dev
```

Quarkus starts PostgreSQL via Dev Services, seeds the database from `import.sql`, then starts Vite on `:5173` as a subprocess. The browser talks to `http://localhost:8080` only — Quinoa proxies requests to Vite transparently.

Hot Module Replacement (HMR) works for frontend changes. Quarkus hot reload works for backend changes.

---

## UI-Only Dev (no Quarkus restart needed)

If you only need to iterate on the frontend and the backend is already running:

```bash
cd src/main/webui
npm run dev      # Vite on :5173, proxies API calls to :8080
```

The `vite.config.js` proxy forwards `/cfp`, `/presenters`, and `/session-proposals` to `http://localhost:8080`.

---

## Directory Map

```
src/main/webui/src/
├── api/               # HTTP client modules
│   ├── http.js        # shared request() helper
│   ├── cfpApi.js      # GET/POST/PUT/DELETE /cfp
│   ├── presenterApi.js
│   └── sessionProposalApi.js
├── components/
│   ├── layout/        # NavBar, Layout (shell with <Outlet>)
│   ├── ui/            # Button, FormField, LoadingSpinner, ErrorAlert
│   └── cfp/           # CfpCard, CfpForm, TrackEditor, FormatEditor
├── hooks/
│   ├── useCfps.js     # fetches GET /cfp
│   └── useCfp.js      # fetches GET /cfp/:id
└── pages/
    ├── cfp/           # CfpListPage, CfpCreatePage, CfpDetailPage, CfpEditPage
    └── wizard/        # WizardPage, Step1–Step4
```

---

## Adding a CRUD Page

Checklist for a new resource (e.g. `Widget`):

1. **API module** — add `src/api/widgetApi.js` with `list`, `get`, `create`, `update`, `delete` methods using the shared `request()` from `http.js`. Use relative paths (`/widgets`).
2. **Hook** — add `hooks/useWidgets.js` (list hook with `reload`) and/or `hooks/useWidget.js` (single item).
3. **Form** — add `components/widget/WidgetForm.jsx` using `useForm` from React Hook Form.
4. **Pages** — add `pages/widget/{List,Create,Detail,Edit}Page.jsx`.
5. **Routes** — add routes in `App.jsx`:
   ```jsx
   <Route path="/widgets" element={<WidgetListPage />} />
   <Route path="/widgets/new" element={<WidgetCreatePage />} />
   <Route path="/widgets/:id" element={<WidgetDetailPage />} />
   <Route path="/widgets/:id/edit" element={<WidgetEditPage />} />
   ```
6. **Nav link** — add a `<NavLink>` in `components/layout/NavBar.jsx`.

---

## Extending the Wizard

The wizard is a single `/submit` route. `WizardPage.jsx` owns all state (`step`, `wizardData`, `cfpId`). Steps are rendered conditionally based on `step` (integer 0–4, where 0 is the CFP selector pre-step).

To add a step between existing steps:

1. Create `pages/wizard/StepNNew.jsx` with `({ wizardData, onNext, onBack })` props.
2. Merge new data into `wizardData` by calling `onNext(newData)` (WizardPage merges it with `setWizardData(prev => ({ ...prev, ...newData }))`).
3. Increment `STEP_LABELS` in `WizardPage.jsx` and update the step number conditionals.

---

## API Module Conventions

All API modules import `request` from `./http.js`. The helper:

- Adds `Content-Type: application/json` on requests with a body.
- Throws an `Error` with `.status` (HTTP status) and `.body` (parsed JSON or text) on non-2xx.
- Returns `null` on 204 No Content.

Use relative paths so the modules work in both dev (Vite proxy) and production (same-origin Quinoa serving).

Email addresses in URL path segments must be encoded:

```js
const encoded = encodeURIComponent(email)
return request(`/presenters/${encoded}`)
```

---

## Tailwind Conventions

- Use `inputClass(hasError)` exported from `components/ui/FormField.jsx` for all `<input>`, `<select>`, and `<textarea>` elements to get consistent focus rings and error states.
- Use the `<Button>` component with `variant="primary|secondary|danger"` rather than inline Tailwind button classes.
- Use `<FormField label="…" error={…}>` as a wrapper for all form inputs — it renders the label and inline error message consistently.

---

## Quarkus Validation Error Shape

When the backend returns HTTP 400 (Bean Validation failure), the response body is:

```json
{
  "title": "Constraint Violation",
  "status": 400,
  "violations": [
    { "field": "createSessionProposal.arg0.title", "message": "must not be blank" }
  ]
}
```

The `request()` helper in `http.js` throws with `e.body` set to this object. You can extract `e.body.violations` to show per-field errors.
