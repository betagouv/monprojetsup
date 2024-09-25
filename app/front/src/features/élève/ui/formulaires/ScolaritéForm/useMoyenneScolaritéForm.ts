import { type UseMoyenneFormArgs } from "./ScolaritéForm.interface";
import { useEffect, useMemo } from "react";

export default function useMoyenneForm({ watch, setValue, getValues }: UseMoyenneFormArgs) {
  const MOYENNE_PAR_DÉFAUT = 14;
  const NE_VEUT_PAS_RÉPONDRE_VALEUR = -1;

  const valeurClasse = watch("classe");
  const valeurMoyenneGénérale = watch("moyenneGénérale");

  const valeurParDéfautMoyenne = useMemo(() => getValues("moyenneGénérale") ?? MOYENNE_PAR_DÉFAUT, [getValues]);
  const afficherChampMoyenne = useMemo(() => valeurClasse === "terminale", [valeurClasse]);
  const neVeutPasRépondreMoyenne = useMemo(
    () => valeurMoyenneGénérale === NE_VEUT_PAS_RÉPONDRE_VALEUR,
    [NE_VEUT_PAS_RÉPONDRE_VALEUR, valeurMoyenneGénérale],
  );

  const auClicSurNeVeutPasRépondreMoyenne = (neVeutPasRépondre: boolean) =>
    setValue("moyenneGénérale", neVeutPasRépondre ? NE_VEUT_PAS_RÉPONDRE_VALEUR : MOYENNE_PAR_DÉFAUT);

  useEffect(() => {
    if (afficherChampMoyenne) {
      setValue("moyenneGénérale", valeurParDéfautMoyenne ?? MOYENNE_PAR_DÉFAUT);
    } else {
      setValue("moyenneGénérale", NE_VEUT_PAS_RÉPONDRE_VALEUR);
    }
  }, [NE_VEUT_PAS_RÉPONDRE_VALEUR, afficherChampMoyenne, setValue, valeurParDéfautMoyenne]);

  return {
    valeurParDéfautMoyenne,
    neVeutPasRépondreMoyenne,
    afficherChampMoyenne,
    valeurMoyenneGénérale: valeurMoyenneGénérale ?? MOYENNE_PAR_DÉFAUT,
    auClicSurNeVeutPasRépondreMoyenne,
  };
}
