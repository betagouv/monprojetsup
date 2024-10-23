import { UseFormationsFavoritesFormationsFormArgs } from "./FormationsForm.interface";
import { SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { constantes } from "@/configuration/constantes";
import { Formation } from "@/features/formation/domain/formation.interface";
import {
  rechercherFormationsQueryOptions,
  récupérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useState } from "react";

export default function useFormationsFavoritesFormationsForm({
  setValue,
  getValues,
}: UseFormationsFavoritesFormationsFormArgs) {
  const [rechercheFormation, setRechercheFormation] = useState<string>();

  const formationVersOptionFormationFavorite = useCallback((formation: Formation): SélecteurMultipleOption => {
    return {
      valeur: formation.id,
      label: formation.nom,
    };
  }, []);

  const { data: formations, isFetching: rechercheEnCours } = useQuery({
    ...rechercherFormationsQueryOptions(rechercheFormation),
    enabled: Boolean(
      rechercheFormation && rechercheFormation?.length >= constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE,
    ),
  });

  const { data: formationsSélectionnéesParDéfaut } = useQuery(
    récupérerFormationsQueryOptions(
      getValues(constantes.FORMATIONS.CHAMP_FORMATIONS_FAVORITES)?.map((formation) => formation.id) ?? [],
    ),
  );

  const auChangementDesFormationsSélectionnées = (formationsSélectionnées: SélecteurMultipleOption[]) => {
    setValue(
      constantes.FORMATIONS.CHAMP_FORMATIONS_FAVORITES,
      formationsSélectionnées.map((formation) => ({
        id: formation.valeur,
        niveauAmbition: null,
        voeux: [],
        commentaire: null,
      })),
    );
  };

  return {
    formationsSuggérées: formations?.map(formationVersOptionFormationFavorite) ?? [],
    formationsSélectionnéesParDéfaut: formationsSélectionnéesParDéfaut?.map(formationVersOptionFormationFavorite),
    rechercheEnCours,
    auChangementDesFormationsSélectionnées,
    àLaRechercheDUneFormation: setRechercheFormation,
  };
}
