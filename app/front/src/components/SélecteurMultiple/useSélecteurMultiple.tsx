import { type SélecteurMultipleOption, type UseSélecteurMultipleArgs } from "./SélecteurMultiple.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { useEffect, useMemo, useState } from "react";
import { useDebounceCallback } from "usehooks-ts";

export default function useSélecteurMultiple({
  nombreDeCaractèreMinimumRecherche,
  àLaRechercheDUneOption,
  auChangementOptionsSélectionnées,
  rechercheSuggestionsEnCours,
  optionsSélectionnéesParDéfaut,
  optionsSuggérées,
  forcerRafraichissementOptionsSélectionnées,
}: UseSélecteurMultipleArgs) {
  const [recherche, setRecherche] = useState<string>();
  const [optionsAffichées, setOptionsAffichées] = useState<SélecteurMultipleOption[]>([]);
  const [optionsSélectionnées, setOptionsSélectionnées] = useState<SélecteurMultipleOption[]>(
    optionsSélectionnéesParDéfaut ?? [],
  );

  const debouncedSetRecherche = useDebounceCallback(setRecherche, 400);

  const statusChampDeSaisie = useMemo(() => {
    if (recherche && recherche.length < nombreDeCaractèreMinimumRecherche) {
      return {
        type: "erreur" as const,
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES} ${nombreDeCaractèreMinimumRecherche} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      };
    }

    if (recherche && optionsSuggérées.length === 0 && !rechercheSuggestionsEnCours)
      return { type: "erreur" as const, message: i18n.COMMUN.ERREURS_FORMULAIRES.AUCUN_RÉSULTAT };

    return undefined;
  }, [nombreDeCaractèreMinimumRecherche, optionsSuggérées, recherche, rechercheSuggestionsEnCours]);

  const supprimerOptionSélectionnée = (optionÀSupprimer: SélecteurMultipleOption) => {
    const nouvellesOptionsSélectionnées = optionsSélectionnées.filter(
      (option) => option.valeur !== optionÀSupprimer.valeur,
    );
    setOptionsSélectionnées(nouvellesOptionsSélectionnées);
    auChangementOptionsSélectionnées(nouvellesOptionsSélectionnées);
  };

  const ajouterOptionSélectionnée = (optionÀAjouter: SélecteurMultipleOption) => {
    const nouvellesOptionsSélectionnées = [...optionsSélectionnées, optionÀAjouter];
    setOptionsSélectionnées(nouvellesOptionsSélectionnées);
    auChangementOptionsSélectionnées(nouvellesOptionsSélectionnées);
  };

  useEffect(() => {
    if (recherche) {
      àLaRechercheDUneOption(recherche);
    }
  }, [recherche, àLaRechercheDUneOption]);

  useEffect(() => {
    const rechercheEstValide = recherche && recherche.length >= nombreDeCaractèreMinimumRecherche;

    if (rechercheEstValide) {
      const optionsÀAfficher = optionsSuggérées.filter(
        (optionSuggérée) =>
          !optionsSélectionnées.some((optionSélectionnée) => optionSélectionnée.valeur === optionSuggérée.valeur),
      );
      setOptionsAffichées(optionsÀAfficher);
    } else {
      setOptionsAffichées([]);
    }
  }, [nombreDeCaractèreMinimumRecherche, optionsSuggérées, optionsSélectionnées, recherche]);

  useEffect(() => {
    if (forcerRafraichissementOptionsSélectionnées) setOptionsSélectionnées(optionsSélectionnéesParDéfaut ?? []);
  }, [forcerRafraichissementOptionsSélectionnées, optionsSélectionnéesParDéfaut]);

  return {
    debouncedSetRecherche,
    statusChampDeSaisie,
    recherche,
    optionsAffichées,
    ajouterOptionSélectionnée,
    supprimerOptionSélectionnée,
    optionsSélectionnées,
  };
}
