import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
  testDir: "./tests-e2e",
  outputDir: "./tests-e2e/results",
  reporter: [["html", { outputFolder: "./tests-e2e/reports" }]],
  fullyParallel: true,
  forbidOnly: true,
  use: {
    baseURL: "http://localhost:4173",
    trace: "on-first-retry",
  },
  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"] },
    },
  ],
  webServer: {
    command: "npm run build && npm run preview",
    env: {
      VITE_TEST_MODE: "true",
    },
    url: "http://localhost:4173",
    reuseExistingServer: false,
  },
});
