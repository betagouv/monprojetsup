import FicheFormation from "./FicheFormation/FicheFormation";
import ListeEtAperçuContenu from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuContenu/ListeEtAperçuContenu";
import ListeEtAperçuLayout from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuLayout";
import { actionsListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import { useLocation } from "@tanstack/react-router";

const FormationSansRecherchePage = () => {
  const { hash } = useLocation();
  const { changerAfficherBarreLatéraleEnMobile } = actionsListeEtAperçuStore();
  changerAfficherBarreLatéraleEnMobile(false);

  return (
    <ListeEtAperçuLayout
      forcerMasquageBarreLatérale
      variante="formations"
    >
      <ListeEtAperçuContenu forcerMasquageBarreLatérale>
        {hash && (
          <FicheFormation
            afficherBoutonFavori={false}
            id={hash}
          />
        )}
      </ListeEtAperçuContenu>
    </ListeEtAperçuLayout>
  );
};

export default FormationSansRecherchePage;
