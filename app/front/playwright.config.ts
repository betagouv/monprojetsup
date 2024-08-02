import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
  testDir: "./src/tests-e2e",
  outputDir: "./src/tests-e2e/results",
  reporter: [["html", { outputFolder: "./src/tests-e2e/reports" }]],
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
    port: 4_173,
    env: {
      VITE_TEST_MODE: "true",
    },
    reuseExistingServer: false,
  },
});
