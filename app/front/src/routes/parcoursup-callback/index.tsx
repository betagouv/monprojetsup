import { dépendances } from "@/configuration/dépendances/dépendances";
import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

const parcourSupCallbackSearchSchema = z.object({
  code: z.string(),
});

export const Route = createFileRoute("/parcoursup-callback/")({
  validateSearch: (searchParamètres) => parcourSupCallbackSearchSchema.parse(searchParamètres),
  beforeLoad: async ({ search }) => {
    const associationRéussie = await dépendances.associerCompteParcourSupÉlèveUseCase.run(
      sessionStorage.getItem("psCodeVerifier") ?? "",
      search.code,
      sessionStorage.getItem("psRedirectUri") ?? "",
    );

    window.location.href = `/?associationPS=${associationRéussie ? "ok" : "erreur"}`;
  },
});
