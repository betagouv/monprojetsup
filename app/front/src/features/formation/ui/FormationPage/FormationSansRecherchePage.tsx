import FicheFormation from "./FicheFormation/FicheFormation";
import ListeEtAperçuContenu from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuContenu/ListeEtAperçuContenu";
import ListeEtAperçuLayout from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuLayout";
import { actionsListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import useFormation from "@/features/formation/ui/useFormation";
import { useLocation, useRouterState } from "@tanstack/react-router";
import { useEffect } from "react";

const FormationSansRecherchePage = () => {
  const { hash } = useLocation();
  const { pathname } = useRouterState().location;
  const { changerAfficherBarreLatéraleEnMobile, changerÉlémentAffiché } = actionsListeEtAperçuStore();
  const { estUnIdDeFormation } = useFormation();

  useEffect(() => {
    changerAfficherBarreLatéraleEnMobile(false);
    if (pathname === "/formation" && hash !== "" && estUnIdDeFormation(hash)) {
      changerÉlémentAffiché({
        id: hash,
        type: "formation",
      });
    }
  }, [changerAfficherBarreLatéraleEnMobile, changerÉlémentAffiché, estUnIdDeFormation, hash, pathname]);

  if (pathname !== "/formation" || hash === "" || !estUnIdDeFormation(hash)) {
    return <AnimationChargement />;
  }

  return (
    <ListeEtAperçuLayout>
      <ListeEtAperçuContenu>
        <FicheFormation
          afficherBoutonFavori={true}
          id={hash}
        />
      </ListeEtAperçuContenu>
    </ListeEtAperçuLayout>
  );
};

export default FormationSansRecherchePage;
