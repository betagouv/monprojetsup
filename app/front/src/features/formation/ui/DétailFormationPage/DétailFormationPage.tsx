import Head from "@/components/_layout/Head/Head";
import Bouton from "@/components/Bouton/Bouton";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import {
  récupérerFormationQueryOptions,
  suggérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import { useQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";
import { useState } from "react";

const DétailFormationPage = () => {
  const [afficherBarreLatéraleEnMobile, setAfficherBarreLatéraleEnMobile] = useState(false);

  const route = getRouteApi("/_auth/formations/$formationId/");
  const { formationId } = route.useParams();

  const { data: suggestions } = useQuery(suggérerFormationsQueryOptions);
  const { data: formation } = useQuery(récupérerFormationQueryOptions(formationId));

  if (!formation || !suggestions) {
    return null;
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
      <Head title={formation.nom} />
      <div className={classBackgroundEnFonctionDeAfficherLaBarreLatérale()}>
        <div className="fr-container lg:grid lg:grid-flow-col">
          <div className={`grid gap-8 lg:sticky lg:left-0 lg:top-0 lg:max-h-screen ${afficherLaBarreLatérale()}`}>
            <div className="px-2 pt-6 xl:px-7">
              <div className="ml-[-1rem] lg:hidden">
                <Bouton
                  auClic={() => setAfficherBarreLatéraleEnMobile(!afficherBarreLatéraleEnMobile)}
                  icône={{ classe: "fr-icon-arrow-left-line", position: "gauche" }}
                  label={i18n.PAGE_FORMATION.BOUTON_AFFICHER_FICHE_FORMATION}
                  type="button"
                  variante="quaternaire"
                />
              </div>
              <p className="mb-0 text-center">
                {i18n.PAGE_FORMATION.SUGGESTIONS_TRIÉES_AFFINITÉ}{" "}
                <LienInterne
                  ariaLabel={i18n.PAGE_FORMATION.SUGGESTIONS_TRIÉES_AFFINITÉ_SUITE}
                  href="/profil"
                  variante="simple"
                >
                  {i18n.PAGE_FORMATION.SUGGESTIONS_TRIÉES_AFFINITÉ_SUITE}
                </LienInterne>
              </p>
            </div>
            <ListeFormations
              formationIdAffichée={formationId}
              formations={suggestions}
            />
          </div>
          <div className={`bg-white ${afficherLaFicheFormation()}`}>
            <FicheFormation
              afficherBarreLatéraleCallback={() => setAfficherBarreLatéraleEnMobile(!afficherBarreLatéraleEnMobile)}
              formation={formation}
            />
          </div>
        </div>
      </div>
    </>
  );
};

export default DétailFormationPage;
