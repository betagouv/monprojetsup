import { Spécialité } from "@/features/référentielDonnées/domain/référentielDonnées.interface";

export type RechercheSpécialitésProps = {
  spécialitésBac: Spécialité[];
};

export type UseRechercheSpécialitésArgs = {
  spécialitésBac: RechercheSpécialitésProps["spécialitésBac"];
};
