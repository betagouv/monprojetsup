import { createEnv } from "@t3-oss/env-core";
import { z } from "zod";

export const environnement = createEnv({
  client: {
    VITE_ENV: z.string(),
    VITE_APP_URL: z.string().url(),
    VITE_API_URL: z.string().url(),
    VITE_PUBLIC_WEBSITE_URL: z.string().url(),
    VITE_KEYCLOAK_ROYAUME_URL: z.string().url(),
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
    VITE_PARCOURSUP_OAUTH2_URL: z.string().url().optional(),
    VITE_PARCOURSUP_OAUTH2_CLIENT: z.string().optional(),
    VITE_SENTRY_DSN: z.string().url().optional(),
    VITE_SENTRY_ORG: z.string().optional(),
    VITE_SENTRY_PROJET: z.string().optional(),
    VITE_LAISSER_AVIS_URL: z.string().url().optional(),
  },
  clientPrefix: "VITE_",
  runtimeEnv: import.meta.env,
  emptyStringAsUndefined: true,
});
