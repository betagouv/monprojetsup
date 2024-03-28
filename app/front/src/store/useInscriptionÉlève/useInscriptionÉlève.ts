/* eslint-disable react-hooks/rules-of-hooks */
import { type InscriptionÉlèveStore } from "./useInscriptionÉlève.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { create } from "zustand";

const useInscriptionÉlèveStore = create<InscriptionÉlèveStore>((set, get) => ({
  étapes: [
    {
      titreÉtape: i18n.MON_PROJET.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.MON_PROJET.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/projet",
    },
    {
      titreÉtape: i18n.MA_SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.MA_SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/scolarite",
    },
    {
      titreÉtape: i18n.MES_ASPIRATIONS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.MES_ASPIRATIONS.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/aspirations",
    },
    {
      titreÉtape: i18n.MES_TALENTS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.MES_TALENTS.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/talents",
    },
    {
      titreÉtape: i18n.MES_MÉTIERS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.MES_MÉTIERS.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/metiers",
    },
    {
      titreÉtape: i18n.MES_ÉTUDES.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.MES_ÉTUDES.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/etudes",
    },
  ],
  indexÉtapeActuelle: undefined,
  étapeActuelle: undefined,
  étapeSuivante: undefined,
  étapePrécédente: undefined,
  actions: {
    définirÉtapeActuelle: (url: string) => {
      const étapeActuelle = get().étapes.find((étape) => url.endsWith(étape.url));
      const indexÉtapeActuelle = get().étapes.findIndex((étape) => étape.url === étapeActuelle?.url);

      set({
        indexÉtapeActuelle,
        étapeActuelle,
        étapeSuivante: get().étapes[indexÉtapeActuelle + 1],
        étapePrécédente: get().étapes[indexÉtapeActuelle - 1],
      });
    },
  },
}));

export const actionsInscriptionÉlèveStore = () => useInscriptionÉlèveStore((étatActuel) => étatActuel.actions);
export const étapesInscriptionÉlèveStore = () => useInscriptionÉlèveStore((étatActuel) => étatActuel.étapes);
export const indexÉtapeActuelleInscriptionÉlèveStore = () =>
  useInscriptionÉlèveStore((étatActuel) => étatActuel.indexÉtapeActuelle);
export const étapeActuelleInscriptionÉlèveStore = () =>
  useInscriptionÉlèveStore((étatActuel) => étatActuel.étapeActuelle);
export const étapeSuivanteInscriptionÉlèveStore = () =>
  useInscriptionÉlèveStore((étatActuel) => étatActuel.étapeSuivante);
export const étapePrécédenteInscriptionÉlèveStore = () =>
  useInscriptionÉlèveStore((étatActuel) => étatActuel.étapePrécédente);
