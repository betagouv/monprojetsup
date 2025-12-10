import Entête from "@/components/_layout/Entête/Entête";
import PiedDePage from "@/components/_layout/PiedDePage/PiedDePage";
import LienÉvitement from "@/components/LienÉvitement/LienÉvitement";
import Toast from "@/components/Toast/Toast";
import { constantes } from "@/configuration/constantes";
import ModaleParcoursup from "@/features/commune/ui/ModaleParcoursup/ModaleParcoursup.tsx";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève.ts";
import useUtilisateur from "@/features/utilisateur/ui/useUtilisateur.ts";
import { createModal } from "@codegouvfr/react-dsfr/Modal";
import { Outlet } from "@tanstack/react-router";
import { useMemo } from "react";

const MainLayout = () => {
  const utilisateur = useUtilisateur();
  const eleve = useÉlève();

  const modaleParcoursup = useMemo(
    () =>
      createModal({
        id: "modale-parcoursup",
        isOpenedByDefault: utilisateur.estAuthentifié && eleve.élève?.classe === "terminale",
      }),
    [utilisateur.estAuthentifié, eleve.élève?.classe],
  );

  return (
    <>
      <LienÉvitement />
      <Entête />
      <main
        id={constantes.ACCESSIBILITÉ.CONTENU_ID}
        tabIndex={-1}
      >
        <Toast />
        <Outlet />
      </main>
      <section
        id={constantes.ACCESSIBILITÉ.PIED_DE_PAGE_ID}
        tabIndex={-1}
      >
        <PiedDePage />
        <ModaleParcoursup modale={modaleParcoursup} />
      </section>
    </>
  );
};

export default MainLayout;
