import { type ChampDeRechercheProps } from "./ChampDeRecherche.interface";
import ChampDeSaisieSquelette from "@/components/_dsfr/ChampDeSaisieSquelette/ChampDeSaisieSquelette";
import useChampDeSaisieSquelette from "@/components/_dsfr/ChampDeSaisieSquelette/useChampDeSaisieSquelette";
import { i18n } from "@/configuration/i18n/i18n";
import { useId } from "react";

const ChampDeRecherche = ({
  entête,
  status,
  placeholder,
  auChangement,
  obligatoire,
  registerHookForm,
}: ChampDeRechercheProps) => {
  const id = useId();

  const { propsInput } = useChampDeSaisieSquelette({
    id,
    status,
    obligatoire,
    placeholder,
    auChangement,
  });

  return (
    <ChampDeSaisieSquelette
      auChangement={auChangement}
      entête={entête}
      id={id}
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
