import { environnement } from "@/configuration/environnement.ts";

export class Matomo {
  public estInitialisé() {
    return environnement.VITE_MATOMO_URL && environnement.VITE_MATOMO_SITE_ID && window._paq;
  }

  private _push(args: unknown[]): void {
    if (!this.estInitialisé()) return;

    window._paq.push(args);
  }

  public envoyerPageVue(url: string): void {
    this._push(["setReferrerUrl", window.document.referrer]);
    this._push(["setCustomUrl", url]);
    this._push(["deleteCustomVariables", "state"]);
    this._push(["deleteCustomVariables", "session_state"]);
    this._push(["deleteCustomVariables", "iss"]);
    this._push(["deleteCustomVariables", "code"]);
    this._push(["trackPageView"]);
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
}
