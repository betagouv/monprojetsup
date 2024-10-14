import "@/style/global.scss";
// eslint-disable-next-line import/no-unassigned-import
import "./configuration/i18n/i18next";
import { àLaConnexionOIDCClientCallback, userManagerOIDCClient } from "./configuration/lib/oidc-client";
import { queryClient } from "./configuration/lib/tanstack-query";
import { router } from "./configuration/lib/tanstack-router";
import { QueryClientProvider } from "@tanstack/react-query";
import { RouterProvider } from "@tanstack/react-router";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { HelmetProvider } from "react-helmet-async";
import { AuthProvider } from "react-oidc-context";

const rootElement = document.querySelector("#app");

if (rootElement) {
  const root = createRoot(rootElement);

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
