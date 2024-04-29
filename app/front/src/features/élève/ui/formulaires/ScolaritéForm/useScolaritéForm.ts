import { type ClasseOptions, type useScolaritéFormArgs } from "./ScolaritéForm.interface";
import { scolaritéValidationSchema } from "./ScolaritéForm.validation";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { type Spécialité } from "@/features/bac/domain/bac.interface";
import {
  bacsQueryOptions,
  rechercheSpécialitésQueryOptions,
  récupérerSpécialitésQueryOptions,
} from "@/features/bac/ui/options";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useQuery, useSuspenseQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useScolaritéForm({ àLaSoumissionDuFormulaireAvecSuccès }: useScolaritéFormArgs) {
  const CHAMP_SPÉCIALITÉS = "spécialités";
  const [rechercheSpécialité, setRechercheSpécialité] = useState<string>();

  const spécialitéVersOptionSpécialité = useCallback((spécialité: Spécialité) => {
    return {
      valeur: spécialité.id,
      label: spécialité.nom,
    };
  }, []);

  const { data: bacs } = useSuspenseQuery(bacsQueryOptions);

  const { register, erreurs, mettreÀJourÉlève, watch, setValue, getValues } = useÉlèveForm({
    schémaValidation: scolaritéValidationSchema(bacs),
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const valeurBac = watch("bac");

  const spécialitésSélectionnéesParDéfautIds = useMemo(() => getValues(CHAMP_SPÉCIALITÉS), [getValues]);

  const {
    data: spécialités,
    refetch: rechercherSpécialités,
    isFetching: rechercheSpécialitéEnCours,
  } = useQuery(rechercheSpécialitésQueryOptions(valeurBac, rechercheSpécialité));

  const { data: spécialitésDuBac, refetch: rechercherToutesSpécialitésDuBac } = useQuery(
    rechercheSpécialitésQueryOptions(valeurBac),
  );

  const { data: spécialitésSélectionnéesParDéfaut } = useQuery(
    récupérerSpécialitésQueryOptions(spécialitésSélectionnéesParDéfautIds),
  );

  useEffect(() => {
    rechercherSpécialités();
  }, [rechercheSpécialité, rechercherSpécialités]);

  useEffect(() => {
    rechercherToutesSpécialitésDuBac();
    setValue(CHAMP_SPÉCIALITÉS, []);
  }, [rechercherToutesSpécialitésDuBac, setValue, valeurBac]);

  const auChangementDesSpécialitésSélectionnées = (spécialitésSélectionnées: SélecteurMultipleOption[]) => {
    setValue(
      CHAMP_SPÉCIALITÉS,
      spécialitésSélectionnées.map((spécialité) => spécialité.valeur),
    );
  };

  const classeOptions: ClasseOptions = [
    {
      valeur: "seconde",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.SECONDE.LABEL,
    },
    {
      valeur: "seconde_sthr",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.SECONDE_STHR.LABEL,
    },
    {
      valeur: "seconde_tmd",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.SECONDE_TMD.LABEL,
    },
    {
      valeur: "première",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.PREMIÈRE.LABEL,
    },
    {
      valeur: "terminale",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.TERMINALE.LABEL,
    },
  ];

  const bacOptions = bacs?.map((bac) => ({ valeur: bac.id, label: bac.nom }));

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    classeOptions,
    bacOptions,
    valeurBac,
    bacADesSpécialités: Boolean(spécialitésDuBac && spécialitésDuBac.length > 0),
    spécialitésSuggérées: spécialités?.map(spécialitéVersOptionSpécialité) ?? [],
    spécialitésSélectionnéesParDéfaut: spécialitésSélectionnéesParDéfaut?.map(spécialitéVersOptionSpécialité),
    rechercheSpécialitéEnCours,
    auChangementDesSpécialitésSélectionnées,
    àLaRechercheDUneSpécialité: setRechercheSpécialité,
  };
}
