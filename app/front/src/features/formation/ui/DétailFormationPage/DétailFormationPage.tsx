import Head from "@/components/_layout/Head/Head";
import { queryClient } from "@/configuration/lib/tanstack-query";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import { détailFormationQueryOptions } from "@/features/formation/ui/options";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";

const DétailFormationPage = () => {
  const route = getRouteApi("/formations/$formationId/");
  const { formationId } = route.useParams();

  useSuspenseQuery(détailFormationQueryOptions(formationId));
  const formation = queryClient.getQueryData(détailFormationQueryOptions(formationId).queryKey);

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
