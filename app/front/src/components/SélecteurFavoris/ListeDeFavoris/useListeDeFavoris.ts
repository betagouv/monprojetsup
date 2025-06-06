import { UseListeDeFavorisArgs } from "./ListeDeFavoris.interface";
import { useEffect, useId, useState } from "react";

export default function useListeDeFavoris({ favoris, nombreFavorisAffichésParDéfaut }: UseListeDeFavorisArgs) {
  const id = useId().replaceAll(":", "");
  const [nombreFavorisAffichés, setNombreFavorisAffichés] = useState<number>(nombreFavorisAffichésParDéfaut);

  useEffect(() => {
    if (nombreFavorisAffichés !== nombreFavorisAffichésParDéfaut) {
      const lienFavoriSuivant = document.querySelector<HTMLElement>(
        `#liste-favoris-${id} > ul > li:nth-child(${nombreFavorisAffichés - nombreFavorisAffichésParDéfaut + 1}) > div > a`,
      );

      if (lienFavoriSuivant) {
        lienFavoriSuivant?.focus();
      } else {
        const togglePrécédent = document.querySelector<HTMLElement>(
          `#liste-favoris-${id} > ul > li:nth-child(${nombreFavorisAffichés - nombreFavorisAffichésParDéfaut}) > button`,
        );
        togglePrécédent?.focus();
      }
    }
  }, [nombreFavorisAffichés]);

  return {
    nombreFavorisAffichés,
    favorisAffichés: favoris.slice(0, nombreFavorisAffichés),
    afficherPlusDeFavoris: () => setNombreFavorisAffichés(nombreFavorisAffichés + nombreFavorisAffichésParDéfaut),
    id,
  };
}
