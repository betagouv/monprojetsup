import { environnement } from "@/configuration/environnement";
import { BrowserOptions, browserTracingIntegration, replayIntegration } from "@sentry/react";

export const sentryConfiguration: BrowserOptions = {
  dsn: environnement.VITE_SENTRY_DSN,
  integrations: [browserTracingIntegration(), replayIntegration()],
  tracesSampleRate: 0.3,
  tracePropagationTargets: [environnement.VITE_APP_URL],
  replaysSessionSampleRate: 0.1,
  replaysOnErrorSampleRate: 1,
  environment: environnement.VITE_ENV,
  beforeSend(événement, metadata) {
    const erreur = metadata.originalException;

    if (
      erreur &&
      typeof erreur === "object" &&
      "estConsignéeManuellement" in erreur &&
      erreur?.estConsignéeManuellement
    ) {
      return null;
    }

    return événement;
  },
};
