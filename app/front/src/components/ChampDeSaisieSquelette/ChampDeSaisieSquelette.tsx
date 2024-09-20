import { type ChampDeSaisieSqueletteProps } from "./ChampDeSaisieSquelette.interface";
import useChampDeSaisieSquelette from "./useChampDeSaisieSquelette";

const ChampDeSaisieSquelette = ({
  id,
  entête: titre,
  status,
  auChangement,
  placeholder,
  obligatoire = false,
  children,
}: ChampDeSaisieSqueletteProps) => {
  const { classesCSS } = useChampDeSaisieSquelette({
    id,
    status,
    obligatoire,
    placeholder,
    auChangement,
  });

  return (
    <div className={`fr-input-group ${classesCSS.inputGroupe}`}>
      {"label" in titre ? (
        <label
          className="fr-label"
          htmlFor={id}
        >
          {titre.label} {obligatoire && <span className="text-[--artwork-minor-red-marianne]">*</span>}
          {titre.description && <span className="fr-hint-text">{titre.description}</span>}
        </label>
      ) : (
        <label
          className="sr-only"
          htmlFor={id}
        >
          {titre.labelAccessibilité}
        </label>
      )}

      {children}
      <div id={`status-${id}`}>
        {status && ["erreur", "succès"].includes(status.type) && <p className={classesCSS.message}>{status.message}</p>}
      </div>
    </div>
  );
};

export default ChampDeSaisieSquelette;
