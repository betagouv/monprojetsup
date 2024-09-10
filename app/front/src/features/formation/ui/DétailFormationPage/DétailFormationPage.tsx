import Head from "@/components/_layout/Head/Head";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import {
  rechercherFormationsQueryOptions,
  récupérerFormationQueryOptions,
  suggérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import RechercheFormations from "@/features/formation/ui/RechercheFormations/RechercheFormations";
import { useQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";
import { useState } from "react";

const DétailFormationPage = () => {
  const [afficherBarreLatéraleEnMobile, setAfficherBarreLatéraleEnMobile] = useState(false);

  const route = getRouteApi("/_auth/formations/$formationId/");
  const { formationId } = route.useParams();

  const { data: suggestions } = useQuery(suggérerFormationsQueryOptions);
  const { data: résultatsDeRecherche, isFetching: rechercheEnCours } = useQuery({
    ...rechercherFormationsQueryOptions(),
    enabled: false,
  });

  const { data: formation, isFetching: récupérationFormationEnCours } = useQuery(
    récupérerFormationQueryOptions(formationId),
  );

  if (!suggestions) {
    return <AnimationChargement />;
  }

  const afficherLaBarreLatérale = () => {
    return afficherBarreLatéraleEnMobile ? "" : "hidden lg:grid";
  };

  const afficherLaFicheFormation = () => {
    return afficherBarreLatéraleEnMobile ? "hidden lg:block" : "";
  };

  const classBackgroundEnFonctionDeAfficherLaBarreLatérale = () => {
    if (afficherBarreLatéraleEnMobile) return "bg-[--background-contrast-beige-gris-galet]";

    return "bg-white lg:bg-gradient-to-r lg:from-[--background-contrast-beige-gris-galet] lg:from-50% lg:to-white lg:to-50%";
  };

  return (
    <>
      <Head title={formation?.nom ?? ""} />
      <div className={classBackgroundEnFonctionDeAfficherLaBarreLatérale()}>
        <div className="fr-container lg:grid lg:grid-cols-[490px_1fr]">
          <div
            className={`grid content-start gap-8 pt-6 lg:sticky lg:left-0 lg:top-0 lg:max-h-screen ${afficherLaBarreLatérale()}`}
          >
            <div className="grid gap-6 px-2 xl:px-7">
              <div className="ml-[-1rem] lg:hidden">
                <Bouton
                  auClic={() => setAfficherBarreLatéraleEnMobile(!afficherBarreLatéraleEnMobile)}
                  icône={{ classe: "fr-icon-arrow-left-line", position: "gauche" }}
                  label={i18n.PAGE_FORMATION.BOUTON_AFFICHER_FICHE_FORMATION}
                  type="button"
                  variante="quaternaire"
                />
              </div>
              <div className="[&_.fr-input]:bg-white">
                <RechercheFormations />
              </div>
            </div>
            {rechercheEnCours ? (
              <AnimationChargement />
            ) : (
              <ListeFormations
                formationIdAffichée={formationId}
                formations={résultatsDeRecherche ?? suggestions}
              />
            )}
          </div>
          <div className={`bg-white ${afficherLaFicheFormation()}`}>
            {récupérationFormationEnCours || !formation ? (
              <AnimationChargement />
            ) : (
              <FicheFormation
                afficherBarreLatéraleCallback={() => setAfficherBarreLatéraleEnMobile(!afficherBarreLatéraleEnMobile)}
                formation={formation}
              />
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default DétailFormationPage;
