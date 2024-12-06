import { type CommuneÉlève } from "@/features/élève/domain/élève.interface";

export type VoeuxOngletUneCommuneProps = {
  codeCommune: CommuneÉlève["codeInsee"];
};

export type UseVoeuxOngletUneCommuneArgs = {
  codeCommune: VoeuxOngletUneCommuneProps["codeCommune"];
};
