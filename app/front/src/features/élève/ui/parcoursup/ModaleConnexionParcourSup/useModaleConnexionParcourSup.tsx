import { générerPKCECodeChallenge, générerPKCECodeVerifier } from "@/utils/crypto";

export default function useModaleConnexionParcourSup() {
  // eslint-disable-next-line unicorn/consistent-function-scoping
  const redirigerVersAuthParcourSup = async () => {
    const codeVerifier = générerPKCECodeVerifier();
    const codeChallenge = await générerPKCECodeChallenge(codeVerifier);

    const paramètresDeRequêteAuthorizePS = new URLSearchParams();
    paramètresDeRequêteAuthorizePS.append("client_id", "monProjetSup");
    paramètresDeRequêteAuthorizePS.append("response_type", "code");
    paramètresDeRequêteAuthorizePS.append("scope", "openid read:favoris");
    paramètresDeRequêteAuthorizePS.append("state", "d7a619c8e91f48dda4ba3b052c8469cc");
    paramètresDeRequêteAuthorizePS.append("code_challenge_method", "S256");
    paramètresDeRequêteAuthorizePS.append("code_challenge", codeChallenge);
    paramètresDeRequêteAuthorizePS.append("redirect_uri", encodeURI("http://localhost:5001/parcoursup-callback"));
    sessionStorage.setItem("psCodeVerifier", codeVerifier);
    sessionStorage.setItem("psRedirectUri", encodeURI("http://localhost:5001/parcoursup-callback"));

    window.location.href = `https://authentification.parcoursup.fr/Authentification/oauth2/authorize?${paramètresDeRequêteAuthorizePS.toString()}`;
  };

  return {
    redirigerVersAuthParcourSup,
  };
}
