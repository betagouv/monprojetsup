import { type BacOptions, type ClasseOptions, type useScolaritéFormArgs } from "./ScolaritéForm.interface";
import { scolaritéValidationSchema } from "./ScolaritéForm.validation";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { type SpécialitéBac } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { useQuery } from "@tanstack/react-query";
import Fuse from "fuse.js";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useScolaritéForm({ àLaSoumissionDuFormulaireAvecSuccès }: useScolaritéFormArgs) {
  const CHAMP_SPÉCIALITÉS = "spécialités";

  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const [rechercheSpécialité, setRechercheSpécialité] = useState<string>();
  const [spécialitésAssociéesAuBacSélectionné, setSpécialitésAssociéesAuBacSélectionné] = useState<SpécialitéBac[]>();
  const [spécialitésSélectionnéesParDéfaut, setSpécialitésSélectionnéesParDéfaut] = useState<SpécialitéBac[]>([]);
  const [spécialitésSuggérées, setSpécialitésSuggérées] = useState<SpécialitéBac[]>([]);

  const { register, erreurs, mettreÀJourÉlève, watch, setValue, getValues } = useÉlèveForm({
    schémaValidation: scolaritéValidationSchema(référentielDonnées?.bacs ?? []),
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const spécialitésSélectionnéesParDéfautIds = useMemo(() => getValues(CHAMP_SPÉCIALITÉS), [getValues]);
  const valeurBac = watch("bac");

  const classeOptions: ClasseOptions =
    référentielDonnées?.élève.classes.map((classe) => ({
      valeur: classe,
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS[classe].LABEL,
    })) ?? [];

  const bacOptions: BacOptions =
    référentielDonnées?.bacs.map((bac) => ({
      valeur: bac.id,
      label: bac.nom,
    })) ?? [];

  const spécialitéVersOptionSpécialité = useCallback(
    (spécialité: SpécialitéBac) => ({
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

  useEffect(() => {
    if (!spécialitésAssociéesAuBacSélectionné || !spécialitésSélectionnéesParDéfautIds) return;

    setSpécialitésSélectionnéesParDéfaut(
      spécialitésAssociéesAuBacSélectionné.filter((spécialité) =>
        spécialitésSélectionnéesParDéfautIds.includes(spécialité.id),
      ),
    );
  }, [spécialitésAssociéesAuBacSélectionné, spécialitésSélectionnéesParDéfautIds]);

  useEffect(() => {
    if (!rechercheSpécialité || rechercheSpécialité.length < 2 || !spécialitésAssociéesAuBacSélectionné) return;

    const fuse = new Fuse<SpécialitéBac>(spécialitésAssociéesAuBacSélectionné, {
      distance: 200,
      threshold: 0.4,
      keys: ["nom"],
    });

    const correspondances = fuse.search(rechercheSpécialité);

    setSpécialitésSuggérées(correspondances.map((correspondance) => correspondance.item));
  }, [rechercheSpécialité, spécialitésAssociéesAuBacSélectionné]);

  useEffect(() => {
    setValue(CHAMP_SPÉCIALITÉS, []);
  }, [setValue, valeurBac]);

  useEffect(() => {
    const bacSélectionné = référentielDonnées?.bacs.find((bac) => bac.id === valeurBac);
    setSpécialitésAssociéesAuBacSélectionné(bacSélectionné?.spécialités);
  }, [référentielDonnées?.bacs, valeurBac]);

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    classeOptions,
    bacOptions,
    valeurBac,
    bacADesSpécialités: Boolean(
      spécialitésAssociéesAuBacSélectionné && spécialitésAssociéesAuBacSélectionné.length > 0,
    ),
    spécialitésSuggérées: spécialitésSuggérées.map(spécialitéVersOptionSpécialité),
    spécialitésSélectionnéesParDéfaut: spécialitésSélectionnéesParDéfaut.map(spécialitéVersOptionSpécialité),
    auChangementDesSpécialitésSélectionnées,
    àLaRechercheDUneSpécialité: setRechercheSpécialité,
  };
}
