import { type CommuneFavorite } from "@/features/élève/domain/élève.interface";

export type VoeuxOngletUneCommuneProps = {
  codeCommune: CommuneFavorite["codeInsee"];
};

export type UseVoeuxOngletUneCommuneArgs = {
  codeCommune: VoeuxOngletUneCommuneProps["codeCommune"];
};
