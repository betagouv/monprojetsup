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
import { formationAffichéeIdFicheFormationStore } from "@/features/formation/ui/store/useFicheFormation/useFicheFormation";
import { useQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";
import { useState } from "react";

const DétailFormationPage = () => {
  const [afficherBarreLatéraleEnMobile, setAfficherBarreLatéraleEnMobile] = useState(false);
  const formationAffichéeId = formationAffichéeIdFicheFormationStore();
  const route = getRouteApi("/_auth/formations/");
  const { recherche } = route.useSearch();

  const { data: suggestions } = useQuery({
    ...suggérerFormationsQueryOptions,
    enabled: recherche === undefined,
  });

  const { data: résultatsDeRecherche } = useQuery({
    ...rechercherFormationsQueryOptions(recherche),
    enabled: recherche !== undefined,
  });

  const { data: formation, isFetching: chargementFormationEnCours } = useQuery(
    récupérerFormationQueryOptions(formationAffichéeId ?? résultatsDeRecherche?.[0].id ?? suggestions?.[0].id ?? ""),
  );

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
              <div>
                <div className="[&_.fr-input]:bg-white">
                  <RechercheFormations />
                </div>
              </div>
            </div>
            {!suggestions && !résultatsDeRecherche ? (
              <AnimationChargement />
            ) : (
              <ListeFormations
                formationIdAffichée={formationAffichéeId ?? résultatsDeRecherche?.[0].id ?? suggestions?.[0].id ?? ""}
                formations={résultatsDeRecherche ?? suggestions ?? []}
              />
            )}
          </div>
          <div className={`bg-white ${afficherLaFicheFormation()}`}>
            {formation && !chargementFormationEnCours ? (
              <FicheFormation
                afficherBarreLatéraleCallback={() => setAfficherBarreLatéraleEnMobile(!afficherBarreLatéraleEnMobile)}
                formation={formation}
              />
            ) : (
              <AnimationChargement />
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default DétailFormationPage;
