import { type FormationFavorite } from "@/features/élève/domain/élève.interface.ts";

export type ÉtablissementLienExterneProps = {
  établissement: { urlParcoursup: string; id: string; nom: string };
  mettreÀJourUnVoeu: (voeu: FormationFavorite["voeux"][number]) => void;
  estFavoris: boolean;
};
