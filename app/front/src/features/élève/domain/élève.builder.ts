import { type ClasseÉlève, type Élève, type SituationÉlève } from "./élève.interface";

export class ÉlèveBuilder {
  private élève: Élève;

  public constructor() {
    this.élève = {
      id: "123",
      situation: undefined,
      classe: undefined,
      bac: undefined,
      spécialités: undefined,
      domaines: undefined,
      centresIntêrets: undefined,
    };
  }

  public avecId(id: string): ÉlèveBuilder {
    this.élève.id = id;
    return this;
  }

  public avecSituation(situation: SituationÉlève): ÉlèveBuilder {
    this.élève.situation = situation;
    return this;
  }

  public avecClasse(classe: ClasseÉlève): ÉlèveBuilder {
    this.élève.classe = classe;
    return this;
  }

  public avecBac(bac: string): ÉlèveBuilder {
    this.élève.bac = bac;
    return this;
  }

  public avecSpécialités(spécialités: string[]): ÉlèveBuilder {
    this.élève.spécialités = spécialités;
    return this;
  }

  public avecDomaines(domaines: string[]): ÉlèveBuilder {
    this.élève.domaines = domaines;
    return this;
  }

  public avecCentresIntêrets(centresIntêrets: string[]): ÉlèveBuilder {
    this.élève.centresIntêrets = centresIntêrets;
    return this;
  }

  public construire(): Élève {
    return this.élève;
  }
}
