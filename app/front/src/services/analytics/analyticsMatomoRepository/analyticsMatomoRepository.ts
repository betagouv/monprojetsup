import { environnement } from "@/configuration/environnement.ts";
import { AnalyticsRepository } from "@/services/analytics/analytics.interface";

export class AnalyticsMatomoRepository implements AnalyticsRepository {
  public estInitialisé() {
    return Boolean(environnement.VITE_MATOMO_URL && environnement.VITE_MATOMO_SITE_ID && window._paq);
  }

  public envoyerPageVue(url: string): void {
    this._push(["setReferrerUrl", window.document.referrer]);
    this._push(["setCustomUrl", url]);
    this._push(["trackPageView"]);
  }

  public envoyerÉvènement(catégorie: string, action: string, nom: string) {
    this._push(["trackEvent", catégorie, action, nom]);
  }

  public estOptOut(): Promise<boolean> {
    return new Promise((resolve) => {
      this._push([
        function (this: { isUserOptedOut: () => boolean }) {
          resolve(this.isUserOptedOut());
        },
      ]);
    });
  }

  public async changerConsentementMatomo(): Promise<void> {
    const estOptOut = await this.estOptOut();

    if (estOptOut) {
      this._push(["forgetUserOptOut"]);
    } else {
      this._push(["optUserOut"]);
    }
  }

  private _push(args: unknown[]): void {
    if (!this.estInitialisé()) return;

    window._paq.push(args);
  }
}
