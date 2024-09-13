import { type ChampDeRechercheFormulaireProps } from "./ChampDeRechercheFormulaire.interface";
import ChampDeSaisieSquelette from "@/components/_dsfr/ChampDeSaisieSquelette/ChampDeSaisieSquelette";
import useChampDeSaisieSquelette from "@/components/_dsfr/ChampDeSaisieSquelette/useChampDeSaisieSquelette";
import { i18n } from "@/configuration/i18n/i18n";
import { useId } from "react";

const ChampDeRechercheFormulaire = ({
  entête,
  àLaSoumission,
  auChangement,
  status,
  placeholder,
  obligatoire,
  registerHookForm,
}: ChampDeRechercheFormulaireProps) => {
  const id = useId();

  const { propsInput } = useChampDeSaisieSquelette({
    id,
    status,
    obligatoire,
    placeholder,
  });

  return (
    <ChampDeSaisieSquelette
      auChangement={auChangement}
      entête={entête}
      id={id}
      obligatoire={obligatoire}
      status={status}
    >
      <form
        onSubmit={(event) => {
          event.preventDefault();
          àLaSoumission((event.target as unknown as HTMLInputElement[])?.[0]?.value);
        }}
      >
        <div
          className="fr-search-bar mt-2"
          id="header-search"
          role="search"
        >
          <input
            type="search"
            {...propsInput}
            {...registerHookForm}
          />
          <button
            className="fr-btn"
            type="submit"
          >
            {i18n.COMMUN.RECHERCHER}
          </button>
        </div>
      </form>
    </ChampDeSaisieSquelette>
  );
};

export default ChampDeRechercheFormulaire;
