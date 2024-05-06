import { type router } from "@/configuration/lib/tanstack-router";

export type CarteTableauDeBordÉlèveProps = {
  titre: string;
  sousTitre: string;
  illustration: string;
  lien: keyof (typeof router)["routesByPath"];
};
