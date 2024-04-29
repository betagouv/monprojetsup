import {
  type SélecteurMultipleAvecAppelHttpOption,
  type SélecteurMultipleAvecAppelHttpProps,
} from "./SélecteurMultipleAvecAppelHttp.interface";
import ChampDeSaisie from "@/components/_dsfr/ChampDeSaisie/ChampDeSaisie";
import TagCliquable from "@/components/_dsfr/TagCliquable/TagCliquable";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import { useEffect, useState } from "react";
import { useDebounceCallback } from "usehooks-ts";

const SélecteurMultipleAvecAppelHttp = ({
  label,
  texteOptionsSélectionnées,
  optionsSuggérées,
  description,
  status,
  optionsSélectionnéesParDéfaut,
  auChangementOptionsSélectionnées,
  àLaRechercheDUneOption,
  rechercheMétiersEnCours,
}: SélecteurMultipleAvecAppelHttpProps) => {
  const [optionsAffichées, setOptionsAffichées] = useState<SélecteurMultipleAvecAppelHttpOption[]>([]);
  const [optionsSélectionnées, setOptionsSélectionnées] = useState<SélecteurMultipleAvecAppelHttpOption[]>(
    optionsSélectionnéesParDéfaut ?? [],
  );

  const debouncedSetRecherche = useDebounceCallback(àLaRechercheDUneOption, 150);

  const supprimerOptionSélectionnée = (optionÀSupprimer: SélecteurMultipleAvecAppelHttpOption) => {
    const nouvellesOptionsSélectionnées = optionsSélectionnées.filter(
      (option) => option.valeur !== optionÀSupprimer.valeur,
    );
    setOptionsSélectionnées(nouvellesOptionsSélectionnées);
    auChangementOptionsSélectionnées(nouvellesOptionsSélectionnées);
  };

  const ajouterOptionSélectionnée = (optionÀAjouter: SélecteurMultipleAvecAppelHttpOption) => {
    const nouvellesOptionsSélectionnées = [...optionsSélectionnées, optionÀAjouter];
    setOptionsSélectionnées(nouvellesOptionsSélectionnées);
    auChangementOptionsSélectionnées(nouvellesOptionsSélectionnées);
  };

  useEffect(() => {
    const optionsÀAfficher = optionsSuggérées.filter(
      (optionSuggérée) =>
        !optionsSélectionnées.some((optionSélectionnée) => optionSélectionnée.valeur === optionSuggérée.valeur),
    );

    setOptionsAffichées(optionsÀAfficher);
  }, [optionsSuggérées, optionsSélectionnées]);
  return (
    <>
      <ChampDeSaisie
        auChangement={(événement) => debouncedSetRecherche(événement.target.value ?? undefined)}
        description={description}
        icône="fr-icon-search-line"
        label={label}
        status={status}
      />
      {rechercheMétiersEnCours ? (
        <AnimationChargement />
      ) : (
        optionsAffichées.length > 0 && (
          <ul
            aria-live="polite"
            className="fr-tags-group mb-6 mt-2 list-none"
            data-testid="suggérées"
          >
            {optionsAffichées.map((option) => (
              <li key={option.valeur}>
                <TagCliquable
                  auClic={() => ajouterOptionSélectionnée(option)}
                  libellé={option.label}
                />
              </li>
            ))}
          </ul>
        )
      )}
      {optionsSélectionnées.length > 0 && (
        <>
          <p className="fr-text--xs mb-3">{texteOptionsSélectionnées}</p>
          <ul
            className="fr-tags-group list-none"
            data-testid="sélectionnées"
          >
            {optionsSélectionnées.map((option) => (
              <li key={option.valeur}>
                <TagCliquable
                  auClic={() => supprimerOptionSélectionnée(option)}
                  libellé={option.label}
                  supprimable
                />
              </li>
            ))}
          </ul>
        </>
      )}
    </>
  );
};

export default SélecteurMultipleAvecAppelHttp;
