export class RessourceNonTrouvéeErreur extends Error {
  constructor() {
    super();
    Object.setPrototypeOf(this, RessourceNonTrouvéeErreur.prototype);
  }
}
