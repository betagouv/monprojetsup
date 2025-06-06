import { Spécialité } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import Fuse from "fuse.js";

export class RechercherSpécialitésUseCase {
  public run(recherche: string, spécialités: Spécialité[]): Spécialité[] {
    const fuse = new Fuse<Spécialité>(spécialités, {
      distance: 200,
      threshold: 0.4,
      keys: ["nom"],
    });

    return fuse.search(recherche).map((correspondance) => correspondance.item);
  }
}
