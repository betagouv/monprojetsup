/* eslint-disable react-hooks/rules-of-hooks */
import { type useÉtablissementsVoeuxArgs } from "./ÉtablissementsVoeux.interface";
import ÉtablissementsVoeuxOnglet from "./ÉtablissementsVoeuxOnglet/ÉtablissementsVoeuxOnglet";
import { i18n } from "@/configuration/i18n/i18n";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { trierTableauDObjetsParOrdreAlphabétique } from "@/utils/array";
import { useQuery } from "@tanstack/react-query";

export default function useÉtablissementsVoeux({ formation }: useÉtablissementsVoeuxArgs) {
  const { data: élève } = useQuery(élèveQueryOptions);

  const ongletToutesLesCommunes = {
    titre: i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_COMMUNES.TITRE_ONGLET,
    contenu: i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_COMMUNES.RAPPEL,
  };

  const ongletsParCommuneFavorite =
    élève?.communesFavorites?.map((communeFavorite) => {
      const établissements = formation.établissementsParCommuneFavorites.find(
        (élément) => élément.commune.code === communeFavorite.codeInsee,
      )?.établissements;

      return {
        titre: communeFavorite.nom,
        contenu: <ÉtablissementsVoeuxOnglet établissements={établissements ?? []} />,
      };
    }) ?? [];

  const ongletsTriés = trierTableauDObjetsParOrdreAlphabétique(ongletsParCommuneFavorite, "titre");

  return {
    onglets: ongletsTriés.length > 0 ? ongletsTriés : [ongletToutesLesCommunes],
  };
}
