import { type UseMoyenneFormArgs } from "./ScolaritéForm.interface";
import { useEffect, useMemo } from "react";

export default function useMoyenneForm({ watch, setValue, getValues }: UseMoyenneFormArgs) {
  const MOYENNE_PAR_DÉFAUT = 14;
  const NE_VEUT_PAS_RÉPONDRE = -1;

  const classe = watch("classe");
  const moyenneGénérale = watch("moyenneGénérale");

  const moyenneInitiale = useMemo(() => getValues("moyenneGénérale") ?? MOYENNE_PAR_DÉFAUT, [getValues]);
  const afficherChampMoyenne = useMemo(() => classe === "terminale", [classe]);
  const neVeutPasRépondre = useMemo(
    () => moyenneGénérale === NE_VEUT_PAS_RÉPONDRE,
    [NE_VEUT_PAS_RÉPONDRE, moyenneGénérale],
  );

  const auClicSurNeVeutPasRépondre = (état: boolean) =>
    setValue("moyenneGénérale", état ? NE_VEUT_PAS_RÉPONDRE : MOYENNE_PAR_DÉFAUT);

  useEffect(() => {
    setValue("moyenneGénérale", afficherChampMoyenne ? moyenneInitiale : null);
  }, [afficherChampMoyenne, moyenneInitiale, setValue]);

  return {
    neVeutPasRépondreMoyenne: neVeutPasRépondre,
    afficherChampMoyenne,
    moyenneGénérale: moyenneGénérale ?? MOYENNE_PAR_DÉFAUT,
    auClicSurNeVeutPasRépondreMoyenne: auClicSurNeVeutPasRépondre,
  };
}
