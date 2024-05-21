import { i18n } from "../../../src/configuration/i18n/i18n";
import { type Page } from "@playwright/test";

export class InscriptionTestHelper {
  public constructor(
    protected _page: Page,
    private readonly urlPageCourante: string,
    public readonly urlPageSuivante: string,
  ) {}

  protected _boutonSoumissionFormulaire = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.CONTINUER });
  };

  protected _boutonRetour = () => {
    return this._page.getByRole("link", { name: i18n.COMMUN.RETOUR });
  };

  public naviguerVersLaPage = async () => {
    await this._page.goto(this.urlPageCourante);
  };

  public soumettreLeFormulaire = async () => {
    await this._boutonSoumissionFormulaire().click();
  };

  public revenirÀLÉtapePrécédente = async () => {
    await this._boutonRetour().click();
  };

  public messageErreurChampObligatoire = () => {
    return this._page.getByText(i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE);
  };

  public messageErreurAucunRésultat = () => {
    return this._page.getByText(i18n.COMMUN.ERREURS_FORMULAIRES.AUCUN_RÉSULTAT);
  };

  public champRechercheSélecteurMultiple = (label: string) => {
    return this._page.getByRole("searchbox", { name: label });
  };

  public renseignerChampRechercheSélecteurMultiple = async (labelDuChamp: string, recherche: string) => {
    await this.champRechercheSélecteurMultiple(labelDuChamp).fill(recherche);
  };

  public listeDesOptionsSuggérées = () => {
    return this._page.getByTestId("suggérées");
  };

  public listeDesOptionsSélectionnées = () => {
    return this._page.getByTestId("sélectionnées");
  };

  public boutonOptionSuggérée = (nom: string) => {
    return this.listeDesOptionsSuggérées().getByRole("button", { name: nom, exact: true });
  };

  public boutonOptionSélectionnée = (nom: string) => {
    return this.listeDesOptionsSélectionnées().getByRole("button", {
      name: `${i18n.ACCESSIBILITÉ.RETIRER} ${nom}`,
      exact: true,
    });
  };
}
