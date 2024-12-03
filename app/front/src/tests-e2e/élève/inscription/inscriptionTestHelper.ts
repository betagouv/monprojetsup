import { i18n } from "@/configuration/i18n/i18n";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { TestHelper } from "@/tests-e2e/testHelper";
import { type Page } from "@playwright/test";

export class InscriptionTestHelper extends TestHelper {
  public constructor(
    protected _page: Page,
    public readonly urlPageCourante: string,
    public readonly urlPageSuivante: string,
    private readonly _profilÉlèveParDéfaut?: Partial<Élève>,
  ) {
    super(_page);
  }

  protected _boutonSoumissionFormulaire = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.CONTINUER });
  };

  protected _boutonRetour = () => {
    return this._page.getByRole("link", { name: i18n.COMMUN.RETOUR });
  };

  public naviguerVersLaPage = async () => {
    if (this._profilÉlèveParDéfaut) {
      await this.initialiserProfilÉlèveParDéfaut(this._profilÉlèveParDéfaut);
    }

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

  public listeDesFavorisSuggérés = () => {
    return this._page.getByRole("list", { name: i18n.ACCESSIBILITÉ.LISTE_SUGGESTIONS_FAVORIS });
  };

  public listeDesFavorisSélectionnés = () => {
    return this._page.getByRole("list", { name: i18n.ACCESSIBILITÉ.LISTE_FAVORIS_SÉLECTIONNÉS });
  };

  public boutonFavoriSuggéré = (nom: string) => {
    return this.listeDesFavorisSuggérés().getByRole("listitem").filter({ hasText: nom }).getByRole("button");
  };

  public boutonFavoriSélectionné = (nom: string) => {
    return this.listeDesFavorisSélectionnés().getByRole("listitem").filter({ hasText: nom }).getByRole("button");
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
      name: nom,
      exact: true,
    });
  };
}
