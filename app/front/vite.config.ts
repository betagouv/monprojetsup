import { TanStackRouterVite } from "@tanstack/router-vite-plugin";
import react from "@vitejs/plugin-react";
import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";

export default defineConfig({
  resolve: {
    alias: [{ find: "@", replacement: fileURLToPath(new URL("src", import.meta.url)) }],
  },
  server: {
    host: "0.0.0.0",
    // Si changement, modifier le .env du repo pour faire correspondre le port
    port: 5_001,
  },
  plugins: [react(), TanStackRouterVite()],
  css: {
    preprocessorOptions: {
      scss: {
        api: "modern-compiler",
      },
    },
  },
});
