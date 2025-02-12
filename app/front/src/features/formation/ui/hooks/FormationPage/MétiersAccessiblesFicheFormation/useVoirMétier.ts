import { dépendances } from "@/configuration/dépendances/dépendances";

export default function UseVoirMétier(metierId: string) {
  return dépendances.voirMétierUseCase.run(metierId);
}
