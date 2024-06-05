import AxeBuilder from "@axe-core/playwright";
import { expect, test } from "@playwright/test";

const pages = [
  "/",
  "/inscription/projet",
  "/inscription/scolarite",
  "/inscription/domaines",
  "/inscription/interets",
  "/inscription/metiers",
  "/inscription/etude",
  "/inscription/formations",
  "/inscription/confirmation",
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
