import { dépendances } from "@/configuration/dépendances/dépendances";
import { useSearch } from "@tanstack/react-router";
import { useEffect } from "react";

const TemporaryConnexionParcourSup = () => {
  const router = useSearch({ from: "/parcoursup-callback/" });
  useEffect(() => {
    const lol = async () => {
      const associationRéussie = await dépendances.associerCompteParcourSupÉlèveUseCase.run(
        sessionStorage.getItem("psCodeVerifier") ?? "",
        router.code,
        sessionStorage.getItem("psRedirectUri") ?? "",
      );

      console.log({ associationRéussie });
    };

    lol();
  }, [router.code]);

  return (
    <div>
      <p>Remove me</p>
    </div>
  );
};

export default TemporaryConnexionParcourSup;
