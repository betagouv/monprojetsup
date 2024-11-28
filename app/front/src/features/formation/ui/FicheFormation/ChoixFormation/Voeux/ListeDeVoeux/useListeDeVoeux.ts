import { constantes } from "@/configuration/constantes";
import { UseListeDeVoeuxArgs } from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/ListeDeVoeux/ListeDeVoeux.interface.ts";
import { useEffect, useState } from "react";

export default function useListeDeVoeux({ voeux }: UseListeDeVoeuxArgs) {
  const [nombreVoeuxAffichés, setNombreVoeuxAffichés] = useState<number>(constantes.VOEUX.NB_VOEUX_PAR_PAGE);

  useEffect(() => {
    if (nombreVoeuxAffichés !== constantes.VOEUX.NB_VOEUX_PAR_PAGE) {
      const element = document.querySelector<HTMLElement>(
        `#liste-voeux > ul > li:nth-child(${nombreVoeuxAffichés - constantes.VOEUX.NB_VOEUX_PAR_PAGE + 1}) > div > a`,
      );
      element?.focus();
    }
  }, [nombreVoeuxAffichés]);

  const afficherPlusDeRésultats = () => {
    setNombreVoeuxAffichés(nombreVoeuxAffichés + constantes.VOEUX.NB_VOEUX_PAR_PAGE);
  };

  return {
    nombreVoeuxAffichés,
    voeuxAffichés: voeux.slice(0, nombreVoeuxAffichés),
    afficherPlusDeRésultats,
  };
}
