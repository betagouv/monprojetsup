import { UseVoeuxOngletToutesLesCommunesArgs } from "./VoeuxOngletToutesLesCommunes.interface";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { constantes } from "@/configuration/constantes";
import { type Formation } from "@/features/formation/domain/formation.interface";
import useVoeux from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/useVoeux";
import Fuse from "fuse.js";
import { useCallback, useMemo, useState } from "react";

export default function useVoeuxOngletToutesLesCommunes({ formation }: UseVoeuxOngletToutesLesCommunesArgs) {
  const { mettreÀJourLesVoeux, voeuxSélectionnés } = useVoeux({ formation });

  const [rechercheVoeu, setRechercheVoeu] = useState<string>("");

  const voeuVersOptionVoeu = useCallback(
    (voeu: Formation["voeux"][number]): SélecteurMultipleOption => ({
      valeur: voeu.id,
      label: voeu.nom,
    }),
    [],
  );

  const auChangementDesVoeuxSélectionnés = (nouveauxVoeuxSélectionnés: SélecteurMultipleOption[]) => {
    mettreÀJourLesVoeux(nouveauxVoeuxSélectionnés.map((voeu) => voeu.valeur));
  };

  const voeuxSélectionnésParDéfaut = useMemo(() => {
    return voeuxSélectionnés
      .map((voeuxSélectionné) => formation.voeux.find((élément) => élément.id === voeuxSélectionné.id))
      .filter((élément): élément is Formation["voeux"][number] => élément !== undefined)
      .map((voeu) => voeuVersOptionVoeu(voeu));
  }, [formation.voeux, voeuxSélectionnés, voeuVersOptionVoeu]);

  const voeuxSuggérés = useMemo<SélecteurMultipleOption[]>(() => {
    if (rechercheVoeu.length < constantes.VOEUX.NB_CARACTÈRES_MIN_RECHERCHE) return [];

    const fuse = new Fuse<Formation["voeux"][number]>(formation.voeux, {
      distance: 200,
      threshold: 0.2,
      keys: ["nom"],
    });

    return fuse.search(rechercheVoeu).map((correspondance) => voeuVersOptionVoeu(correspondance.item));
  }, [formation, rechercheVoeu, voeuVersOptionVoeu]);

  return {
    auChangementDesVoeuxSélectionnés,
    voeuxSuggérés,
    voeuxSélectionnés,
    voeuxSélectionnésParDéfaut,
    àLaRechercheDUnVoeu: setRechercheVoeu,
  };
}
