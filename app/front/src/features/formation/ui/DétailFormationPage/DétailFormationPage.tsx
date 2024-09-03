import Head from "@/components/_layout/Head/Head";
import Bouton from "@/components/Bouton/Bouton";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import { queryClient } from "@/configuration/lib/tanstack-query";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import {
  récupérerFormationQueryOptions,
  suggérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";
import { useState } from "react";

const DétailFormationPage = () => {
  const [afficherBarreLatérale, setAfficherBarreLatérale] = useState(false);
  const route = getRouteApi("/_auth/formations/$formationId/");
  const { formationId } = route.useParams();

  const suggestions = queryClient.getQueryData(suggérerFormationsQueryOptions.queryKey);

  useSuspenseQuery(récupérerFormationQueryOptions(formationId));
  const formation = queryClient.getQueryData(récupérerFormationQueryOptions(formationId).queryKey);

  if (!formation || !suggestions) {
    return null;
  }

  const afficherLaBarreLatérale = () => {
    return afficherBarreLatérale ? "" : "hidden md:block";
  };

  const afficherLaFicheFormation = () => {
    return afficherBarreLatérale ? "hidden md:block" : "";
  };

  const classBackgroundEnFonctionDeAfficherLaBarreLatérale = () => {
    if (afficherBarreLatérale) return "bg-[--background-contrast-beige-gris-galet]";

    return "md:bg-gradient-to-r md:from-[--background-contrast-beige-gris-galet] md:from-50% md:to-white md:to-50% bg-white";
  };

  return (
    <>
      <Head title={formation.nom} />
      <div className={`h-full ${classBackgroundEnFonctionDeAfficherLaBarreLatérale()}`}>
        <div className="fr-container grid grid-flow-col">
          <div className={`grid gap-8 pr-4 pt-6 xl:pr-10 ${afficherLaBarreLatérale()}`}>
            <div className="ml-[-1rem] pb-6 md:hidden">
              <Bouton
                auClic={() => setAfficherBarreLatérale(!afficherBarreLatérale)}
                icône={{ classe: "fr-icon-arrow-left-line", position: "gauche" }}
                label={i18n.PAGE_FORMATION.BOUTON_AFFICHER_FICHE_FORMATION}
                type="button"
                variante="quaternaire"
              />
            </div>
            <p className="mb-0 text-center">
              Suggestions triées par affinité d’après{" "}
              <LienInterne
                ariaLabel="tes préférences"
                href="/profil"
                variante="simple"
              >
                tes préférences ›
              </LienInterne>
            </p>
            <ListeFormations formations={suggestions} />
          </div>
          <div className={afficherLaFicheFormation()}>
            <FicheFormation
              afficherBarreLatéraleCallback={() => setAfficherBarreLatérale(!afficherBarreLatérale)}
              formation={formation}
            />
          </div>
        </div>
      </div>
    </>
  );
};

export default DétailFormationPage;
