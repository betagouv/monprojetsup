import { Voeu } from "@/features/formation/domain/formation.interface";
import Fuse from "fuse.js";

export class RechercherVoeuxUseCase {
  public run(recherche: string, voeux: Voeu[]): Voeu[] {
    const fuse = new Fuse<Voeu>(voeux, {
      distance: 200,
      threshold: 0.4,
      keys: ["nom"],
    });

    return fuse.search(recherche).map((correspondance) => correspondance.item);
  }
}
