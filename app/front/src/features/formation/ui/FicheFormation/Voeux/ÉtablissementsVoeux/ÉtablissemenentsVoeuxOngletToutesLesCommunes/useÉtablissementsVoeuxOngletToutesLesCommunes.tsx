/* eslint-disable sonarjs/rules-of-hooks */
import { UseÉtablissementsVoeuxOngletToutesLesCommunesArgs } from "./ÉtablissemenentsVoeuxOngletToutesLesCommunes.interface";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { constantes } from "@/configuration/constantes";
import { type Formation } from "@/features/formation/domain/formation.interface";
import useÉtablissementsVoeux from "@/features/formation/ui/FicheFormation/Voeux/ÉtablissementsVoeux/useÉtablissementsVoeux";
import Fuse from "fuse.js";
import { useCallback, useMemo, useState } from "react";

export default function useÉtablissementsVoeuxOngletToutesLesCommunes({
  formation,
}: UseÉtablissementsVoeuxOngletToutesLesCommunesArgs) {
  const { mettreÀJourLesVoeux, voeuxSélectionnés } = useÉtablissementsVoeux({ formation });

  const [rechercheÉtablissement, setRechercheÉtablissement] = useState<string>("");

  const établissementVersOptionÉtablissement = useCallback(
    (établissement: Formation["établissements"][number]): SélecteurMultipleOption => ({
      valeur: établissement.id,
      label: établissement.nom,
    }),
    [],
  );

  const auChangementDesÉtablissementsSélectionnés = (établissementsSélectionnés: SélecteurMultipleOption[]) => {
    mettreÀJourLesVoeux(établissementsSélectionnés.map((établissement) => établissement.valeur));
  };

  const établissementsSélectionnésParDéfaut = useMemo(() => {
    return voeuxSélectionnés
      .map((voeuxSélectionné) => formation.établissements.find((élément) => élément.id === voeuxSélectionné))
      .filter((élément): élément is Formation["établissements"][number] => élément !== undefined)
      .map((établissement) => établissementVersOptionÉtablissement(établissement));
  }, [formation.établissements, voeuxSélectionnés, établissementVersOptionÉtablissement]);

  const établissementsSuggérés = useMemo<SélecteurMultipleOption[]>(() => {
    if (rechercheÉtablissement.length < constantes.ÉTABLISSEMENTS.NB_CARACTÈRES_MIN_RECHERCHE) return [];

    const fuse = new Fuse<Formation["établissements"][number]>(formation.établissements, {
      distance: 200,
      threshold: 0.2,
      keys: ["nom"],
    });

    return fuse
      .search(rechercheÉtablissement)
      .map((correspondance) => établissementVersOptionÉtablissement(correspondance.item));
  }, [formation, rechercheÉtablissement, établissementVersOptionÉtablissement]);

  return {
    auChangementDesÉtablissementsSélectionnés,
    établissementsSuggérés,
    voeuxSélectionnés,
    établissementsSélectionnésParDéfaut,
    àLaRechercheDUnÉtablissement: setRechercheÉtablissement,
  };
}
