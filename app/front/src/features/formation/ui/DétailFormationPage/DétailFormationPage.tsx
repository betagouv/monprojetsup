import Head from "@/components/_layout/Head/Head";
import { queryClient } from "@/configuration/lib/tanstack-query";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import { récupérerFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";

const DétailFormationPage = () => {
  const route = getRouteApi("/_auth/formations/$formationId/");
  const { formationId } = route.useParams();

  useSuspenseQuery(récupérerFormationQueryOptions(formationId));
  const formation = queryClient.getQueryData(récupérerFormationQueryOptions(formationId).queryKey);

  if (!formation) {
    return null;
  }

  return (
    <>
      <Head title={formation.nom} />
      <div className="fr-container">
        <FicheFormation formation={formation} />
      </div>
    </>
  );
};

export default DétailFormationPage;
