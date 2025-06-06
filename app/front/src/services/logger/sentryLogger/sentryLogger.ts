import { sentryConfiguration } from "@/configuration/lib/sentry";
import { Logger } from "@/services/logger/logger.interface";
import { SeverityLevel } from "@sentry/react";
import * as Sentry from "@sentry/react";

export class SentryLogger implements Logger {
  public constructor() {
    Sentry.init(sentryConfiguration);
  }

  public consigner(niveau: SeverityLevel, erreur: Error, contexte?: unknown) {
    Sentry.withScope((scope) => {
      scope.setLevel(niveau);
      scope.setExtra("Données complémentaires pour debug", contexte);
      Sentry.captureException(erreur);
    });
  }
}
