/* eslint-disable react-hooks/rules-of-hooks */
import { i18n } from "@/configuration/i18n/i18n";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

export default function useÉtablissementsVoeux() {
  const { data: élève } = useQuery(élèveQueryOptions);

  const ongletToutesLesVilles = {
    titre: i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_VILLES.TITRE_ONGLET,
    contenu: i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_VILLES.RAPPEL,
  };

  // Tri par ordre alphabetique
  const ongletsParVilleFavorite =
    élève?.communesFavorites?.map((communeFavorite) => ({
      titre: communeFavorite.nom,
      contenu: communeFavorite.nom,
    })) ?? [];

  return {
    onglets: [...ongletsParVilleFavorite, ongletToutesLesVilles],
  };
}
