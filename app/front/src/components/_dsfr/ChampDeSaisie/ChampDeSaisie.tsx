import { type ChampDeSaisieProps } from "./ChampDeSaisie.interface";
import { useId } from "react";

const ChampDeSaisie = ({ label, description, status, icône, auChangement, registerHookForm }: ChampDeSaisieProps) => {
  const id = useId();

  const classEnFonctionDuStatus = () => {
    if (status?.type === "erreur")
      return { input: "fr-input--error", message: "fr-error-text", inputGroupe: "fr-input-group--error" };
    if (status?.type === "succès")
      return { input: "fr-input--valid", message: "fr-valid-text", inputGroupe: "fr-input-group--valid" };
    if (status?.type === "désactivé") return { input: "", message: "", inputGroupe: "fr-input-group--disabled" };

    return { input: "", message: "", inputGroupe: "" };
  };

  const inputJSX = (
    <input
      aria-describedby={`status-${id}`}
      className={`fr-input ${classEnFonctionDuStatus().input}`}
      disabled={status?.type === "désactivé"}
      id={id}
      name={id}
      onChange={auChangement}
      type="text"
      {...registerHookForm}
    />
  );

  return (
    <div className={`fr-input-group ${classEnFonctionDuStatus().inputGroupe}`}>
      <label
        className="fr-label"
        htmlFor={id}
      >
        {label}
        {description && <span className="fr-hint-text">{description}</span>}
      </label>
      {icône ? <div className={`fr-input-wrap ${icône}`}>{inputJSX}</div> : inputJSX}
      {status && ["erreur", "succès"].includes(status.type) && (
        <p
          className={classEnFonctionDuStatus().message}
          id={`status-${id}`}
        >
          {status.message}
        </p>
      )}
    </div>
  );
};

export default ChampDeSaisie;
