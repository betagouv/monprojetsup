import { type router } from "@/configuration/lib/tanstack-router";

export type TagCliquableProps =
  | {
      libellé: string;
      taille?: "sm";
      hrefInterne: never;
      hrefExterne: string;
    }
  | {
      libellé: string;
      taille?: "sm";
      hrefInterne: keyof (typeof router)["routesByPath"];
      hrefExterne: never;
    };
