import { type router } from "@/configuration/lib/tanstack-router";
import { type ReactNode } from "react";

type ParamètresPath<H> = H extends keyof (typeof router)["routesByPath"]
  ? (typeof router)["routesByPath"][H]["types"]["allParams"]
  : never;

type ParamètresSearch<H> = H extends keyof (typeof router)["routesByPath"]
  ? (typeof router)["routesByPath"][H]["types"]["searchSchema"]
  : never;

export type LienInterneProps<H extends keyof (typeof router)["routesByPath"]> = {
  children: ReactNode;
  href: keyof (typeof router)["routesByPath"];
  ariaLabel: string;
  hash?: string;
  paramètresPath?: ParamètresPath<H>;
  paramètresSearch?: ParamètresSearch<H>;
  taille?: "petit" | "grand";
  variante?: "neutre" | "simple";
  icône?: {
    position: "droite" | "gauche";
    classe: string;
  };
  estUnTag?: boolean;
};
