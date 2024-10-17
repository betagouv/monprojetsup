/* eslint-disable import/no-unassigned-import */
import "./configuration/i18n/i18next";
import { userManagerOIDCClient, àLaConnexionOIDCClientCallback } from "./configuration/lib/oidc-client";
import { queryClient } from "./configuration/lib/tanstack-query";
import { router } from "./configuration/lib/tanstack-router";
import "@/style/global.scss";
import { startReactDsfr } from "@codegouvfr/react-dsfr/spa";
import { QueryClientProvider } from "@tanstack/react-query";
import { Link, RouterProvider } from "@tanstack/react-router";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { HelmetProvider } from "react-helmet-async";
import { AuthProvider } from "react-oidc-context";

const rootElement = document.querySelector("#app");

if (rootElement) {
  const root = createRoot(rootElement);

  startReactDsfr({ defaultColorScheme: "light", Link });

  root.render(
    <StrictMode>
      <AuthProvider
        onSigninCallback={àLaConnexionOIDCClientCallback}
        userManager={userManagerOIDCClient}
      >
        <QueryClientProvider client={queryClient}>
          <HelmetProvider>
            <RouterProvider router={router} />
          </HelmetProvider>
        </QueryClientProvider>
      </AuthProvider>
    </StrictMode>,
  );
}
