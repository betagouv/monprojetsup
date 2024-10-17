import { générerPKCECodeChallenge, générerPKCECodeVerifier } from "@/utils/crypto";

export default function useModaleConnexionParcourSup() {
  // eslint-disable-next-line unicorn/consistent-function-scoping
  const connecterLesComptesPSetMPS = async () => {
    const codeVerifier = générerPKCECodeVerifier();
    const codeChallenge = await générerPKCECodeChallenge(codeVerifier);

    window.location.href = `http://authentification.parcoursup.fr/Authentification/oauth2/authorize?
                            client_id=monProjetSup
                            &response_type=code
                            &scope=openid
                            &state=d7a619c8e91f48dda4ba3b052c8469cc
                            &code_challenge=${codeChallenge}
                            &code_challenge_method=S256
                            &redirect_uri=${encodeURI("http://localhost:5001/parcoursup-callback")}`;
  };

  return {
    connecterLesComptesPSetMPS,
  };
}
