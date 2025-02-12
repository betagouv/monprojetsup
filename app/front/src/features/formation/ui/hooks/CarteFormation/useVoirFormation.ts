import { dépendances } from "@/configuration/dépendances/dépendances";

export default function useVoirFormation(formationId: string) {
  return dépendances.voirFicheFormationUseCase.run(formationId);
}
