import { createEnv } from "@t3-oss/env-core";
import { z } from "zod";

export const env = createEnv({
  client: {
    VITE_APP_URL: z.string().url(),
    VITE_API_URL: z.string().url(),
    VITE_PUBLIC_WEBSITE_URL: z.string().url(),
    VITE_KEYCLOAK_ROYAUME_URL: z.string().url(),
    VITE_KEYCLOAK_CLIENT_ID: z.string(),
    VITE_KEYCLOAK_CLIENT_SECRET: z.string(),
    VITE_TEST_MODE: z
      .string()
      .refine((variable) => variable === "true" || variable === "false")
      .transform((variable) => variable === "true"),
  },
  clientPrefix: "VITE_",
  runtimeEnv: import.meta.env,
  emptyStringAsUndefined: true,
});
