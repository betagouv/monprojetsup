import { type ChampDeSaisieProps } from "./ChampDeSaisie.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { useId } from "react";

const ChampDeSaisie = ({
  label,
  description,
  status,
  icône,
  auChangement,
  obligatoire = false,
  registerHookForm,
  estChampDeRecherche,
}: ChampDeSaisieProps) => {
  const id = useId();

  const classEnFonctionDuStatus = () => {
    if (status?.type === "erreur")
      return { input: "fr-input--error", message: "fr-error-text", inputGroupe: "fr-input-group--error" };
    if (status?.type === "succès")
      return { input: "fr-input--valid", message: "fr-valid-text", inputGroupe: "fr-input-group--valid" };
    if (status?.type === "désactivé") return { input: "", message: "", inputGroupe: "fr-input-group--disabled" };

    return { input: "", message: "", inputGroupe: "" };
  };

  const propsInputJSX = {
    "aria-describedby": `status-${id}`,
    className: `fr-input ${classEnFonctionDuStatus().input}`,
    disabled: status?.type === "désactivé",
    id,
    name: id,
    onChange: auChangement,
    required: obligatoire ?? false,
  };

  const inputJSX = (
    <input
      type="text"
      {...propsInputJSX}
      {...registerHookForm}
    />
  );

  const inputSearchJSX = (
    <input
      onKeyDown={(événément) => {
        if (événément.key === "Enter") événément.preventDefault();
      }}
      type="search"
      {...propsInputJSX}
      {...registerHookForm}
    />
  );

  return (
    <div className={`fr-input-group ${classEnFonctionDuStatus().inputGroupe}`}>
      <label
        className="fr-label"
        htmlFor={id}
      >
        {label} {obligatoire && <span className="text-[--artwork-minor-red-marianne]">*</span>}
        {description && <span className="fr-hint-text">{description}</span>}
      </label>
      {estChampDeRecherche ? (
        <div
          className="fr-search-bar mt-2"
          id="header-search"
          role="search"
        >
          {inputSearchJSX}
          <button
            className="fr-btn"
            type="button"
          >
            {i18n.COMMUN.RECHERCHER}
          </button>
        </div>
      ) : icône ? (
        <div className={`fr-input-wrap ${icône}`}>{inputJSX}</div>
      ) : (
        inputJSX
      )}
      <div id={`status-${id}`}>
        {status && ["erreur", "succès"].includes(status.type) && (
          <p className={classEnFonctionDuStatus().message}>{status.message}</p>
        )}
      </div>
    </div>
  );
};

export default ChampDeSaisie;
