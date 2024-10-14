import { type UseSpécialitésScolaritéFormArgs } from "./ScolaritéForm.interface";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { type SpécialitéBac } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import Fuse from "fuse.js";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useSpécialitésScolaritéForm({
  référentielDonnées,
  valeurBac,
  setValue,
  getValues,
}: UseSpécialitésScolaritéFormArgs) {
  const CHAMP_SPÉCIALITÉS = "spécialités";
  const [rechercheSpécialité, setRechercheSpécialité] = useState<string>("");
  const [spécialitésAssociéesAuBacSélectionné, setSpécialitésAssociéesAuBacSélectionné] = useState<SpécialitéBac[]>([]);

  const spécialitésSélectionnéesParDéfautIds = useMemo(() => getValues(CHAMP_SPÉCIALITÉS), [getValues]);

  const spécialitéVersOptionSpécialité = useCallback(
    (spécialité: SpécialitéBac): SélecteurMultipleOption => ({
      valeur: spécialité.id,
      label: spécialité.nom,
    }),
    [],
  );

  const auChangementDesSpécialitésSélectionnées = (spécialitésSélectionnées: SélecteurMultipleOption[]) => {
    setValue(
      CHAMP_SPÉCIALITÉS,
      spécialitésSélectionnées.map((spécialité) => spécialité.valeur),
    );
  };

  const spécialitésSélectionnéesParDéfaut = useMemo<SélecteurMultipleOption[]>(
    () =>
      spécialitésAssociéesAuBacSélectionné
        .filter((spécialité) => spécialitésSélectionnéesParDéfautIds?.includes(spécialité.id))
        .map(spécialitéVersOptionSpécialité),
    [spécialitéVersOptionSpécialité, spécialitésAssociéesAuBacSélectionné, spécialitésSélectionnéesParDéfautIds],
  );

  const spécialitésSuggérées = useMemo<SélecteurMultipleOption[]>(() => {
    if (rechercheSpécialité.length < 2) return [];

    const fuse = new Fuse<SpécialitéBac>(spécialitésAssociéesAuBacSélectionné, {
      distance: 200,
      threshold: 0.4,
      keys: ["nom"],
    });

    return fuse
      .search(rechercheSpécialité)
      .map((correspondance) => spécialitéVersOptionSpécialité(correspondance.item));
  }, [rechercheSpécialité, spécialitéVersOptionSpécialité, spécialitésAssociéesAuBacSélectionné]);

  // Gestion lors du changement du bac
  useEffect(() => {
    setValue(CHAMP_SPÉCIALITÉS, []);
    const bacSélectionné = référentielDonnées?.bacs.find((bac) => bac.id === valeurBac);
    setSpécialitésAssociéesAuBacSélectionné(bacSélectionné?.spécialités ?? []);
  }, [référentielDonnées, setValue, valeurBac]);

  // Gestion si données existantes
  useEffect(() => {
    if (spécialitésSélectionnéesParDéfautIds && spécialitésSélectionnéesParDéfautIds?.length > 0)
      setValue(CHAMP_SPÉCIALITÉS, spécialitésSélectionnéesParDéfautIds);
  }, [setValue, spécialitésSélectionnéesParDéfautIds]);

  return {
    bacADesSpécialités: spécialitésAssociéesAuBacSélectionné.length > 0,
    spécialitésSuggérées,
    spécialitésSélectionnéesParDéfaut,
    auChangementDesSpécialitésSélectionnées,
    àLaRechercheDUneSpécialité: setRechercheSpécialité,
  };
}
