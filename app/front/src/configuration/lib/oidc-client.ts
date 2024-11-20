import { environnement } from "@/configuration/environnement";
import { UserManager, WebStorageStateStore } from "oidc-client-ts";

export const userManagerOIDCClient = new UserManager({
  authority: environnement.VITE_KEYCLOAK_ROYAUME_URL,
  client_id: environnement.VITE_KEYCLOAK_CLIENT_ID,
  // Oui je sais c'est bizarre de mettre un secret côté front mais c'est la configuration Avenir(s) :)
  client_secret: environnement.VITE_KEYCLOAK_CLIENT_SECRET,
  redirect_uri: window.location.origin + window.location.pathname,
  post_logout_redirect_uri: environnement.VITE_APP_URL,
  userStore: new WebStorageStateStore({ store: window.sessionStorage }),
  monitorSession: true,
});

export const àLaConnexionOIDCClientCallback = () => {
  window.history.replaceState({}, document.title, window.location.pathname);
};
