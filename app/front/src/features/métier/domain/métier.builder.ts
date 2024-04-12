import { type Métier } from "./métier.interface";

export class MétierBuilder {
  private métier: Métier;

  public constructor() {
    this.métier = {
      id: "1234",
      nom: "Nom générique",
      descriptif: null,
      urls: [],
      formations: [],
    };
  }

  public avecId(id: string): MétierBuilder {
    this.métier.id = id;
    return this;
  }

  public avecNom(nom: string): MétierBuilder {
    this.métier.nom = nom;
    return this;
  }

  public avecDescriptif(descriptif: string): MétierBuilder {
    this.métier.descriptif = descriptif;
    return this;
  }

  public avecUrls(urls: string[]): MétierBuilder {
    this.métier.urls = urls;
    return this;
  }

  public avecFormations(formations: Métier["formations"]): MétierBuilder {
    this.métier.formations = formations;
    return this;
  }

  public construire(): Métier {
    return this.métier;
  }
}
