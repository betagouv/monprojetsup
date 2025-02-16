import FicheFormation from "./FicheFormation/FicheFormation";
import ListeEtAperçuContenu from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuContenu/ListeEtAperçuContenu";
import ListeEtAperçuLayout from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuLayout";
import {
  actionsListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import { useLocation } from "@tanstack/react-router";

const FormationSansRecherchePage = () => {
  const { hash } = useLocation();
  const { changerAfficherBarreLatéraleEnMobile } = actionsListeEtAperçuStore();
  changerAfficherBarreLatéraleEnMobile(false);

  return (
    <ListeEtAperçuLayout variante="formations" forcerMasquageBarreLatérale={true}>
      <ListeEtAperçuContenu forcerMasquageBarreLatérale={true}>{hash && <FicheFormation id={hash} afficherBoutonFavori={false}/>}</ListeEtAperçuContenu>
    </ListeEtAperçuLayout>
);
};

export default FormationSansRecherchePage;
