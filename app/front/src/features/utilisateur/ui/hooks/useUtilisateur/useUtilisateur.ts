import { env } from "@/configuration/environnement";
import { useSearch } from "@tanstack/react-router";
import { useAuth } from "react-oidc-context";

export default function useUtilisateur() {
  const auth = useAuth();
  const paramètresURL: { simulerCompte: "élève" | "enseignant" } = useSearch({
    strict: false,
  });

  const récupérerProfil = () => {
    if (auth.user?.profile.profile === "APP-SEC") return "élève" as const;
    if (auth.user?.profile.profile === "EDU-SEC") return "enseignant" as const;
    return undefined;
  };

  const seDéconnecter = () => {
    localStorage.clear();
    auth.signoutRedirect({ id_token_hint: auth.user?.id_token });
  };

  const récupérerInformationsUtilisateur = () => {
    if (env.VITE_TEST_MODE && paramètresURL?.simulerCompte === "élève") {
      return {
        type: "élève" as const,
        prénom: "nina",
        nom: "dupont",
        email: "nina@example.com",
      };
    }

    if (env.VITE_TEST_MODE && paramètresURL?.simulerCompte === "enseignant") {
      return {
        type: "enseignant" as const,
        prénom: "hugo",
        nom: "durant",
        email: "hugo@example.com",
      };
    }

    return {
      type: récupérerProfil(),
      prénom: auth.user?.profile.given_name,
      nom: auth.user?.profile.family_name,
      email: auth.user?.profile.email,
    };
  };

  return {
    ...récupérerInformationsUtilisateur(),
    seDéconnecter,
  };
}
