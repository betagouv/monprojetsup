import { type Élève, type SituationÉlève } from "./élève.interface";

export class ÉlèveBuilder {
  private élève: Élève;

  public constructor() {
    this.élève = {
      id: "1234",
      situation: undefined,
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

  public construire(): Élève {
    return this.élève;
  }
}
