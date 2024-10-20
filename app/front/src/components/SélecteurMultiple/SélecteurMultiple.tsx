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
  nombreDeSuggestionsMax,
  messageNbSuggestionsMaxDépassé,
  forcerRafraichissementOptionsSélectionnées,
}: SélecteurMultipleProps) => {
  const {
    debouncedSetRecherche,
    statusChampDeSaisie,
    recherche,
    optionsAffichées,
    ajouterOptionSélectionnée,
    supprimerOptionSélectionnée,
    nombreDeSuggestionsRestantes,
    optionsSélectionnées,
  } = useSélecteurMultiple({
    nombreDeCaractèreMinimumRecherche,
    àLaRechercheDUneOption,
    auChangementOptionsSélectionnées,
    rechercheSuggestionsEnCours,
    optionsSélectionnéesParDéfaut,
    optionsSuggérées,
    nombreDeSuggestionsMax,
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
          <div aria-live="polite">
            {messageNbSuggestionsMaxDépassé &&
              nombreDeSuggestionsMax &&
              nombreDeSuggestionsRestantes > nombreDeSuggestionsMax && (
                <p className="fr-text--sm mb-6">
                  {nombreDeSuggestionsRestantes} {messageNbSuggestionsMaxDépassé}
                </p>
              )}
            <ul
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
          </div>
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
