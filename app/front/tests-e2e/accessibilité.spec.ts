import AxeBuilder from "@axe-core/playwright";
import { expect, test } from "@playwright/test";

const pages = [
  "/",
  "/eleve/inscription/projet",
  "/eleve/inscription/scolarite",
  "/eleve/inscription/domaines",
  "/eleve/inscription/interets",
  "/eleve/inscription/metiers",
  "/eleve/inscription/etude",
  "/eleve/inscription/formations",
  "/eleve/inscription/confirmation",
  "/eleve/tableau-de-bord",
  "/eleve/profil",
];

for (const testedPage of pages) {
  test(`la page "${testedPage}" valide tous les tests d'accessibilité`, async ({ page }) => {
    await page.goto(testedPage, { waitUntil: "networkidle" });
    await new Promise((resolve) => {
      setTimeout(resolve, 500);
    });

    const accessibilityScanResults = await new AxeBuilder({ page }).analyze();

    expect(accessibilityScanResults.violations).toEqual([]);
  });
}
