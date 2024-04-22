import { type SélecteurMultipleOption, type SélecteurMultipleProps } from "./SélecteurMultiple.interface";
import ChampDeSaisie from "@/components/_dsfr/ChampDeSaisie/ChampDeSaisie";
import TagCliquable from "@/components/_dsfr/TagCliquable/TagCliquable";
import Fuse from "fuse.js";
import { useEffect, useState } from "react";
import { useDebounceCallback } from "usehooks-ts";

const SélecteurMultiple = ({
  label,
  texteOptionsSélectionnées,
  options,
  description,
  status,
  valeursOptionsSélectionnéesParDéfaut,
  auChangementOptionsSélectionnées,
}: SélecteurMultipleProps) => {
  const [recherche, setRecherche] = useState<string>();
  const [optionsSuggérées, setOptionsSuggérées] = useState<SélecteurMultipleOption[]>([]);
  const [optionsSélectionnées, setOptionsSélectionnées] = useState<SélecteurMultipleOption[]>(
    options.filter((option) => valeursOptionsSélectionnéesParDéfaut?.includes(option.valeur)) ?? [],
  );

  const debouncedSetRecherche = useDebounceCallback(setRecherche, 150);

  const trouverOptionsLesPlusPertinentes = (texteRecherché: string, opts: SélecteurMultipleOption[]) => {
    const fuse = new Fuse(opts, {
      distance: 200,
      threshold: 0.4,
      keys: ["label"],
    });

    const correspondances = fuse.search(texteRecherché);
    return correspondances.map((correspondance) => correspondance.item);
  };

  const supprimerOptionSélectionnée = (optionÀSupprimer: SélecteurMultipleOption) => {
    const nouvellesOptionsSélectionnées = optionsSélectionnées.filter(
      (option) => option.valeur !== optionÀSupprimer.valeur,
    );
    setOptionsSélectionnées(nouvellesOptionsSélectionnées);
  };

  const ajouterOptionSélectionnée = (optionÀAjouter: SélecteurMultipleOption) => {
    const nouvellesOptionsSélectionnées = [...optionsSélectionnées, optionÀAjouter];
    setOptionsSélectionnées(nouvellesOptionsSélectionnées);
  };

  useEffect(() => {
    if (recherche) {
      const optionsSansLesOptionsSélectionnées = options.filter(
        (option) => !optionsSélectionnées.some((optionSélectionnée) => optionSélectionnée.valeur === option.valeur),
      );
      setOptionsSuggérées(trouverOptionsLesPlusPertinentes(recherche, optionsSansLesOptionsSélectionnées));
    } else {
      setOptionsSuggérées([]);
    }
  }, [options, optionsSélectionnées, recherche]);

  useEffect(() => {
    auChangementOptionsSélectionnées?.(optionsSélectionnées.map((opt) => opt.valeur));
  }, [auChangementOptionsSélectionnées, optionsSélectionnées]);

  return (
    <>
      <ChampDeSaisie
        auChangement={(événement) => debouncedSetRecherche(événement.target.value ?? undefined)}
        description={description}
        icône="fr-icon-search-line"
        label={label}
        status={status}
      />
      {optionsSuggérées.length > 0 && (
        <ul
          aria-live="polite"
          className="fr-tags-group mb-6 mt-2 list-none"
        >
          {optionsSuggérées.map((option) => (
            <li key={option.valeur}>
              <TagCliquable
                auClic={() => ajouterOptionSélectionnée(option)}
                libellé={option.label}
              />
            </li>
          ))}
        </ul>
      )}
      {optionsSélectionnées.length > 0 && (
        <>
          <p className="fr-text--xs mb-3">{texteOptionsSélectionnées}</p>
          <ul className="fr-tags-group list-none">
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
