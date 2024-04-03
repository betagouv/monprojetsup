import { type ChampDeSaisieProps } from "./ChampDeSaisie.interface";

const ChampDeSaisie = ({ label, name, description, status, icône, registerHookForm }: ChampDeSaisieProps) => {
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
      aria-describedby={`status-${name}`}
      className={`fr-input ${classEnFonctionDuStatus().input}`}
      disabled={status?.type === "désactivé"}
      id={name}
      name={name}
      type="text"
      {...registerHookForm}
    />
  );

  return (
    <div className={`fr-input-group ${classEnFonctionDuStatus().inputGroupe}`}>
      <label
        className="fr-label"
        htmlFor={name}
      >
        {label}
        {description && <span className="fr-hint-text">{description}</span>}
      </label>
      {icône ? <div className={`fr-input-wrap ${icône}`}>{inputJSX}</div> : inputJSX}
      {status && ["erreur", "succès"].includes(status.type) && (
        <p
          className={classEnFonctionDuStatus().message}
          id={`status-${name}`}
        >
          {status.message}
        </p>
      )}
    </div>
  );
};

export default ChampDeSaisie;
