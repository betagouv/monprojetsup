import { type router } from "@/configuration/lib/tanstack-router";
import { Paths } from "@/types/commons";
import { type ReactNode } from "react";

type ParamètresPath<H> = H extends Paths ? (typeof router)["routesByPath"][H]["types"]["allParams"] : never;

type ParamètresSearch<H> = H extends Paths ? (typeof router)["routesByPath"][H]["types"]["searchSchema"] : never;

export type LienInterneProps<H extends Paths> = {
  children: ReactNode;
  href: Paths;
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
  auClic?: () => void;
  réinitialiserScroll?: boolean;
};
