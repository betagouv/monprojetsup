import { BacÉlève, Spécialité } from "@/features/référentielDonnées/domain/référentielDonnées.interface";

export type RechercheSpécialitésProps = {
  bac: BacÉlève;
  spécialitésBac: Spécialité[];
};

export type UseRechercheSpécialitésArgs = {
  bac: RechercheSpécialitésProps["bac"];
  spécialitésBac: RechercheSpécialitésProps["spécialitésBac"];
};
