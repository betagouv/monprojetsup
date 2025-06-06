import {
  MétiersAccessiblesFicheFormationProps,
  UseMétiersAccessiblesFicheFormationArgs,
} from "./MétiersAccessiblesFicheFormation.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { createModal } from "@codegouvfr/react-dsfr/Modal";
import { useMemo, useState } from "react";

export default function useMétiersAccessiblesFicheFormation({ métiers }: UseMétiersAccessiblesFicheFormationArgs) {
  const [métierSélectionné, setMétierSélectionné] = useState<MétiersAccessiblesFicheFormationProps["métiers"][number]>(
    métiers[0],
  );

  const { estMétierFavoriPourÉlève } = useÉlève();

  const métiersTriésParFavoris = [...métiers].sort((a, b) => {
    const aEstFavori = estMétierFavoriPourÉlève(a.id);
    const bEstFavori = estMétierFavoriPourÉlève(b.id);

    if (aEstFavori && !bEstFavori) {
      return -1;
    } else if (!aEstFavori && bEstFavori) {
      return 1;
    }

    return 0;
  });

  const modaleMétier = useMemo(
    () =>
      createModal({
        id: "modale-métier",
        isOpenedByDefault: false,
      }),
    [],
  );

  return {
    modaleMétier,
    métiersTriésParFavoris,
    métierSélectionné,
    setMétierSélectionné,
  };
}
