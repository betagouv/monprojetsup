import { environnement } from "@/configuration/environnement";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { useSearch } from "@tanstack/react-router";
import { useMemo } from "react";
import { useAuth } from "react-oidc-context";

export default function useUtilisateur() {
  const auth = useAuth();
  const paramètresURL: { simulerCompte: "élève" | "expert" } = useSearch({
    strict: false,
  });

  const seDéconnecter = async () => {
    localStorage.clear();
    queryClient.removeQueries();
    await auth.signoutRedirect({ id_token_hint: auth.user?.id_token });
  };

  const récupérerInformationsUtilisateur = useMemo(() => {
    if (environnement.VITE_TEST_MODE && paramètresURL?.simulerCompte === "expert") {
      return {
        id: "expert",
        prénom: "hugo",
        nom: "expert",
        email: "expert@example.com",
        estExpert: true,
      };
    }

    if (environnement.VITE_TEST_MODE) {
      return {
        id: "eleve",
        prénom: "nina",
        nom: "élève",
        email: "eleve@example.com",
        estExpert: false,
      };
    }

    return {
      id: auth.user?.profile.sub,
      prénom: auth.user?.profile.given_name,
      nom: auth.user?.profile.family_name,
      email: auth.user?.profile.email,
      estExpert: auth.user?.profile.profile === "expert",
    };
  }, [auth.user, paramètresURL?.simulerCompte]);

  return {
    ...récupérerInformationsUtilisateur,
    seDéconnecter,
  };
}
