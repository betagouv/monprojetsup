import { i18n } from "../../../src/configuration/i18n/i18n";
import { expect, test } from "@playwright/test";

test.describe("Inscription élève - Confirmation", () => {
  test("J'ai un bouton qui me permet d'accéder à mon tableau de bord", async ({ page }) => {
    // GIVEN
    await page.goto("/eleve/inscription/confirmation");

    // WHEN
    await page.getByRole("link", { name: i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.BOUTON_ACTION }).click();
    await page.waitForURL("/eleve/tableau-de-bord");

    // THEN
    expect(page.url()).toMatch("/eleve/tableau-de-bord");
  });
});
