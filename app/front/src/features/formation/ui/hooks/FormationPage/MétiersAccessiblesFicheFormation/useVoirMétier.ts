import { dépendances } from "@/configuration/dépendances/dépendances";

export default function useVoirMétier(metierId: string) {
  return dépendances.voirMétierUseCase.run(metierId);
}
