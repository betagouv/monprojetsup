import { constantes } from "@/configuration/constantes.ts";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries.tsx";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries.tsx";
import { useQuery } from "@tanstack/react-query";

export default function useLienParcoursupVoeu() {
  const { data: élève } = useQuery(élèveQueryOptions);
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const idCarteParcoursup =
    référentielDonnées?.bacs.find((baccalaureat) => baccalaureat.id === élève?.bac)?.idCarteParcoursup ?? 0;

  const creerUrlParcoursup = (idVoeu: string): string => {
    return (
      constantes.LIENS.FICHE_VOEU_PARCOURSUP +
      "?g_ta_cod=" +
      idVoeu.replaceAll(/\D/gu, "") +
      "&typeBac=" +
      idCarteParcoursup +
      "&originePc=0"
    );
  };

  return {
    creerUrlParcoursup,
  };
}
