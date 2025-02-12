import { dépendances } from "@/configuration/dépendances/dépendances";
import { UseSélectionneOngletFicheFormationArgs} from "./useSélectionneOngletFicheFormationArgs.interface";

export default function useSélectionneOngletFicheFormation(args: UseSélectionneOngletFicheFormationArgs) {
  if (typeof args.tabId === "string") {
    return dépendances.voirOngletFormationUseCase.run(args.formationId, args.tabId);
  } else {
    return Promise.resolve();
  }
}
