import { type ChampDeSaisieSqueletteProps } from "./ChampDeSaisieSquelette.interface";
import useChampDeSaisieSquelette from "./useChampDeSaisieSquelette";

const ChampDeSaisieSquelette = ({
  id,
  label,
  description,
  status,
  auChangement,
  obligatoire = false,
  children,
}: ChampDeSaisieSqueletteProps) => {
  const { classesCSS } = useChampDeSaisieSquelette({
    id,
    status,
    obligatoire,
    auChangement,
  });

  return (
    <div className={`fr-input-group ${classesCSS.inputGroupe}`}>
      <label
        className="fr-label"
        htmlFor={id}
      >
        {label} {obligatoire && <span className="text-[--artwork-minor-red-marianne]">*</span>}
        {description && <span className="fr-hint-text">{description}</span>}
      </label>
      {children}
      <div id={`status-${id}`}>
        {status && ["erreur", "succès"].includes(status.type) && <p className={classesCSS.message}>{status.message}</p>}
      </div>
    </div>
  );
};

export default ChampDeSaisieSquelette;
