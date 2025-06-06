import { createEnv } from "@t3-oss/env-core";
import { z } from "zod";

export const environnement = createEnv({
  client: {
    VITE_ENV: z.string(),
    VITE_APP_URL: z.string().url(),
    VITE_API_URL: z.string().url(),
    VITE_PUBLIC_WEBSITE_URL: z.string().url(),
    VITE_AVENIRS_URL: z.string().url(),
    VITE_MATOMO_URL: z.string().url(),
    VITE_KEYCLOAK_URL: z.string().url(),
    VITE_KEYCLOAK_ROYAUME: z.string(),
    VITE_KEYCLOAK_CLIENT_ID: z.string(),
    VITE_KEYCLOAK_CLIENT_SECRET: import.meta.env.VITE_TEST_MODE === "true" ? z.string().optional() : z.string(),
    VITE_TEST_MODE: z
      .string()
      .refine((variable) => variable === "true" || variable === "false")
      .transform((variable) => variable === "true"),
    VITE_FF_MOYENNE_GENERALE: z
      .string()
      .optional()
      .refine((variable) => variable === "true" || variable === "false" || variable === undefined)
      .transform((variable) => variable === "true"),
    VITE_PARCOURSUP_OAUTH2_URL: import.meta.env.VITE_PARCOURSUP_OAUTH2_CLIENT
      ? z.string()
      : z.string().url().optional(),
    VITE_PARCOURSUP_OAUTH2_CLIENT: import.meta.env.VITE_PARCOURSUP_OAUTH2_URL ? z.string() : z.string().optional(),
    VITE_SENTRY_DSN:
      import.meta.env.VITE_SENTRY_ORG || import.meta.env.VITE_SENTRY_PROJET ? z.string() : z.string().url().optional(),
    VITE_SENTRY_ORG:
      import.meta.env.VITE_SENTRY_DSN || import.meta.env.VITE_SENTRY_PROJET ? z.string() : z.string().optional(),
    VITE_SENTRY_PROJET:
      import.meta.env.VITE_SENTRY_DSN || import.meta.env.VITE_SENTRY_ORG ? z.string() : z.string().optional(),
    VITE_LAISSER_AVIS_URL: z.string().url().optional(),
    VITE_MATOMO_SITE_ID: z.string().optional(),
  },
  clientPrefix: "VITE_",
  runtimeEnv: import.meta.env,
  emptyStringAsUndefined: true,
});
