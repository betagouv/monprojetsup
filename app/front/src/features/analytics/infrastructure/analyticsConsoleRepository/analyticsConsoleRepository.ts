/* eslint-disable no-console */
import { AnalyticsRepository } from "@/features/analytics/infrastructure/analytics.interface";

export class AnalyticsConsoleRepository implements AnalyticsRepository {
  private _estOptout = false;

  public estInitialisé() {
    return true;
  }

  public envoyerPageVue(url: string): void {
    console.log("[ANALYTICS] page vue ", url);
  }

  public envoyerÉvènement(catégorie: string, action: string, nom: string) {
    console.log("[ANALYTICS] évènement ", catégorie, action, nom);
  }

  public estOptOut() {
    return this._estOptout;
  }

  public changerConsentementMatomo() {
    const estOptOut = this.estOptOut();

    if (estOptOut) {
      this._estOptout = false;
    } else {
      this._estOptout = true;
    }
  }
}
