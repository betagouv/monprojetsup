import { expect, test } from "@playwright/test";

test.describe("Page d'accueil", () => {
  test("a un titre de niveau 1", async ({ page }) => {
    // WHEN
    await page.goto("/");

    // GIVEN
    const h1 = page.getByRole("heading", { level: 1, name: /accueil/iu });

    // THEN
    await expect(h1).toBeVisible();
  });
});
