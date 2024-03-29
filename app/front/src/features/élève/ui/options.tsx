import { dependencies } from "@/configuration/dependencies/dependencies";
import { CréerÉlèveUseCase } from "@/features/élève/usecase/CréerÉlève";
import { RécupérerÉlèveUseCase } from "@/features/élève/usecase/RécupérerÉlève";
import { queryOptions } from "@tanstack/react-query";

export const élèveQueryOptions = queryOptions({
  queryKey: ["élève"],
  queryFn: async () => {
    const élève = await new RécupérerÉlèveUseCase(dependencies.élèveRepository).run();

    if (!élève) {
      return await new CréerÉlèveUseCase(dependencies.élèveRepository).run();
    }

    return élève;
  },
});
