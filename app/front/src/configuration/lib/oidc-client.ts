import { env } from "@/configuration/environnement";
import { UserManager, WebStorageStateStore } from "oidc-client-ts";

export const userManagerOIDCClient = new UserManager({
  authority: env.VITE_KEYCLOAK_ROYAUME_URL,
  client_id: env.VITE_KEYCLOAK_CLIENT_ID,
  client_secret: env.VITE_KEYCLOAK_CLIENT_SECRET, // Oui je sais c'est bizarre de mettre un secret côté front mais c'est la configuration Avenir(s) :)
  redirect_uri: window.location.href,
  post_logout_redirect_uri: env.VITE_PUBLIC_WEBSITE_URL,
  userStore: new WebStorageStateStore({ store: window.sessionStorage }),
  monitorSession: true,
});

export const àLaConnexionOIDCClientCallback = () => {
  window.history.replaceState({}, document.title, window.location.pathname);
};
