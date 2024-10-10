import { type SélecteurMultipleProps } from "./SélecteurMultiple.interface";
import useSélecteurMultiple from "./useSélecteurMultiple";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import ChampDeRecherche from "@/components/ChampDeRecherche/ChampDeRecherche";
import TagCliquable from "@/components/TagCliquable/TagCliquable";

const SélecteurMultiple = ({
  label,
  texteOptionsSélectionnées,
  optionsSuggérées,
  description,
  optionsSélectionnéesParDéfaut,
  auChangementOptionsSélectionnées,
  àLaRechercheDUneOption,
  rechercheSuggestionsEnCours,
  nombreDeCaractèreMinimumRecherche,
  forcerRafraichissementOptionsSélectionnées,
}: SélecteurMultipleProps) => {
  const {
    debouncedSetRecherche,
    statusChampDeSaisie,
    recherche,
    optionsAffichées,
    ajouterOptionSélectionnée,
    supprimerOptionSélectionnée,
    optionsSélectionnées,
  } = useSélecteurMultiple({
    nombreDeCaractèreMinimumRecherche,
    àLaRechercheDUneOption,
    auChangementOptionsSélectionnées,
    rechercheSuggestionsEnCours,
    optionsSélectionnéesParDéfaut,
    optionsSuggérées,
    forcerRafraichissementOptionsSélectionnées,
  });

  return (
    <>
      <ChampDeRecherche
        auChangement={(événement) => debouncedSetRecherche(événement.target.value ?? undefined)}
        entête={{ description, label }}
        obligatoire={false}
        status={statusChampDeSaisie}
      />
      {rechercheSuggestionsEnCours ? (
        <AnimationChargement />
      ) : (
        recherche &&
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

export default SélecteurMultiple;
