import {
  CommuneÉlève,
  FormationÉlève,
  MétierÉlève,
  SpécialitéÉlève,
  VoeuÉlève,
} from "@/features/élève/domain/élève.interface";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useÉlève() {
  const { data: élève } = useQuery(élèveQueryOptions);

  const aAssociéSonCompteParcoursup = useMemo(
    (): boolean => élève?.compteParcoursupAssocié ?? false,
    [élève?.compteParcoursupAssocié],
  );

  const auMoinsUnMétierFavori = useMemo(
    (): boolean => (élève?.métiersFavoris && élève?.métiersFavoris?.length > 0) ?? false,
    [élève?.métiersFavoris],
  );

  const auMoinsUneFormationFavorite = useMemo(
    (): boolean => (élève?.formations && élève?.formations?.length > 0) ?? false,
    [élève?.formations],
  );

  const auMoinsUnDomaineFavori = useMemo(
    (): boolean => (élève?.domaines && élève?.domaines?.length > 0) ?? false,
    [élève?.domaines],
  );

  const auMoinsUnCentreIntêretFavori = useMemo(
    (): boolean => (élève?.centresIntérêts && élève?.centresIntérêts?.length > 0) ?? false,
    [élève?.centresIntérêts],
  );

  const estMétierFavori = (idMétier: MétierÉlève): boolean => {
    return élève?.métiersFavoris?.includes(idMétier) ?? false;
  };

  const estFormationFavorite = (idFormation: FormationÉlève): boolean => {
    return élève?.formations?.includes(idFormation) ?? false;
  };

  const estFormationMasquée = (idFormation: FormationÉlève): boolean => {
    return élève?.formationsMasquées?.includes(idFormation) ?? false;
  };

  const estCommuneFavorite = (codeInsee: CommuneÉlève["codeInsee"]): boolean => {
    return élève?.communesFavorites?.some((communeFavorite) => communeFavorite.codeInsee === codeInsee) ?? false;
  };

  const estVoeuFavori = (idVoeu: VoeuÉlève["id"]): boolean => {
    return élève?.voeuxFavoris?.some((voeuFavori) => voeuFavori.id === idVoeu) ?? false;
  };

  const estVoeuFavoriProvenantDeParcoursup = (idVoeu: VoeuÉlève["id"]): boolean => {
    return élève?.voeuxFavoris?.find((voeuFavori) => voeuFavori.id === idVoeu)?.estParcoursup ?? false;
  };

  const estSpécialitéFavorite = (idSpécialité: SpécialitéÉlève): boolean => {
    return élève?.spécialités?.includes(idSpécialité) ?? false;
  };

  return {
    élève,
    élèveAAssociéSonCompteParcoursup: aAssociéSonCompteParcoursup,
    élèveAuMoinsUnMétierFavori: auMoinsUnMétierFavori,
    élèveAuMoinsUneFormationFavorite: auMoinsUneFormationFavorite,
    élèveAuMoinsUnDomaineFavori: auMoinsUnDomaineFavori,
    élèveAuMoinsUnCentreIntêretFavori: auMoinsUnCentreIntêretFavori,
    estMétierFavoriPourÉlève: estMétierFavori,
    estFormationFavoritePourÉlève: estFormationFavorite,
    estFormationMasquéePourÉlève: estFormationMasquée,
    estCommuneFavoritePourÉlève: estCommuneFavorite,
    estVoeuFavoriPourÉlève: estVoeuFavori,
    estVoeuFavoriProvenantDeParcoursupPourÉlève: estVoeuFavoriProvenantDeParcoursup,
    estSpécialitéFavoritePourÉlève: estSpécialitéFavorite,
  };
}
