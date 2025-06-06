import { sentryVitePlugin } from "@sentry/vite-plugin";
import { TanStackRouterVite } from "@tanstack/router-vite-plugin";
import react from "@vitejs/plugin-react";
import { fileURLToPath } from "node:url";
import { defineConfig, loadEnv } from "vite";

export default ({ mode }: { mode: string }) => {
  process.env = { ...process.env, ...loadEnv(mode, process.cwd()) };

  return defineConfig({
    resolve: {
      alias: [{ find: "@", replacement: fileURLToPath(new URL("src", import.meta.url)) }],
    },

    server: {
      host: "0.0.0.0",
      // Si changement, modifier le .env du repo pour faire correspondre le port
      port: 5_001,
    },
    plugins: [
      react(),
      TanStackRouterVite(),
      sentryVitePlugin({
        org: process.env.VITE_SENTRY_ORG,
        project: process.env.VITE_SENTRY_PROJET,
        authToken: process.env.VITE_SENTRY_AUTH_TOKEN,
        telemetry: false,
      }),
    ],

    css: {
      preprocessorOptions: {
        scss: {
          api: "modern-compiler",
        },
      },
    },

    build: {
      sourcemap: true,
    },
  });
};
