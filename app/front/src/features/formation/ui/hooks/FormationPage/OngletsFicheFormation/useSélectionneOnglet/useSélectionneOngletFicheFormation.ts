import { UseSélectionneOngletFicheFormationArgs } from "./useSélectionneOngletFicheFormationArgs.interface";
import { dépendances } from "@/configuration/dépendances/dépendances";

export default function useSélectionneOngletFicheFormation(args: UseSélectionneOngletFicheFormationArgs) {
  if (typeof args.tabId === "string") {
    return dépendances.voirOngletFormationUseCase.run(args.formationId, args.tabId);
  } else {
    return Promise.resolve();
  }
}
