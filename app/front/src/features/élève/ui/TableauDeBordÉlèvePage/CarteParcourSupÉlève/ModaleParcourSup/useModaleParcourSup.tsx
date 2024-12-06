import { environnement } from "@/configuration/environnement";
import { i18n } from "@/configuration/i18n/i18n";
import { Paths } from "@/types/commons";
import { générerPKCECodeChallenge, générerPKCECodeVerifier, générerState } from "@/utils/crypto";
import { ModalProps } from "@codegouvfr/react-dsfr/Modal";
import { useMemo } from "react";

const redirigerVersAuthParcourSup = async () => {
  if (!environnement.VITE_PARCOURSUP_OAUTH2_CLIENT || !environnement.VITE_PARCOURSUP_OAUTH2_URL) return;

  const codeVerifier = générerPKCECodeVerifier();
  const codeChallenge = await générerPKCECodeChallenge(codeVerifier);
  const state = générerState();
  const urlCallback: Paths = "/parcoursup-callback";
  const redirectURI = encodeURI(`${window.location.origin}${urlCallback}`);

  const paramètresDeRequêteAuthorizePS = new URLSearchParams();
  paramètresDeRequêteAuthorizePS.append("client_id", environnement.VITE_PARCOURSUP_OAUTH2_CLIENT);
  paramètresDeRequêteAuthorizePS.append("response_type", "code");
  paramètresDeRequêteAuthorizePS.append("scope", "openid read:favoris");
  paramètresDeRequêteAuthorizePS.append("state", state);
  paramètresDeRequêteAuthorizePS.append("code_challenge_method", "S256");
  paramètresDeRequêteAuthorizePS.append("code_challenge", codeChallenge);
  paramètresDeRequêteAuthorizePS.append("redirect_uri", redirectURI);
  sessionStorage.setItem("psCodeVerifier", codeVerifier);
  sessionStorage.setItem("psRedirectUri", redirectURI);

  window.location.href = `${environnement.VITE_PARCOURSUP_OAUTH2_URL}/oauth2/authorize?${paramètresDeRequêteAuthorizePS.toString()}`;
};

export default function useModaleParcourSup() {
  const boutons = useMemo(
    (): ModalProps["buttons"] => [
      {
        children: i18n.COMMUN.FERMER,
        priority: "tertiary",
        size: "large",
      },
      {
        children: i18n.ÉLÈVE.TABLEAU_DE_BORD.MODALE_PARCOURSUP.BOUTON_ACTION,
        iconId: "fr-icon-arrow-right-line",
        iconPosition: "right",
        size: "large",
        onClick: async () => await redirigerVersAuthParcourSup(),
        doClosesModal: false,
      },
    ],
    [],
  );

  return {
    boutons,
  };
}
