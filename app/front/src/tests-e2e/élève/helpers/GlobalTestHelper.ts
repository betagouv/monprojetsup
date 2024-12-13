import { type Page } from "@playwright/test";

export class GlobalTestHelper {
  public PAGE_FORMATIONS = "/formations";

  public PAGE_FAVORIS = "/favoris";

  public PAGE_TABLEAU_DE_BORD = "/";

  public PAGE_PROFIL = "/profil";

  public constructor(protected _page: Page) {}

  public toast = () => {
    return this._page.locator("#contenu").getByRole("status");
  };

  public lien = (nom: string) => {
    return this._page.getByRole("link", { name: nom });
  };
}
