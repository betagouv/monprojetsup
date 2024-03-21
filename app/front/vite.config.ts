import { TanStackRouterVite } from "@tanstack/router-vite-plugin";
import react from "@vitejs/plugin-react";
import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";

export default defineConfig({
  resolve: {
    alias: [{ find: "@", replacement: fileURLToPath(new URL("src", import.meta.url)) }],
  },
  server: {
    host: true,
    port: 8_000,
  },
  plugins: [react(), TanStackRouterVite()],
});
