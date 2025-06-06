import { type RéférentielDonnées } from "@/features/référentielDonnées/domain/référentielDonnées.interface";

export type RéférentielDonnéesRepository = {
  récupérer: () => Promise<RéférentielDonnées | Error>;
};
