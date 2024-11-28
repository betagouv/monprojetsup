import { Voeu } from "@/features/formation/domain/formation.interface";

export type VoeuProps = {
  voeu: Voeu;
};

export type UseVoeuArgs = {
  voeu: VoeuProps["voeu"];
};
