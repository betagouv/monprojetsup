import AxeBuilder from "@axe-core/playwright";
import { expect, test } from "@playwright/test";

const pages = ["/", "/inscription/projet"];

for (const testedPage of pages) {
  test(`la page "${testedPage}" valide tous les tests d'accessibilité`, async ({ page }) => {
    await page.goto(testedPage, { waitUntil: "networkidle" });

    const accessibilityScanResults = await new AxeBuilder({ page }).analyze();

    expect(accessibilityScanResults.violations).toEqual([]);
  });
}
