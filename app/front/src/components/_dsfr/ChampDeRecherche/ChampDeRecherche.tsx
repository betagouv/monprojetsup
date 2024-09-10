import { type ChampDeRechercheProps } from "./ChampDeRecherche.interface";
import ChampDeSaisieSquelette from "@/components/_dsfr/ChampDeSaisieSquelette/ChampDeSaisieSquelette";
import useChampDeSaisieSquelette from "@/components/_dsfr/ChampDeSaisieSquelette/useChampDeSaisieSquelette";
import { i18n } from "@/configuration/i18n/i18n";
import { useId } from "react";

const ChampDeRecherche = ({
  label,
  description,
  status,
  auChangement,
  obligatoire,
  registerHookForm,
}: ChampDeRechercheProps) => {
  const id = useId();

  const { propsInput } = useChampDeSaisieSquelette({
    id,
    status,
    obligatoire,
    auChangement,
  });

  return (
    <ChampDeSaisieSquelette
      auChangement={auChangement}
      description={description}
      id={id}
      label={label}
      obligatoire={obligatoire}
      status={status}
    >
      <div
        className="fr-search-bar mt-2"
        id="header-search"
        role="search"
      >
        <input
          onKeyDown={(événément) => {
            if (événément.key === "Enter") événément.preventDefault();
          }}
          type="search"
          {...propsInput}
          {...registerHookForm}
        />
        <button
          className="fr-btn"
          type="button"
        >
          {i18n.COMMUN.RECHERCHER}
        </button>
      </div>
    </ChampDeSaisieSquelette>
  );
};

export default ChampDeRecherche;
