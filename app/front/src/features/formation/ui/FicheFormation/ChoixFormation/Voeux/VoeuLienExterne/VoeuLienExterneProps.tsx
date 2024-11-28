import { type FormationFavorite } from "@/features/élève/domain/élève.interface.ts";

export type VoeuLienExterneProps = {
  voeu: { urlParcoursup: string; id: string; nom: string };
  mettreÀJourUnVoeu: (voeu: FormationFavorite["voeux"][number]) => void;
  estFavoris: boolean;
};
