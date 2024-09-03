import Head from "@/components/_layout/Head/Head";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { queryClient } from "@/configuration/lib/tanstack-query";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import {
  récupérerFormationQueryOptions,
  suggérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";

const DétailFormationPage = () => {
  const route = getRouteApi("/_auth/formations/$formationId/");
  const { formationId } = route.useParams();

  const suggestions = queryClient.getQueryData(suggérerFormationsQueryOptions.queryKey);

  useSuspenseQuery(récupérerFormationQueryOptions(formationId));
  const formation = queryClient.getQueryData(récupérerFormationQueryOptions(formationId).queryKey);

  if (!formation || !suggestions) {
    return null;
  }

  return (
    <>
      <Head title={formation.nom} />
      <div className="h-full bg-gradient-to-r from-[--background-contrast-beige-gris-galet] from-50% to-white to-50%">
        <div className="fr-container grid grid-flow-col">
          <div className="grid gap-8 pr-10">
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
          <FicheFormation formation={formation} />
        </div>
      </div>
    </>
  );
};

export default DétailFormationPage;
