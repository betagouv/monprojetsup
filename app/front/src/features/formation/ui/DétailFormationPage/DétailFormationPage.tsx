/* eslint-disable sonarjs/no-nested-conditional */
import Head from "@/components/_layout/Head/Head";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";
import BoutonRetourAuxSuggestions from "@/features/formation/ui/BoutonRetourAuxSuggestions/BoutonRetourAuxSuggestions";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import {
  rechercherFormationsQueryOptions,
  récupérerFormationQueryOptions,
  suggérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import RechercheFormations from "@/features/formation/ui/RechercheFormations/RechercheFormations";
import {
  actionsFicheFormationStore,
  formationAffichéeIdFicheFormationStore,
} from "@/features/formation/ui/store/useFicheFormation/useFicheFormation";
import { useQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";
import { useEffect, useState } from "react";

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
    récupérerFormationQueryOptions(
      formationAffichéeId ?? résultatsDeRecherche?.[0]?.id ?? suggestions?.[0]?.id ?? null,
    ),
  );
  const { changerFormationAffichéeId } = actionsFicheFormationStore();

  useEffect(() => {
    changerFormationAffichéeId(undefined);
  }, [changerFormationAffichéeId]);

  if (!résultatsDeRecherche && !suggestions && !formation) {
    return <AnimationChargement />;
  }

  const afficherLaBarreLatérale = () => {
    return afficherBarreLatéraleEnMobile ? "" : "hidden lg:grid";
  };

  const afficherLaFicheFormation = () => {
    return afficherBarreLatéraleEnMobile ? "hidden lg:block" : "";
  };

  const classBackgroundEnFonctionDeAfficherLaBarreLatérale = () => {
    if (afficherBarreLatéraleEnMobile) return "bg-[--background-contrast-beige-gris-galet] h-full";

    return "bg-white lg:bg-gradient-to-r lg:from-[--background-contrast-beige-gris-galet] lg:from-50% lg:to-white lg:to-50% h-full";
  };

  return (
    <>
      <Head title={formation?.nom ?? ""} />
      <div className={classBackgroundEnFonctionDeAfficherLaBarreLatérale()}>
        <div className="fr-container h-full lg:grid lg:grid-cols-[450px_1fr] xl:grid-cols-[490px_1fr]">
          <div
            className={`grid content-start gap-8 overflow-hidden pt-6 lg:sticky lg:left-0 lg:top-0 lg:max-h-screen ${afficherLaBarreLatérale()}`}
          >
            <div className="grid gap-6 px-2 lg:px-7">
              <div className="ml-[-1rem] lg:hidden">
                <Bouton
                  auClic={() => setAfficherBarreLatéraleEnMobile(!afficherBarreLatéraleEnMobile)}
                  icône={{ classe: "fr-icon-arrow-left-line", position: "gauche" }}
                  label={i18n.PAGE_FORMATION.BOUTON_AFFICHER_FICHE_FORMATION}
                  type="button"
                  variante="quaternaire"
                />
              </div>
              <div className="grid gap-6">
                <div className="[&_.fr-input]:bg-white">
                  <RechercheFormations valeurParDéfaut={recherche} />
                </div>
                {résultatsDeRecherche?.[0]?.id && <BoutonRetourAuxSuggestions />}
              </div>
            </div>
            {!suggestions && !résultatsDeRecherche ? (
              <AnimationChargement />
            ) : (
              <ListeFormations
                formationIdAffichée={
                  formationAffichéeId ?? résultatsDeRecherche?.[0]?.id ?? suggestions?.[0]?.id ?? null
                }
                formations={résultatsDeRecherche ?? suggestions ?? []}
              />
            )}
          </div>
          <div className={`bg-white ${afficherLaFicheFormation()}`}>
            {formation === null ? null : formation && !chargementFormationEnCours ? (
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
