import { type UseOngletFicheFormationArgs } from "./OngletsFicheFormation.interface";
import useSélectionneOngletFicheFormation from "@/features/formation/ui/hooks/FormationPage/OngletsFicheFormation/useSélectionneOnglet/useSélectionneOngletFicheFormation";

interface UseOngletsFicheFormationReturn {
  changementOngletFicheFormation: ({ tabIndex }: { tabIndex: number }) => Promise<void | Error>;
}

export default function useOngletsFicheFormation({
  formation,
  onglets,
}: UseOngletFicheFormationArgs): UseOngletsFicheFormationReturn {
  return {
    changementOngletFicheFormation: async ({ tabIndex }: { tabIndex: number }) => {
      return useSélectionneOngletFicheFormation({ formationId: formation.id, tabId: onglets[tabIndex] });
    },
  };
}
