import { dépendances } from "@/configuration/dépendances/dépendances";

export class RequêteInvalideErreurHttp extends Error {
  estConsignéeManuellement = false;

  constructor(protected contexte: unknown) {
    super();
    Object.setPrototypeOf(this, RequêteInvalideErreurHttp.prototype);
    this.name = "HTTP - Erreur 400 - Requête invalide";
    dépendances.logger.consigner("error", this, contexte);
    this.estConsignéeManuellement = true;
  }
}

export class NonIdentifiéErreurHttp extends Error {
  estConsignéeManuellement = true;

  constructor(protected contexte: unknown) {
    super();
    Object.setPrototypeOf(this, NonIdentifiéErreurHttp.prototype);
    this.name = "HTTP - Erreur 401 - Non identifié";
    dépendances.logger.consigner("info", this, contexte);
    this.estConsignéeManuellement = true;
  }
}

export class NonAutoriséErreurHttp extends Error {
  estConsignéeManuellement = true;

  constructor(protected contexte: unknown) {
    super();
    Object.setPrototypeOf(this, NonAutoriséErreurHttp.prototype);
    this.name = "HTTP - Erreur 403 - Interdit";
    dépendances.logger.consigner("info", this, contexte);
    this.estConsignéeManuellement = true;
  }
}

export class RessourceNonTrouvéeErreurHttp extends Error {
  estConsignéeManuellement = false;

  constructor(protected contexte: unknown) {
    super();
    Object.setPrototypeOf(this, RessourceNonTrouvéeErreurHttp.prototype);
    this.name = "HTTP - Erreur 404 - Ressource non trouvée";
    dépendances.logger.consigner("info", this, contexte);
    this.estConsignéeManuellement = true;
  }
}

export class ServeurTemporairementIndisponibleErreurHttp extends Error {
  estConsignéeManuellement = true;

  constructor(protected contexte: unknown) {
    super();
    Object.setPrototypeOf(this, ServeurTemporairementIndisponibleErreurHttp.prototype);
    this.name = "HTTP - Erreur 503 - Serveur temporairement indisponible";
    dépendances.logger.consigner("log", this, contexte);
    this.estConsignéeManuellement = true;
  }
}

export class ErreurInterneServeurErreurHttp extends Error {
  estConsignéeManuellement = false;

  constructor(protected contexte: unknown) {
    super();
    Object.setPrototypeOf(this, ErreurInterneServeurErreurHttp.prototype);
    this.name = "HTTP - Erreur 500 - Erreur interne serveur";
    dépendances.logger.consigner("error", this, contexte);
    this.estConsignéeManuellement = true;
  }
}

export class CodeRéponseInattenduErreurHttp extends Error {
  estConsignéeManuellement = false;

  constructor(
    protected contexte: unknown,
    protected statusCode: number,
  ) {
    super();
    Object.setPrototypeOf(this, CodeRéponseInattenduErreurHttp.prototype);
    this.name = `HTTP - Erreur ${statusCode} - code réponse inattendu`;
    dépendances.logger.consigner("error", this, contexte);
    this.estConsignéeManuellement = true;
  }
}

export class RequêteAnnuléeErreurHttp extends Error {
  estConsignéeManuellement = true;

  constructor(protected contexte: unknown) {
    super();
    Object.setPrototypeOf(this, RequêteAnnuléeErreurHttp.prototype);
    this.name = `HTTP - requête interrompue`;
    dépendances.logger.consigner("debug", this, contexte);
    this.estConsignéeManuellement = true;
  }
}

export class ErreurRéseauErreurHttp extends Error {
  estConsignéeManuellement = true;

  constructor(protected contexte: unknown) {
    super();
    Object.setPrototypeOf(this, ErreurRéseauErreurHttp.prototype);
    this.name = `HTTP - erreur réseau`;
    dépendances.logger.consigner("debug", this, contexte);
    this.estConsignéeManuellement = true;
  }
}

export class ErreurInconnueErreurHttp extends Error {
  estConsignéeManuellement = false;

  constructor(protected contexte: unknown) {
    super();
    Object.setPrototypeOf(this, ErreurInconnueErreurHttp.prototype);
    this.name = "HTTP - Erreur inconnue";
    dépendances.logger.consigner("error", this, contexte);
    this.estConsignéeManuellement = true;
  }
}
