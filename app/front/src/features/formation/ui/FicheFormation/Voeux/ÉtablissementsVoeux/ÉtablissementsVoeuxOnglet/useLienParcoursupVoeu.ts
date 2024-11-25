import { constantes } from "@/configuration/constantes";

export default function useLienParcoursupVoeu() {
  const creerUrlParcoursup = (idVoeu: string): string => {
    return (
      constantes.LIENS.FICHE_VOEU_PARCOURSUP +
      "?g_ta_cod=" +
      idVoeu.replaceAll(/\D/gu, "") +
      "&typeBac=" +
      0 + // TODO: typeBac
      "&originePc=0"
    );
  };

  return {
    creerUrlParcoursup,
  };
}
