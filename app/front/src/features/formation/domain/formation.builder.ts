import { type Formation } from "./formation.interface";

export class FormationBuilder {
  private formation: Formation;

  public constructor() {
    this.formation = {
      id: "1234",
      nom: "Nom générique",
      descriptifs: {
        général: null,
        spécialités: null,
        attendu: null,
      },
      urls: [],
      métiersAccessibles: [],
    };
  }

  public avecId(id: string): FormationBuilder {
    this.formation.id = id;
    return this;
  }

  public avecNom(nom: string): FormationBuilder {
    this.formation.nom = nom;
    return this;
  }

  public avecDescriptifs(descriptifs: Formation["descriptifs"]): FormationBuilder {
    this.formation.descriptifs = descriptifs;
    return this;
  }

  public avecUrls(urls: string[]): FormationBuilder {
    this.formation.urls = urls;
    return this;
  }

  public avecMétiersAccessibles(métiersAccessibles: Formation["métiersAccessibles"]): FormationBuilder {
    this.formation.métiersAccessibles = métiersAccessibles;
    return this;
  }

  public avecAffinité(affinité: number): FormationBuilder {
    this.formation.affinité = affinité;
    return this;
  }

  public construire(): Formation {
    return this.formation;
  }
}
