import { queryClient } from "@/configuration/lib/tanstack-query";
import { détailFormationQueryOptions } from "@/features/formation/ui/options";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";

const DétailFormationPage = () => {
  const route = getRouteApi("/formations/$formationId/");
  const { formationId } = route.useParams();
  useSuspenseQuery(détailFormationQueryOptions(formationId));
  const formation = queryClient.getQueryData(détailFormationQueryOptions(formationId).queryKey);

  return <>{JSON.stringify(formation)}</>;
};

export default DétailFormationPage;
